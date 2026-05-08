plugins {
    `java-library`
    `maven-publish`
    idea
    alias(libs.plugins.indra)
    alias(libs.plugins.indraPublishing)
    alias(libs.plugins.indraLicenserSpotless)
    alias(libs.plugins.indraCheckstyle)
}

repositories {
    mavenCentral()
    maven {
        name = "hboyd-dev-repo"
        url = uri("https://repo.hboyd.dev/snapshots/")
    }
}

dependencies {
    api(libs.bundles.adventureAPI)
    api(libs.configurateCore)
    api(libs.jspecify)
    api(libs.checkerFramework)
    api(libs.jetbrainsAnnotations)

    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.bundles.junitJupiterRuntime)
}

indra {
    javaVersions {
        target(25)
    }

    github("hboyd2003", "configurate-nbt") {
        ci(true)
        publishing(false)
    }

    publishReleasesTo("hboydDev", "https://repo.hboyd.dev/releases")
    publishSnapshotsTo("hboydDev", "https://repo.hboyd.dev/snapshots")

    lgpl3OrLaterLicense()

    checkstyle(libs.versions.checkstyle.get())

    signWithKeyFromPrefixedProperties("hboyd")

    configurePublications {
        pom {
            developers {
                developer {
                    id = "hboyd"
                    timezone = "America/New_York"
                }
            }
        }
    }
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file(".spotless/license_header_template.txt"))
    newLine(true)
}

spotless {
    java {
        removeUnusedImports()
        formatAnnotations()
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}