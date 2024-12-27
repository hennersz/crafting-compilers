{bats, klox, writeShellApplication, parallel}: writeShellApplication {
    name = "test-klox";
    runtimeInputs = [bats klox parallel];
    text = ''
        bats -j 8 "$ROOT/tests/test.bats" -o "$OUT_DIR" -T --report-formatter tap
    '';
}