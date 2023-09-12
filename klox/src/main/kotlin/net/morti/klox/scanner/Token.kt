package net.morti.klox.scanner

class Token(
    private val type: TokenType,
    private val lexeme: String,
    private val literal: Any?,
    private val line: Int
) {
    override fun toString(): String {
        return "$type $lexeme $literal $line"
    }

    override fun equals(other: Any?): Boolean {
        return ((other is Token)
                && (other.type == type)
                && (other.lexeme == lexeme)
                && (other.literal == literal)
                && (other.line == line))
    }
}