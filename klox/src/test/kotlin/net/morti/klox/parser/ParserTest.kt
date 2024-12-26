package net.morti.klox.parser

import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun testSingleStatements() {
        val testCases =
            arrayListOf(
                Pair(
                    arrayListOf(
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Var(
                        Token(IDENTIFIER, "a", null, 1),
                        null,
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Var(
                        Token(IDENTIFIER, "a", null, 1),
                        Expr.Literal(1),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(PRINT, "print", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Print(
                        Expr.Literal(1),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Assign(
                            Token(IDENTIFIER, "a", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(EQUAL_EQUAL, "==", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(EQUAL_EQUAL, "==", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(GREATER_EQUAL, ">=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(GREATER_EQUAL, ">=", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(PLUS, "+", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(STAR, "*", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(STAR, "*", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(MINUS, "-", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Unary(
                            Token(MINUS, "-", null, 1),
                            Expr.Literal(1),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(STAR, "*", null, 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(STAR, "*", null, 1),
                            Expr.Grouping(
                                Expr.Binary(
                                    Expr.Literal(1),
                                    Token(PLUS, "+", null, 1),
                                    Expr.Literal(1),
                                ),
                            ),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "2", 2, 1),
                        Token(STAR, "*", null, 1),
                        Token(NUMBER, "3", 3, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(PLUS, "+", null, 1),
                            Expr.Binary(
                                Expr.Literal(2),
                                Token(STAR, "*", null, 1),
                                Expr.Literal(3),
                            ),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(NUMBER, "1", 1, 1),
                        Token(STAR, "*", null, 1),
                        Token(NUMBER, "2", 2, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "3", 3, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Binary(
                            Expr.Binary(
                                Expr.Literal(1),
                                Token(STAR, "*", null, 1),
                                Expr.Literal(2),
                            ),
                            Token(PLUS, "+", null, 1),
                            Expr.Literal(3),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(LEFT_BRACE, "{", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(RIGHT_BRACE, "}", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Block(
                        arrayListOf(
                            Stmt.Expression(
                                Expr.Binary(
                                    Expr.Literal(1),
                                    Token(PLUS, "+", null, 1),
                                    Expr.Literal(1),
                                ),
                            ),
                            Stmt.Var(
                                Token(IDENTIFIER, "a", null, 1),
                                Expr.Literal(1),
                            ),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(IF, "if", null, 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(PRINT, "print", null, 1),
                        Token(STRING, "hello", "hello", 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.If(
                        Expr.Literal(true),
                        Stmt.Print(Expr.Literal("hello")),
                        null,
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(IF, "if", null, 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(PRINT, "print", null, 1),
                        Token(STRING, "hello", "hello", 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(ELSE, "else", null, 1),
                        Token(PRINT, "print", null, 1),
                        Token(STRING, "bye", "bye", 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.If(
                        Expr.Literal(true),
                        Stmt.Print(Expr.Literal("hello")),
                        Stmt.Print(Expr.Literal("bye")),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(TRUE, "true", null, 1),
                        Token(AND, "and", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Logical(
                            Expr.Literal(true),
                            Token(AND, "and", null, 1),
                            Expr.Literal(true),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(TRUE, "true", null, 1),
                        Token(OR, "or", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Logical(
                            Expr.Literal(true),
                            Token(OR, "or", null, 1),
                            Expr.Literal(true),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(TRUE, "true", null, 1),
                        Token(AND, "and", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(OR, "or", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Logical(
                            Expr.Logical(
                                Expr.Literal(true),
                                Token(AND, "and", null, 1),
                                Expr.Literal(true),
                            ),
                            Token(OR, "or", null, 1),
                            Expr.Literal(true),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(TRUE, "true", null, 1),
                        Token(OR, "or", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(AND, "and", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Expression(
                        Expr.Logical(
                            Expr.Literal(true),
                            Token(OR, "or", null, 1),
                            Expr.Logical(
                                Expr.Literal(true),
                                Token(AND, "and", null, 1),
                                Expr.Literal(true),
                            ),
                        ),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(WHILE, "while", null, 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(TRUE, "true", null, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(PRINT, "print", null, 1),
                        Token(STRING, "hello", "hello", 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.While(
                        Expr.Literal(true),
                        Stmt.Print(Expr.Literal("hello")),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(FOR, "for", null, 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(PRINT, "print", null, 1),
                        Token(STRING, "hello", "hello", 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.While(
                        Expr.Literal(true),
                        Stmt.Print(Expr.Literal("hello")),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(FUN, "fun", null, 1),
                        Token(IDENTIFIER, "test", "test", 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(LEFT_BRACE, "{", null, 1),
                        Token(RIGHT_BRACE, "}", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Function(
                        Token(IDENTIFIER, "test", "test", 1),
                        Expr.Function(ArrayList<Token>(), ArrayList<Stmt>()),
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(FUN, "fun", null, 1),
                        Token(IDENTIFIER, "test", "test", 1),
                        Token(LEFT_PAREN, "(", null, 1),
                        Token(IDENTIFIER, "a", "a", 1),
                        Token(RIGHT_PAREN, ")", null, 1),
                        Token(LEFT_BRACE, "{", null, 1),
                        Token(RIGHT_BRACE, "}", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    Stmt.Function(
                        Token(IDENTIFIER, "test", "test", 1),
                        Expr.Function(arrayListOf(Token(IDENTIFIER, "a", "a", 1)), ArrayList<Stmt>()),
                    ),
                ),
            )

        for (testCase in testCases) {
            val (tokens, expected) = testCase
            val (statements, errors) = Parser(tokens).parse()

            assert(errors.isEmpty()) { "$errors" }
            assertEquals(1, statements.size)
            assertEquals(expected, statements[0])
        }
    }

    @Test
    fun testParseErrors() {
        val testCases =
            arrayListOf(
                Pair(
                    arrayListOf(
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(EOF, "", null, 1),
                    ),
                    ParseError(
                        Token(EOF, "", null, 1),
                        "Expect ';' after variable declaration.",
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(RETURN, "return", null, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    ParseError(
                        Token(RETURN, "return", null, 1),
                        "expected expression.",
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(FALSE, "false", false, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    ParseError(
                        Token(EQUAL, "=", null, 1),
                        "Invalid assignment target.",
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(LEFT_BRACE, "{", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(PLUS, "+", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(VAR, "var", null, 1),
                        Token(IDENTIFIER, "a", null, 1),
                        Token(EQUAL, "=", null, 1),
                        Token(NUMBER, "1", 1, 1),
                        Token(SEMICOLON, ";", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    ParseError(
                        Token(EOF, "", null, 1),
                        "Expected '}' after block.",
                    ),
                ),
                Pair(
                    arrayListOf(
                        Token(FUN, "fun", null, 1),
                        Token(EOF, "", null, 1),
                    ),
                    ParseError(
                        Token(EOF, "", null, 1),
                        "Expect '(' after lambda name.",
                    ),
                ),
            )

        val test = ArrayList<Token>(256)
        for (testCase in testCases) {
            val (tokens, expected) = testCase
            val (statements, errors) = Parser(tokens).parse()

            assert(statements.isEmpty())
            assertEquals(1, errors.size)
            assertEquals(expected, errors[0])
        }
    }

    @Test
    fun testTooManyArgs() {
        val tokens =
            arrayListOf(
                Token(FUN, "fun", null, 1),
                Token(IDENTIFIER, "test", "test", 1),
                Token(LEFT_PAREN, "(", null, 1),
            )
        tokens.addAll(
            arrayListOf(
                Token(IDENTIFIER, "a", "a", 1),
                Token(RIGHT_PAREN, ")", null, 1),
                Token(LEFT_BRACE, "{", null, 1),
                Token(PRINT, "print", null, 1),
                Token(STRING, "hello", "hello", 1),
                Token(SEMICOLON, ";", null, 1),
                Token(RIGHT_BRACE, "}", null, 1),
                Token(IDENTIFIER, "test", "test", 1),
                Token(LEFT_PAREN, "(", null, 1),
            ),
        )
        tokens.addAll(
            (0..254)
                .map { i ->
                    Pair(
                        Token(IDENTIFIER, "param$i", "param$i", 1),
                        Token(COMMA, ",", ",", 1),
                    )
                }.flatMap { (k, v) -> listOf(k, v) },
        )
        tokens.addAll(
            arrayListOf(
                Token(IDENTIFIER, "a", "a", 1),
                Token(RIGHT_PAREN, ")", null, 1),
                Token(SEMICOLON, ";", null, 1),
                Token(EOF, "", null, 1),
            ),
        )

        val (statements, errors) = Parser(tokens).parse()
        assertEquals(2, statements.size)
        assertEquals(1, errors.size)
        assertEquals(
            ParseError(
                Token(IDENTIFIER, "a", "a", 1),
                "Can't have more than 255 arguments.",
            ),
            errors[0],
        )
    }

    @Test
    fun testTooManyParams() {
        val tokens =
            arrayListOf(
                Token(FUN, "fun", null, 1),
                Token(IDENTIFIER, "test", "test", 1),
                Token(LEFT_PAREN, "(", null, 1),
            )
        tokens.addAll(
            (0..254)
                .map { i ->
                    Pair(
                        Token(IDENTIFIER, "param$i", "param$i", 1),
                        Token(COMMA, ",", ",", 1),
                    )
                }.flatMap { (k, v) -> listOf(k, v) },
        )
        tokens.addAll(
            arrayListOf(
                Token(IDENTIFIER, "a", "a", 1),
                Token(RIGHT_PAREN, ")", null, 1),
                Token(LEFT_BRACE, "{", null, 1),
                Token(RIGHT_BRACE, "}", null, 1),
                Token(EOF, "", null, 1),
            ),
        )

        val (statements, errors) = Parser(tokens).parse()
        assertEquals(1, statements.size)
        assertEquals(1, errors.size)
        assertEquals(
            ParseError(
                Token(IDENTIFIER, "a", "a", 1),
                "Can't have more than 255 parameters.",
            ),
            errors[0],
        )
    }

    @Test
    fun testSynchronize() {
        val tokens =
            arrayListOf(
                Token(VAR, "var", null, 1),
                Token(IDENTIFIER, "a", null, 1),
                Token(EQUAL, "=", null, 1),
                Token(RETURN, "return", null, 1),
                Token(VAR, "var", null, 2),
                Token(IDENTIFIER, "a", null, 2),
                Token(EQUAL, "=", null, 2),
                Token(NUMBER, "1", 1, 2),
                Token(SEMICOLON, ";", null, 2),
                Token(EOF, "", null, 2),
            )

        val expected =
            ParseError(
                Token(RETURN, "return", null, 1),
                "expected expression.",
            )
        val (statements, errors) = Parser(tokens).parse()
        assertEquals(1, statements.size)
        assertEquals(1, errors.size)
        assertEquals(expected, errors[0])
    }
}
