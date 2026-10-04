plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

val swtVersion = "3.129.0"

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.eclipse.platform:org.eclipse.swt"))
            .using(module("org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:$swtVersion"))
            .because("Windows SWT native bundle")
    }
}

dependencies {
    implementation(project(":core"))
    implementation("org.eclipse.platform:org.eclipse.swt.win32.win32.x86_64:$swtVersion")
    implementation("org.eclipse.platform:org.eclipse.jface:3.34.0")
    implementation("com.googlecode.sarasvati.thirdparty.eclipse:draw2d:3.8.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

application {
    mainClass.set("it.frank.conformita.gef.GefSchemaEditorMain")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("schema-editor-gef")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
    manifest {
        attributes("Main-Class" to "it.frank.conformita.gef.GefSchemaEditorMain")
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}
