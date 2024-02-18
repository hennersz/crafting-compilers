package net.morti.klox.scanner

class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int,
) {
    override fun toString(): String {
        return "$type $lexeme $literal $line"
    }

    override fun equals(other: Any?): Boolean {
        return (
            (other is Token) &&
                (other.type == type) &&
                (other.lexeme == lexeme) &&
                (other.literal == literal) &&
                (other.line == line)
        )
    }
}
