package net.morti.klox.interpreter

import com.github.stefanbirkner.systemlambda.SystemLambda.*
import net.morti.generated.klox.parser.Expr
import net.morti.generated.klox.parser.Stmt
import net.morti.klox.resolver.Resolver
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.util.stream.Stream
import kotlin.test.assertEquals

class InterpreterTest {
    @TestFactory
    fun testPrograms(): Stream<DynamicTest> =
        Stream.of(
            Triple(
                "Test Print",
                arrayListOf(
                    Stmt.Print(Expr.Literal("test")),
                ),
                "test",
            ),
            Triple(
                "Test add",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.PLUS, "+", "", 1),
                            Expr.Literal(1.0),
                        ),
                    ),
                ),
                "2.0",
            ),
            Triple(
                "Test add strings",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal("a"),
                            Token(TokenType.PLUS, "+", "", 1),
                            Expr.Literal("b"),
                        ),
                    ),
                ),
                "ab",
            ),
            Triple(
                "Test subtract",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.MINUS, "-", "", 1),
                            Expr.Literal(1.0),
                        ),
                    ),
                ),
                "0.0",
            ),
            Triple(
                "Test multiply",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(2.0),
                            Token(TokenType.STAR, "*", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "6.0",
            ),
            Triple(
                "Test divide",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.SLASH, "/", "", 1),
                            Expr.Literal(2.0),
                        ),
                    ),
                ),
                "1.5",
            ),
            Triple(
                "Test greater than",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.GREATER, ">", "", 1),
                            Expr.Literal(2.0),
                        ),
                    ),
                ),
                "true",
            ),
            Triple(
                "Test greater or equal",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.GREATER_EQUAL, ">=", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "true",
            ),
            Triple(
                "Test less than",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.LESS, "<", "", 1),
                            Expr.Literal(2.0),
                        ),
                    ),
                ),
                "false",
            ),
            Triple(
                "Test less or equal",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.LESS_EQUAL, "<=", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "true",
            ),
            Triple(
                "Test equal",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.EQUAL_EQUAL, "==", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "true",
            ),
            Triple(
                "Test not equal",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(3.0),
                            Token(TokenType.BANG_EQUAL, "!=", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "false",
            ),
            Triple(
                "Test negative",
                arrayListOf(
                    Stmt.Print(
                        Expr.Unary(
                            Token(TokenType.MINUS, "-", "", 1),
                            Expr.Literal(3.0),
                        ),
                    ),
                ),
                "-3.0",
            ),
            Triple(
                "Test not",
                arrayListOf(
                    Stmt.Print(
                        Expr.Unary(
                            Token(TokenType.BANG, "!", "", 1),
                            Expr.Literal(false),
                        ),
                    ),
                ),
                "true",
            ),
            Triple(
                "Test not with number",
                arrayListOf(
                    Stmt.Print(
                        Expr.Unary(
                            Token(TokenType.BANG, "!", "", 1),
                            Expr.Literal(1.0),
                        ),
                    ),
                ),
                "false",
            ),
            Triple(
                "Test variable",
                arrayListOf(
                    Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(1.0)),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                ),
                "1.0",
            ),
            Triple(
                "Test variable no initialise",
                arrayListOf(
                    Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), null),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                ),
                "nil",
            ),
            Triple(
                "Test assign",
                arrayListOf(
                    Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(1.0)),
                    Stmt.Expression(Expr.Assign(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(2.0))),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                ),
                "2.0",
            ),
            Triple(
                "Test Block",
                arrayListOf(
                    Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(1.0)),
                    Stmt.Var(Token(TokenType.IDENTIFIER, "b", "b", 1), Expr.Literal(5.0)),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "b", "b", 1))),
                    Stmt.Block(
                        listOf(
                            Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(2.0)),
                            Stmt.Expression(Expr.Assign(Token(TokenType.IDENTIFIER, "b", "b", 1), Expr.Literal(4.0))),
                            Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                            Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "b", "b", 1))),
                        ),
                    ),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                    Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "b", "b", 1))),
                ),
                """
                1.0
                5.0
                2.0
                4.0
                1.0
                4.0
                """.trimIndent(),
            ),
            Triple(
                "Test if",
                arrayListOf(
                    Stmt.If(
                        Expr.Literal(true),
                        Stmt.Print(Expr.Literal("hello")),
                        Stmt.Print(Expr.Literal("bye")),
                    ),
                ),
                "hello",
            ),
            Triple(
                "Test else",
                arrayListOf(
                    Stmt.If(
                        Expr.Literal(false),
                        Stmt.Print(Expr.Literal("hello")),
                        Stmt.Print(Expr.Literal("bye")),
                    ),
                ),
                "bye",
            ),
            Triple(
                "Test no else",
                arrayListOf(
                    Stmt.If(
                        Expr.Literal(false),
                        Stmt.Print(Expr.Literal("hello")),
                        null,
                    ),
                ),
                "",
            ),
            Triple(
                "Test and",
                arrayListOf(
                    Stmt.Print(
                        Expr.Logical(
                            Expr.Literal(false),
                            Token(TokenType.AND, "and", null, 1),
                            Expr.Literal(2),
                        ),
                    ),
                ),
                "false",
            ),
            Triple(
                "Test or",
                arrayListOf(
                    Stmt.Print(
                        Expr.Logical(
                            Expr.Literal(null),
                            Token(TokenType.OR, "or", null, 1),
                            Expr.Literal(2.0),
                        ),
                    ),
                ),
                "2.0",
            ),
            Triple(
                "Test loop",
                arrayListOf(
                    Stmt.Var(Token(TokenType.IDENTIFIER, "a", "a", 1), Expr.Literal(0.0)),
                    Stmt.While(
                        Expr.Binary(
                            Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1)),
                            Token(TokenType.LESS, "<", null, 1),
                            Expr.Literal(3.0),
                        ),
                        Stmt.Block(
                            arrayListOf(
                                Stmt.Print(Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1))),
                                Stmt.Expression(
                                    Expr.Assign(
                                        Token(TokenType.IDENTIFIER, "a", "a", 1),
                                        Expr.Binary(
                                            Expr.Variable(Token(TokenType.IDENTIFIER, "a", "a", 1)),
                                            Token(TokenType.PLUS, "+", null, 1),
                                            Expr.Literal(1.0),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
                """
                0.0
                1.0
                2.0
                """.trimIndent(),
            ),
            Triple(
                "Test Function",
                arrayListOf(
                    Stmt.Function(
                        Token(TokenType.IDENTIFIER, "test", "test", 1),
                        Expr.Function(
                            ArrayList<Token>(),
                            arrayListOf(
                                Stmt.Print(
                                    Expr.Literal(2.0),
                                ),
                            ),
                        ),
                    ),
                    Stmt.Expression(
                        Expr.Call(
                            Expr.Variable(Token(TokenType.IDENTIFIER, "test", "test", 1)),
                            Token(TokenType.LEFT_PAREN, "(", "(", 1),
                            ArrayList<Expr>(),
                        ),
                    ),
                ),
                "2.0",
            ),
        ).map { (name, statements, expected) ->
            dynamicTest(name) {
                val interpreter = Interpreter()
                val resolver = Resolver(interpreter)
                val errors = resolver.resolve(statements)
                assert(errors.isEmpty())
                val output =
                    tapSystemOut {
                        interpreter.interpret(statements)
                    }

                assertEquals(expected, output.trim())
            }
        }

    @TestFactory
    fun testErrors(): Stream<DynamicTest> =
        Stream.of(
            Triple(
                "Test plus bad operands",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.PLUS, "+", "", 1),
                            Expr.Literal("hello"),
                        ),
                    ),
                ),
                RuntimeError(Token(TokenType.PLUS, "+", "", 1), "Operands must be 2 numbers or 2 strings"),
            ),
            Triple(
                "Test negative string",
                arrayListOf(
                    Stmt.Print(
                        Expr.Unary(
                            Token(TokenType.MINUS, "-", "", 1),
                            Expr.Literal("hello"),
                        ),
                    ),
                ),
                RuntimeError(Token(TokenType.MINUS, "-", "", 1), "Operand must be a number"),
            ),
            Triple(
                "Test minus bad operands",
                arrayListOf(
                    Stmt.Print(
                        Expr.Binary(
                            Expr.Literal(1.0),
                            Token(TokenType.MINUS, "-", "", 1),
                            Expr.Literal("hello"),
                        ),
                    ),
                ),
                RuntimeError(Token(TokenType.MINUS, "-", "", 1), "Operands must be numbers"),
            ),
        ).map { (name, statements, expectedError) ->
            dynamicTest(name) {
                val actualError =
                    assertThrows<RuntimeError> {
                        Interpreter().interpret(statements)
                    }

                assertEquals(expectedError, actualError)
            }
        }
}
