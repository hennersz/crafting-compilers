package net.morti.klox.interpreter

import net.morti.klox.scanner.Token

class RuntimeError(val token: Token, message: String): Exception(message)