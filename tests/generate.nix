{writeShellApplication}: writeShellApplication {
    name = "generate-tests";
    runtimeInputs = [];
    text = builtins.readFile ./generate_tests.bash;
}