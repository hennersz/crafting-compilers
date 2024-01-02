package net.morti.klox.parser

import net.morti.klox.scanner.Token

class ParseError(val token: Token, override val message: String): Exception(message)