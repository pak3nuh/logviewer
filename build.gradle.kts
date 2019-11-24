import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pt.pak3nuh.util"
version = "0.1"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.41"
    id("org.openjfx.javafxplugin") version "0.0.8"
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("no.tornado:tornadofx:1.7.19")
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

javafx {
    modules("javafx.controls")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClassName = "pt.pak3nuh.util.logviewer.LogViewerAppKt"
}

val run by tasks.getting(JavaExec::class) {
    systemProperties = System.getProperties()
            .filter { it.key.toString().startsWith("logviewer") }
            .map { Pair(it.key.toString(), it.value) }
            .toMap()
}