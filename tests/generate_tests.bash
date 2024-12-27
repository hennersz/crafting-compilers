cp "$ROOT"/tests/template.bats "$ROOT"/tests/test.bats

for file in "$ROOT"/tests/resources/*.lox ; do
    filename="$(basename -- "$file")"
    filename="${filename%.*}"

    echo -n """
@test \"$filename\" {
  run_file \"$filename\"
}
""" >> "$ROOT/tests/test.bats"
done