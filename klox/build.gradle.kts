plugins {
    kotlin("jvm") version "1.9.20"
    application
    jacoco
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
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Generate>("generate") {
    outputDir.set(layout.buildDirectory.dir("generated/src/kotlin"))
}

tasks.compileKotlin {
    dependsOn(tasks.getByName("generate"))
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