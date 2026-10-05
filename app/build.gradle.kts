plugins {

    application

    id("org.openjfx.javafxplugin") version "0.1.0"

}



java {

    toolchain {

        languageVersion.set(JavaLanguageVersion.of(22))

    }

}



val javafxVersion = "23.0.2"



javafx {

    version = javafxVersion

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



val javafxWinArtifacts = configurations.create("javafxWinCopy") {

    isCanBeResolved = true

    isCanBeConsumed = false

}



dependencies {

    // Classifier :win only; no transitive deps (they lack classifier and break variant resolution).

    javafxWinArtifacts("org.openjfx:javafx-base:$javafxVersion:win") {

        isTransitive = false

    }

    javafxWinArtifacts("org.openjfx:javafx-graphics:$javafxVersion:win") {

        isTransitive = false

    }

    javafxWinArtifacts("org.openjfx:javafx-controls:$javafxVersion:win") {

        isTransitive = false

    }

}



tasks.register<Copy>("copySchemaEditorGefJar") {

    dependsOn(":schema-editor-gef:shadowJar")

    from(project(":schema-editor-gef").tasks.named("shadowJar"))

    into(layout.buildDirectory.dir("install/conformita-3708/lib"))

    rename { "schema-editor-gef.jar" }

}



tasks.register<Copy>("copySchemaEditorJavafxJar") {

    dependsOn(":schema-editor-javafx:shadowJar")

    from(project(":schema-editor-javafx").tasks.named("shadowJar"))

    into(layout.buildDirectory.dir("install/conformita-3708/lib"))

    rename { "schema-editor-javafx.jar" }

}



tasks.register<Copy>("copyJavafxModules") {

    from(javafxWinArtifacts)

    into(layout.buildDirectory.dir("install/conformita-3708/lib/javafx"))

}



tasks.register<Copy>("syncJavafxModulesForDev") {

    from(javafxWinArtifacts)

    into(layout.buildDirectory.dir("javafx-modules"))

}



tasks.register("copySchemaEditorJars") {

    dependsOn("copySchemaEditorGefJar", "copySchemaEditorJavafxJar", "copyJavafxModules")

}



tasks.named("installDist") {

    dependsOn("copySchemaEditorJars")

}



tasks.register("runJavafx") {

    group = "application"

    description = "Runs the app with schema.editor=javafx (safe on PowerShell; no -P/-D flags)"

    dependsOn("run")

}



tasks.named<JavaExec>("run") {

    dependsOn(

        ":schema-editor-gef:shadowJar",

        ":schema-editor-javafx:shadowJar",

        "syncJavafxModulesForDev",

    )

    doFirst {

        val editor =

            if (gradle.taskGraph.allTasks.any { it.path == ":app:runJavafx" || it.name == "runJavafx" }) {

                "javafx"

            } else {

                sequenceOf(

                    findProperty("schema.editor")?.toString(),

                    System.getenv("SCHEMA_EDITOR"),

                    System.getProperty("schema.editor"),

                ).firstOrNull { !it.isNullOrBlank() } ?: "gef"

            }

        systemProperty("schema.editor", editor)

    }

}


