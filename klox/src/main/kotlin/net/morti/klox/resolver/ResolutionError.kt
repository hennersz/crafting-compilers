package net.morti.klox.resolver

import net.morti.klox.parser.ParseError
import net.morti.klox.scanner.Token

class ResolutionError(val token: Token, override val message: String) : Exception(message) {
    override fun equals(other: Any?): Boolean {
        if (other is ParseError) {
            return this.token == other.token && this.message == other.message
        }

        return false
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }

    override fun toString(): String {
        return "Token: $token, Message: $message"
    }
}
