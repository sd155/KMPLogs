import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = libs.versions.library.group.get()
version = libs.versions.library.version.name.get()

private val _java = libs.versions.java.get()
private val _javaVersion = JavaVersion.toVersion(_java)
private val _jvmTarget = JvmTarget.fromTarget(_java)

kotlin {
    jvmToolchain(_java.toInt())

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(_jvmTarget)
        }
    }    
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
//    linuxX64()
//    jvm()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kmplogsApi)
                implementation(libs.gson)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
    }
}

android {
    namespace = libs.versions.library.namespace.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = _javaVersion
        targetCompatibility = _javaVersion
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(group.toString(), "kmplogs-core", version.toString())
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
