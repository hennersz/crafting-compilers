package net.morti.klox.scanner

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

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + lexeme.hashCode()
        result = 31 * result + literal.hashCode()
        result = 31 * result + line.hashCode()
        return result
    }
}
