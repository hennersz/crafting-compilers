package net.morti.klox.lox

import net.morti.generated.klox.parser.Expr
import net.morti.klox.scanner.Token
import net.morti.klox.scanner.TokenType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AstPrinterTest {
    @Test
    fun testPrint() {
        val printer = AstPrinter()
        val expression = Expr.Binary(
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(Expr.Literal(45.67))
        )
        assertEquals("(* (- 123) (group 45.67))", printer.print(expression))
    }
}