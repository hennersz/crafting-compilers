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
    description = "Hello World Application";
    longDescription = ''
      Not much to say here...
    '';
    sourceProvenance = with sourceTypes; [
      fromSource
      binaryBytecode
    ];
    platforms = platforms.unix;
  };
}