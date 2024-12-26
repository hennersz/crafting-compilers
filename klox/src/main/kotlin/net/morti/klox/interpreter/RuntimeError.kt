package net.morti.klox.interpreter

import net.morti.klox.scanner.Token
import java.util.Objects

class RuntimeError(
    val token: Token,
    message: String,
) : Exception(message) {
    override fun equals(other: Any?): Boolean {
        if (other is RuntimeError) {
            return this.token == other.token && this.message == other.message
        }

        return false
    }

    override fun hashCode(): Int = Objects.hash(token, message)

    override fun toString(): String = "Token: $token, Message: $message"
}
