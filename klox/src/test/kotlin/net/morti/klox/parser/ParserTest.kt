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
        val testCases = arrayListOf(
            Pair(
                arrayListOf(
                    Token(VAR, "var", null, 1),
                    Token(IDENTIFIER, "a", null, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Var(
                    Token(IDENTIFIER, "a", null, 1),
                    null
                )
            ),
            Pair(
                arrayListOf(
                    Token(VAR, "var", null, 1),
                    Token(IDENTIFIER, "a", null, 1),
                    Token(EQUAL, "=", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Var(
                    Token(IDENTIFIER, "a", null, 1),
                    Expr.Literal(1)
                )
            ),
            Pair(
                arrayListOf(
                    Token(PRINT, "print", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Print(
                    Expr.Literal(1)
                )
            ),
            Pair(
                arrayListOf(
                    Token(IDENTIFIER, "a", null, 1),
                    Token(EQUAL, "=", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Assign(
                        Token(IDENTIFIER, "a", null, 1),
                        Expr.Literal(1)
                    )
                )
            ),
            Pair(
                arrayListOf(
                    Token(NUMBER, "1", 1, 1),
                    Token(EQUAL_EQUAL, "==", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(EQUAL_EQUAL, "==", null, 1),
                        Expr.Literal(1),
                    )
                )
            ),
            Pair(
                arrayListOf(
                    Token(NUMBER, "1", 1, 1),
                    Token(GREATER_EQUAL, ">=", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(GREATER_EQUAL, ">=", null, 1),
                        Expr.Literal(1),
                    )
                )
            ),
            Pair(
                arrayListOf(
                    Token(NUMBER, "1", 1, 1),
                    Token(PLUS, "+", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(PLUS, "+", null, 1),
                        Expr.Literal(1),
                    )
                )
            ),
            Pair(
                arrayListOf(
                    Token(NUMBER, "1", 1, 1),
                    Token(STAR, "*", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(STAR, "*", null, 1),
                        Expr.Literal(1),
                    )
                )
            ),
            Pair(
                arrayListOf(
                    Token(MINUS, "-", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), Stmt.Expression(
                    Expr.Unary(
                        Token(MINUS, "-", null, 1),
                        Expr.Literal(1),
                    )
                )
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
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(STAR, "*", null, 1),
                        Expr.Grouping(
                            Expr.Binary(
                                Expr.Literal(1),
                                Token(PLUS, "+", null, 1),
                                Expr.Literal(1),
                            )
                        ),
                    )
                )
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
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(PLUS, "+", null, 1),
                        Expr.Binary(
                            Expr.Literal(2),
                            Token(STAR, "*", null, 1),
                            Expr.Literal(3),
                        ),
                    )
                )
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
                ), Stmt.Expression(
                    Expr.Binary(
                        Expr.Binary(
                            Expr.Literal(1),
                            Token(STAR, "*", null, 1),
                            Expr.Literal(2),
                        ),
                        Token(PLUS, "+", null, 1),
                        Expr.Literal(3),
                    )
                )
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
                ), Stmt.Block(
                    arrayListOf(
                        Stmt.Expression(
                            Expr.Binary(
                                Expr.Literal(1),
                                Token(PLUS, "+", null, 1),
                                Expr.Literal(1),
                            )
                        ),
                        Stmt.Var(
                            Token(IDENTIFIER, "a", null, 1),
                            Expr.Literal(1)
                        )
                    )
                )
            )
        )

        for (testCase in testCases) {
            val (tokens, expected) = testCase
            val (statements, errors) = Parser(tokens).parse()

            assert(errors.isEmpty())
            assertEquals(1, statements.size)
            assertEquals(expected, statements[0])
        }
    }

    @Test
    fun testParseErrors() {
        val testCases = arrayListOf(
            Pair(
                arrayListOf(
                    Token(VAR, "var", null, 1),
                    Token(IDENTIFIER, "a", null, 1),
                    Token(EQUAL, "=", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(EOF, "", null, 1),
                ), ParseError(
                    Token(EOF, "", null, 1),
                    "Expect ';' after variable declaration."
                )
            ),
            Pair(
                arrayListOf(
                    Token(VAR, "var", null, 1),
                    Token(IDENTIFIER, "a", null, 1),
                    Token(EQUAL, "=", null, 1),
                    Token(RETURN, "return", null, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), ParseError(
                    Token(RETURN, "return", null, 1),
                    "expected expression."
                )
            ),
            Pair(
                arrayListOf(
                    Token(FALSE, "false", false, 1),
                    Token(EQUAL, "=", null, 1),
                    Token(NUMBER, "1", 1, 1),
                    Token(SEMICOLON, ";", null, 1),
                    Token(EOF, "", null, 1),
                ), ParseError(
                    Token(EQUAL, "=", null, 1),
                    "Invalid assignment target."
                )
            ),
        )

        for (testCase in testCases) {
            val (tokens, expected) = testCase
            val (statements, errors) = Parser(tokens).parse()

            assert(statements.isEmpty())
            assertEquals(1, errors.size)
            assertEquals(expected, errors[0])
        }
    }
}