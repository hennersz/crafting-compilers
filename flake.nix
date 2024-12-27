{
  description = "Dev environment for crafting compilers";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    devenv.url = "github:cachix/devenv";
    build-gradle-application.url = "github:raphiz/buildGradleApplication";
  };

  outputs = { self, nixpkgs, flake-utils, devenv, build-gradle-application, ... } @ inputs:
    flake-utils.lib.eachDefaultSystem (system:
      let
        javaVersion = 21;
        semver = nixpkgs.lib.strings.trim (builtins.readFile ./version);
        version = if (self ? dirtyShortRev) then "${semver}-${self.dirtyShortRev}" else semver;
        overlays = [
          (final: prev: rec {
            jdk = prev.graalvm-ce;
            java = jdk;
            gradle = prev.gradle.override { java = jdk; };
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
        devShells.default = devenv.lib.mkShell {
          inherit pkgs inputs;
          modules = [
            ({pkgs, ...}: {
              env.JAVA_HOME = "${jdk}";
              packages = [ kotlin jdk coreutils gradle updateVerificationMetadata self.packages."${system}".klox test-klox bats generate];
              enterShell = ''
                rm -rf $DEVENV_ROOT/.lib
                mkdir -p $DEVENV_ROOT/.lib
                ln -sf ${jdk} $DEVENV_ROOT/.lib/jdk
                export ROOT=$DEVENV_ROOT
                export OUT_DIR=$ROOT/tests/reports
              '';
            })
          ];
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