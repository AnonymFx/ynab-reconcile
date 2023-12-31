plugins {
    id("java")
    id("application")
    id("org.graalvm.buildtools.native") version ("0.9.23")
}

group = "anonym.fx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("info.picocli:picocli:4.1.0")
    annotationProcessor("info.picocli:picocli-codegen:4.1.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "anonym.fx.YnabReconcile"
    val dependencies = configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

application {
    mainClass.set("anonym.fx.YnabReconcile")
}

graalvmNative {
    binaries {
        all {
            // Include resource bundle for OpenCSV
            // Include english local for YNAB CSV parsing and german locale for bank CSV parsing
            buildArgs("-H:IncludeResourceBundles=opencsv", "-H:IncludeLocales=en,de")
            resources.autodetect()
        }
    }
    toolchainDetection.set(false)
}
