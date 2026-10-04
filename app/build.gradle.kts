plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

javafx {
    version = "23.0.2"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":core"))
    implementation("io.github.mkpaz:atlantafx-base:2.1.0")
}

application {
    mainClass.set("it.frank.conformita.ConformitaLauncher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register<Copy>("copySchemaEditorJar") {
    dependsOn(":schema-editor-gef:shadowJar")
    from(project(":schema-editor-gef").tasks.named("shadowJar"))
    into(layout.buildDirectory.dir("install/conformita-3708/lib"))
    rename { "schema-editor-gef.jar" }
}

tasks.named("installDist") {
    dependsOn("copySchemaEditorJar")
}

tasks.named<JavaExec>("run") {
    dependsOn(":schema-editor-gef:shadowJar")
}
