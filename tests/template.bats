#! /usr/bin/env bash

load "_common"

function setup() {
    export RESOURCES_DIR="$ROOT/tests/resources"
}

run_file() {
    run klox "$RESOURCES_DIR/${1}.lox"
    assert_output "$(cat "$RESOURCES_DIR/${1}.out")" 
    if test -f "$RESOURCES_DIR/${1}.ret"; then
        assert_failure "$(cat "$RESOURCES_DIR/${1}.ret")"
    fi
}
