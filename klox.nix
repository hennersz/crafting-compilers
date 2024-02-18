{ pkgs }:
with pkgs;
let
  deps = stdenv.mkDerivation {
    pname = "klox-deps";
    version = "1.0.2";
    src = ./klox;
    nativeBuildInputs = [perl jdk gradle];
    # run the same build as our main derivation to ensure we capture the correct set of dependencies
    buildPhase = ''
      export GRADLE_USER_HOME=$(mktemp -d)
      gradle --no-daemon installDist
    '';

    # take the cached dependencies and convert them into a maven repo structure
    installPhase = ''
      find $GRADLE_USER_HOME/caches/modules-2 -type f -regex '.*\.\(jar\|pom\)' \
        | LC_ALL=C sort \
        | perl -pe 's#(.*/([^/]+)/([^/]+)/([^/]+)/[0-9a-f]{30,40}/([^/\s]+))$# ($x = $2) =~ tr|\.|/|; "install -Dm444 $1 \$out/$x/$3/$4/$5" #e' \
        | sh

        pushd $out/org/jetbrains/kotlin/kotlin-gradle-plugin/1.9.22/ &>/dev/null
          cp -v ../../kotlin-gradle-plugin/1.9.22/kotlin-gradle-plugin-1.9.22-gradle82.jar kotlin-gradle-plugin-1.9.22.jar
        popd &>/dev/null
    '';

    # specify the content hash of this derivations output
    outputHashAlgo = "sha256";
    outputHashMode = "recursive";
    outputHash = "sha256-BWmF32KyayLOyGZDeS6u9lCYzQFCAgqte8KBFtLvElk=";
  };

    gradleInit = pkgs.writeText "init.gradle" ''
      logger.lifecycle 'Replacing Maven repositories with ${deps}...'
      gradle.projectsLoaded {
        rootProject.allprojects {
          buildscript {
            repositories {
              clear()
              maven { url '${deps}' }
            }
          }
          repositories {
            clear()
            maven { url '${deps}' }
          }
        }
      }
      settingsEvaluated { settings ->
        settings.pluginManagement {
          repositories {
            maven { url '${deps}' }
          }
        }
      }
    '';
in
stdenv.mkDerivation {
  pname = "klox";
  version = "1.0.0";
  src = ./klox;

  nativeBuildInputs = [jdk makeWrapper deps gradle];

  buildPhase = ''
    export GRADLE_USER_HOME=$(mktemp -d)
    gradle --stacktrace --offline --init-script ${gradleInit} --no-daemon installDist
  '';

  installPhase = ''
    mkdir -p $out/share/klox
    cp -r build/install/klox/* $out/share/klox
    makeWrapper $out/share/klox/bin/klox $out/bin/klox \
    --set JAVA_HOME ${jdk}
  '';
}