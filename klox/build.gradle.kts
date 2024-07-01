import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files

plugins {
    kotlin("jvm") version "1.9.23"
    application
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "net.morti"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets.main {
    java.srcDir("build/generated/src/kotlin")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Generate>("generate") {
    outputDir.set(layout.buildDirectory.dir("generated/src/kotlin"))
}

tasks.compileKotlin {
    dependsOn(tasks.getByName("generate"))
}

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn(tasks.getByName("generate"))
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
}
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
    }
}

kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode. See 7️⃣
}

application {
    mainClass.set("MainKt") // The main class of the application
}

abstract class Generate : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val dir = outputDir.get().asFile
        defineAst(
            dir,
            "Expr",
            listOf(
                "Binary   : Expr left, Token operator, Expr right",
                "Call     : Expr callee, Token paren, List<Expr> arguments",
                "Grouping : Expr expression",
                "Literal  : Any? value",
                "Unary    : Token operator, Expr right",
                "Variable : Token name",
                "Assign   : Token name, Expr value",
                "Logical  : Expr left, Token operator, Expr right",
                "Function : List<Token> params, List<Stmt> body",
            ),
        )

        defineAst(
            dir,
            "Stmt",
            listOf(
                "Expression : Expr expression",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr? value",
                "Var        : Token name, Expr? initializer",
                "Block      : List<Stmt> statements",
                "If         : Expr condition, Stmt thenBranch, Stmt? elseBranch",
                "While      : Expr condition, Stmt body",
                "Function   : Token name, Expr.Function function",
            ),
        )
    }

    private fun defineAst(
        outputDirectory: File,
        baseName: String,
        types: List<String>,
    ) {
        val outFile = File(outputDirectory, "net/morti/generated/klox/parser/$baseName.kt")
        Files.createDirectories(outFile.toPath().parent)
        val writer = PrintWriter(FileWriter(outFile, charset("UTF-8"), false))
        writer.print(
            """
package net.morti.generated.klox.parser

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

    abstract fun <R> accept(visitor: Visitor<R>): R?
}
        """.trim().replace(Regex("^\\s*$", RegexOption.MULTILINE), ""),
        )
        writer.println()
        writer.close()
    }

    private fun defineType(
        baseName: String,
        className: String,
        fieldList: String,
    ): String {
        val fields =
            fieldList.split(", ").map { field ->
                val t = field.split(" ")[0]
                val v = field.split(" ")[1]
                Pair(v, t)
            }

        return """
class $className(
${fields.joinToString("\n") { field -> "val ${field.first}: ${field.second},".prependIndent("    ") }}
) : $baseName() {
    override fun <R> accept(visitor: Visitor<R>): R? {
        return visitor.visit${className}$baseName(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other is $className) {
            return ${fields.joinToString(" && ") { field -> "this.${field.first} == other.${field.first}" }}
        }
        return false
    }
}
""".trim()
    }

    private fun defineVisitor(
        baseName: String,
        types: List<String>,
    ): String {
        return """
interface Visitor<R> {
${types.joinToString("\n") { type ->
            val typeName = type.split(":")[0].trim()
            "fun visit${typeName}$baseName(${baseName.lowercase()}: $typeName): R?".prependIndent("    ")
        }}
}
        """.trim()
    }
}
