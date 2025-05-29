import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = libs.versions.library.namespace.get()
version = libs.versions.library.version.name.get()

private val _java = libs.versions.java.get()

kotlin {
    jvmToolchain(_java.toInt())
    jvm()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(group.toString(), "library", version.toString())
    pom {
        name = libs.versions.library.name.get()
        description = libs.versions.library.description.get()
        inceptionYear = libs.versions.library.inception.get()
        url = libs.versions.library.git.get()
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://opensource.org/licenses/Apache-2.0"
            }
        }
        developers {
            developer {
                name = "Alexey Utovka"
            }
        }
        scm {
            url = libs.versions.library.git.get()
            connection = "${libs.versions.library.git.get()}.git"
        }
    }
}
