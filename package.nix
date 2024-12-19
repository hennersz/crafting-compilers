{
  lib,
  version,
  buildGradleApplication,
}:
buildGradleApplication {
  pname = "klox";
  version = version;
  src = ./klox;
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