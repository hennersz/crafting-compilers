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
        version = self.shortRev or "dirty";
        overlays = [
          (final: prev: rec {
            jdk = prev."jdk${toString javaVersion}";
            java = jdk;
            gradle = prev.gradle.override { java = jdk; };
            kotlin = prev.kotlin.override { jre = jdk; };
          })
          build-gradle-application.overlays.default
        ];

        pkgs = import nixpkgs {
          inherit system overlays;
        };
      in
      with pkgs;
      {
        devShells.default = devenv.lib.mkShell {
          inherit pkgs inputs;
          modules = [
            ({pkgs, ...}: {
              env.JAVA_HOME = "${jdk}/lib/openjdk";
              packages = [ kotlin jdk coreutils gradle updateVerificationMetadata ];
              enterShell = ''
                rm -rf $DEVENV_ROOT/.lib
                mkdir -p $DEVENV_ROOT/.lib
                ln -sf ${jdk}/lib/openjdk/ $DEVENV_ROOT/.lib/jdk
              ''; 
            })
          ];
        };

        packages = rec { 
          klox = pkgs.callPackage ./package.nix { inherit version; };
          default = klox;
        };
      }
    );
}