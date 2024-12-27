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

@test "anonymous" {
  run_file "anonymous"
}

@test "arithmetic" {
  run_file "arithmetic"
}

@test "badAssignment" {
  run_file "badAssignment"
}

@test "badConcat" {
  run_file "badConcat"
}

@test "badString" {
  run_file "badString"
}

@test "comparison" {
  run_file "comparison"
}

@test "conditional" {
  run_file "conditional"
}

@test "counter" {
  run_file "counter"
}

@test "fibonacci" {
  run_file "fibonacci"
}

@test "forLoop" {
  run_file "forLoop"
}

@test "function" {
  run_file "function"
}

@test "helloWorld" {
  run_file "helloWorld"
}

@test "ifElse" {
  run_file "ifElse"
}

@test "method" {
  run_file "method"
}

@test "nestedVars" {
  run_file "nestedVars"
}

@test "readLocal" {
  run_file "readLocal"
}

@test "redeclare" {
  run_file "redeclare"
}

@test "resolve" {
  run_file "resolve"
}

@test "sync" {
  run_file "sync"
}

@test "this" {
  run_file "this"
}

@test "topReturn" {
  run_file "topReturn"
}

@test "undefinedAssign" {
  run_file "undefinedAssign"
}

@test "undefinedVariable" {
  run_file "undefinedVariable"
}
