plugins {
    id("java")
    id("application")
    id("org.graalvm.buildtools.native") version("0.9.23")
}

group = "anonym.fx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("anonym.fx.Main")
}

graalvmNative {
    binaries {
        all {
            resources.autodetect()
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(17))
                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
            })
        }
    }
    toolchainDetection.set(false)
}
