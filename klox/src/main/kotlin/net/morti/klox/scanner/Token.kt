package net.morti.klox.scanner

import java.util.*

class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int,
) {
    override fun toString(): String = "$type $lexeme $literal $line"

    override fun equals(other: Any?): Boolean =
        (
            (other is Token) &&
                (other.type == type) &&
                (other.lexeme == lexeme) &&
                (other.literal == literal) &&
                (other.line == line)
        )

    override fun hashCode(): Int = Objects.hash(type, lexeme, literal, line)
}
