package net.morti.klox.lox

import net.morti.klox.scanner.Scanner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class Lox {
    private var hadError = false

    fun start(args: Array<String>): Int {
        if (args.size > 1) {
            println("Usage: jlox [script]")
            return 64
        } else if (args.size == 1) {
            return runFile(args[0])
        } else {
            runPrompt()
        }

        return 0
    }

    private fun runFile(path: String): Int {
        val bytes = Files.readAllBytes(Path.of(path))
        run(String(bytes, Charset.defaultCharset()))
        if (hadError) {
            return 65
        }
        return 0
    }

    private fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            println("> ")
            val line = reader.readLine() ?: break
            run(line)
            hadError = false
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val (tokens, scanErrors) = scanner.scanTokens()

        if (scanErrors.isNotEmpty()) {
            for (scanError in scanErrors) {
                error(scanError.line, scanError.message)
            }
        } else {
            for (token in tokens) {
                println(token)
            }
        }
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    private fun report (line: Int, where: String, message: String) {
        println("[line$line] Error$where: $message")
        hadError = true
    }
}
