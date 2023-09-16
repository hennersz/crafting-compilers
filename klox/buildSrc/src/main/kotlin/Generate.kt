import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files

abstract class Generate: DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val dir = outputDir.get().asFile
        defineAst(dir, "Expr", listOf(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Any value",
            "Unary    : Token operator, Expr right"
        ))
    }

    private fun defineAst(outputDirectory: File, baseName: String, types: List<String>) {
        val outFile = File(outputDirectory, "net/morti/klox/parser/$baseName.kt")
        Files.createDirectories(outFile.toPath().parent)
        val writer = PrintWriter(FileWriter(outFile, charset("UTF-8"), false))
        writer.print("""
package net.morti.klox.parser

import net.morti.klox.scanner.Token

abstract class $baseName {

${defineVisitor(baseName, types).prependIndent("    ")}

${
    types.joinToString("\n\n") { type ->
        val className = type.split(":")[0].trim()
        val fields = type.split(":")[1].trim()
        defineType(baseName, className, fields)
    }.prependIndent("    ")
}

    abstract fun <R> accept(visitor: Visitor<R>): R
}
        """.trim())
        writer.println()
        writer.close()
    }

    private fun defineType(baseName: String, className: String, fieldList: String): String {
        val fields = fieldList.split(", ").map{ field ->
            val t = field.split(" ")[0]
            val v = field.split(" ")[1]
            Pair(v, t)
        }

        return """
class $className(
${fields.joinToString(",\n") { field -> "val ${field.first}: ${field.second}".prependIndent("    ") }}
): $baseName() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit${className}${baseName}(this)
    }
}
""".trim()
    }

    private fun defineVisitor(baseName: String, types: List<String>): String {
        return """
interface Visitor<R> {
${types.joinToString("\n") { type -> 
    val typeName = type.split(":")[0].trim()
    "fun visit${typeName}${baseName}(expr: $typeName): R".prependIndent("    ")        
}}
}
        """.trim()
    }
}