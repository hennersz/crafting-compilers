package net.morti.klox.scanner

class ScanError(val message: String, val line: Int) {
    override fun toString(): String {
        return "Scan error: $message, on line $line"
    }

    override fun equals(other: Any?): Boolean {
        return (other is ScanError) &&
            (other.message == message) &&
            (other.line == line)
    }
}
