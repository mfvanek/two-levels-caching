import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java-library")
    id("jacoco")
    id("maven-publish")
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs") version "6.0.24"
    id("net.ltgt.errorprone") version "4.0.1"
    id("io.freefair.lombok") version "8.10"
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "io.github.mfvanek"
version = "1.6.2"
description = "Simple implementation of two levels caching"

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")

    testImplementation(platform("org.junit:junit-bom:5.11.1"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.assertj:assertj-core:3.26.3")

    //pitest("it.mulders.stryker:pit-dashboard-reporter:0.2.1")
    checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")

    errorprone("com.google.errorprone:error_prone_core:2.32.0")
    errorprone("jp.skypencil.errorprone.slf4j:errorprone-slf4j:0.1.28")

    spotbugsPlugins("jp.skypencil.findbugs.slf4j:bug-pattern:1.5.0")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
    spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.6.5")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        implementation = JvmImplementation.VENDOR_SPECIFIC
    }
    withJavadocJar()
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("Slf4jLoggerShouldBeNonStatic", "TruthSelfEquals")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.7"
    }

    test {
        useJUnitPlatform()
        dependsOn(checkstyleMain, checkstyleTest, pmdMain, pmdTest, spotbugsMain, spotbugsTest)
        finalizedBy(jacocoTestReport, jacocoTestCoverageVerification)
    }

    javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    jacocoTestCoverageVerification {
        dependsOn(jacocoTestReport)
        violationRules {
            rule {
                limit {
                    counter = "CLASS"
                    value = "MISSEDCOUNT"
                    maximum = "1".toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "METHOD"
                    value = "MISSEDCOUNT"
                    maximum = "27".toBigDecimal() // +1 from windows build
                }
            }
            rule {
                limit {
                    counter = "LINE"
                    value = "MISSEDCOUNT"
                    maximum = "90".toBigDecimal() // +2 from windows build
                }
            }
            rule {
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.78".toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.78".toBigDecimal()
                }
            }
        }
    }

    check {
        dependsOn(jacocoTestCoverageVerification)
    }
}

lombok {
    version.set(libs.versions.lombok.get())
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

pmd {
    toolVersion = libs.versions.pmd.get()
    isConsoleOutput = true
    ruleSetFiles = files("config/pmd/pmd.xml")
    ruleSets = listOf()
}

spotbugs {
    showProgress.set(true)
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    excludeFilter.set(file("config/spotbugs/exclude.xml"))
}
tasks.withType<SpotBugsTask>().configureEach {
    reports {
        create("xml") { enabled = true }
        create("html") { enabled = true }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    checkConstraints = true
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
