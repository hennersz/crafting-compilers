{
  description = "Dev environment for crafting compilers";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    devenv.url = "github:cachix/devenv";
  };

  outputs = { self, nixpkgs, flake-utils, devenv, ... } @ inputs:
    flake-utils.lib.eachDefaultSystem (system:
      let
        javaVersion = 20;

        overlays = [
          (final: prev: rec {
            jdk = prev."jdk${toString javaVersion}";
            gradle = prev.gradle.override { java = jdk; };
            kotlin = prev.kotlin.override { jre = jdk; };
          })
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
              env.FOO = ./.;
              packages = [ kotlin jdk coreutils ];
                enterShell = ''
                  rm -rf $DEVENV_ROOT/.lib
                  mkdir -p $DEVENV_ROOT/.lib
                  ln -sf ${jdk}/lib/openjdk/ $DEVENV_ROOT/.lib/jdk
                ''; 
            })
          ];
        };
      }
    );
}