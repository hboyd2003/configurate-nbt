plugins {
    `java-library`
    `maven-publish`
    idea
}

repositories {
    mavenCentral()
    maven {
        name = "hboyd-dev-repo"
        url = uri("https://repo.hboyd.dev/snapshots/")
    }
}

dependencies {
    api(libs.jspecify)
    api(libs.bundles.adventureAPI)
    api(libs.configurateCore)

    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.bundles.junitJupiterRuntime)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val targetJavaVersion = 25
java {
    withJavadocJar()
    withSourcesJar()

    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}



publishing {
    repositories {
        maven {
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }

            name = "hboyd-dev-repo"
            url = uri("https://repo.hboyd.dev/" + (if (version.toString().contains("SNAPSHOT")) "snapshots/" else "releases/")
            )
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
