package net.morti.klox.lox

import net.morti.klox.interpreter.Interpreter
import net.morti.klox.interpreter.RuntimeError
import net.morti.klox.parser.Parser
import net.morti.klox.resolver.Resolver
import net.morti.klox.scanner.Scanner
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType
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
            print("> ")
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
            return
        }

        val parser = Parser(tokens)
        val (statements, parseErrors) = parser.parse()

        if (parseErrors.isNotEmpty()) {
            for (parseError in parseErrors) {
                error(parseError.token, parseError.message)
            }
            return
        }

        val interpreter = Interpreter()
        val resolver = Resolver(interpreter)
        val resolutionErrors = resolver.resolve(statements)

        if (resolutionErrors.isNotEmpty()) {
            for (resolutionError in resolutionErrors) {
                error(resolutionError.token, resolutionError.message)
            }
            return
        }

        try {
            interpreter.interpret(statements)
        } catch (e: RuntimeError) {
            error(e.token, e.message.orEmpty())
        }
    }

    private fun error(
        line: Int,
        message: String,
    ) {
        report(line, "", message)
    }

    private fun error(
        token: Token,
        message: String,
    ) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message)
        } else {
            report(token.line, " at '${token.lexeme}'", message)
        }
    }

    private fun report(
        line: Int,
        where: String,
        message: String,
    ) {
        println("[line$line] Error$where: $message")
        hadError = true
    }
}
