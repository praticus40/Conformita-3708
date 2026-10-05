plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

val javafxVersion = "23.0.2"

javafx {
    version = javafxVersion
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass.set("it.frank.conformita.javafx.schema.JavaFxSchemaEditorMain")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("schema-editor-javafx")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
    manifest {
        attributes("Main-Class" to "it.frank.conformita.javafx.schema.JavaFxSchemaEditorMain")
    }
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}
