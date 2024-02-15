package net.morti.klox.parser

import net.morti.klox.scanner.Token

class ParseError(val token: Token, override val message: String): Exception(message) {
    override fun equals(other: Any?): Boolean {
        if(other is ParseError) {
            return this.token == other.token && this.message == other.message
        }

        return false
    }
}