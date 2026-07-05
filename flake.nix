{
  description = "Dev environment for crafting compilers";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
#    devenv.url = "github:cachix/devenv";
    std-dev-env.url = "github:hennersz/std-dev-env";
    build-gradle-application.url = "github:raphiz/buildGradleApplication";
  };

  outputs = { self, nixpkgs, flake-utils, std-dev-env, build-gradle-application, ... } @ inputs:
    flake-utils.lib.eachDefaultSystem (system:
      let
        version = nixpkgs.lib.strings.trim (builtins.readFile ./version);
        overlays = [
          (final: prev: rec {
            jdk = prev.graalvmPackages.graalvm-ce;
            java = jdk;
            gradle = prev.gradle_9.override { java = jdk; };
            kotlin = prev.kotlin.override { jre = jdk; };
          })
          build-gradle-application.overlays.default
        ];

        pkgs = import nixpkgs {
          inherit system overlays;
        };

        test-klox = import ./tests/test.nix {
            inherit (pkgs) bats writeShellApplication parallel;
            inherit (self.packages."${system}") klox;
        };

        generate = import ./tests/generate.nix {
            inherit (pkgs) writeShellApplication ;
        };

        klox-check = pkgs.runCommandLocal "klox-check" rec {
            src = ./.;
            nativeBuildInputs = with pkgs; [test-klox];
        } ''
          export ROOT=$src
          export OUT_DIR=$out
          test-klox
        '';
      in
      with pkgs;
      {
        devShells.default = std-dev-env.lib.base.devenv {
          inherit pkgs inputs;
          env.JAVA_HOME = "${jdk}";
          packages = [ kotlin jdk coreutils gradle updateVerificationMetadata self.packages."${system}".klox test-klox bats generate git convco ];
          scripts = {
            tests.exec = ''
                unit-tests
                integration-tests
            '';

            unit-tests.exec = ''
                ROOT_DIR="${PROJECT_ROOT:-$PWD}"
                gradle -p "$ROOT_DIR/klox" test
            '';

            integration-tests.exec = ''
                ROOT_DIR="${PROJECT_ROOT:-$PWD}"
                REPORT_DIR="${OUT_DIR:-$ROOT_DIR/tests/reports}"
                mkdir -p "$REPORT_DIR"
                export ROOT="$ROOT_DIR"
                export OUT_DIR="$REPORT_DIR"
                test-klox
            '';

            lint.exec = ''
                ROOT_DIR="${PROJECT_ROOT:-$PWD}"
                gradle -p "$ROOT_DIR/klox" ktlintCheck
            '';

            release.exec = ''
                ROOT_DIR="${PROJECT_ROOT:-$PWD}"
                convco -C "$ROOT_DIR" version --bump | tr -d [:space:] > "$ROOT_DIR/version"
                convco -C "$ROOT_DIR" changelog --unreleased v$(cat "$ROOT_DIR/version") > "$ROOT_DIR/CHANGELOG.md"
                git -C "$ROOT_DIR" add .
                git -C "$ROOT_DIR" commit -m "chore(release): $(cat "$ROOT_DIR/version")"
                git -C "$ROOT_DIR" tag v$(cat "$ROOT_DIR/version") -m "chore(release): $(cat "$ROOT_DIR/version")"
            '';
          };
          enterShell = ''
            ROOT_DIR="${PROJECT_ROOT:-$PWD}"
            rm -rf "$ROOT_DIR/.lib"
            mkdir -p "$ROOT_DIR/.lib"
            ln -sf ${jdk} "$ROOT_DIR/.lib/jdk"
            export ROOT="$ROOT_DIR"
            export OUT_DIR="${OUT_DIR:-$ROOT_DIR/tests/reports}"
            mkdir -p "$OUT_DIR"
            git -C "$ROOT_DIR" submodule update --init
          '';
        };

        packages = rec { 
          klox = pkgs.callPackage ./package.nix { inherit version; };
          klox-native = (pkgs.callPackage ./package.nix { inherit version; buildTask = "nativeCompile";}).overrideDerivation (oldAttrs: {
            installPhase = ''
              runHook preInstall
              pushd build/native/nativeCompile
              mkdir -p $out/bin
              cp klox $out/bin/klox
              popd
              runHook postInstall
            '';
           });
          default = klox;
        };

        checks = { inherit klox-check;};
      }
    );
}
