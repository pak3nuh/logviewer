import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pt.pak3nuh.util"

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
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


application {
    mainClassName = "pt.pak3nuh.util.logviewer.AppKt"
}