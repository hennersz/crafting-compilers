{
  lib,
  version,
  buildGradleApplication,
  pkgs
}:
buildGradleApplication {
  pname = "klox";
  version = version;
  src = ./klox;
  nativeBuildInputs = [pkgs.libgcc];
  meta = with lib; {
    description = "A lox interpreter written in kotlin";
    longDescription = ''
        An implementation of a lox interpreter, written in kotlin and
        based on the book https://craftinginterpreters.com
    '';
    sourceProvenance = with sourceTypes; [
      fromSource
      binaryBytecode
    ];
    platforms = platforms.unix;
  };
}