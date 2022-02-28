import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5
import org.springframework.cloud.contract.verifier.config.TestMode.WEBTESTCLIENT
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

val commonsVersion: String by project
val usersServiceVersion: String by project
val imageName = "springboot-availability"
val gcloudProjectId = System.getenv("GCP_PROJECT_ID") ?: "gcloud-equidis"

plugins {
    idea
    `maven-publish`
    jacoco
    id("org.springframework.boot") version "2.6.4"
    id("org.springframework.cloud.contract") version "2.2.5.RELEASE"
    id("com.google.cloud.tools.jib") version "3.0.0"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
}

group = "com.github.jntakpe"
version = "0.1.5"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    mavenGithub("equidis/sb-commons")
    mavenGithub("equidis/spring-boot-users-service")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("com.github.jntakpe:sb-commons-cache:$commonsVersion")
    implementation("com.github.jntakpe:sb-commons-mongo:$commonsVersion")
    implementation("com.github.jntakpe:sb-commons-web:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-commons-cache-test:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-commons-client-test:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-commons-mongo-test:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-commons-test:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-commons-web-test:$commonsVersion")
    testImplementation("com.github.jntakpe:sb-users:$usersServiceVersion:stubs")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    to {
        image = "eu.gcr.io/$gcloudProjectId/$imageName:${project.version}"
    }
}

tasks {
    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.isEnabled = true
        }
        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("build/generated", "**/model/entity/**")
            }
        )
    }
    check {
        dependsOn(jacocoTestReport)
    }
    val metadataPath = Paths.get("$buildDir/build-metadata.yaml")
    val deploymentZip = register<Zip>("deploymentZip") {
        archiveFileName.set("deployment-metadata.zip")
        destinationDirectory.set(Paths.get(buildDir.toString(), "libs").toFile())
        from(metadataPath.toString())
    }
    bootJar {
        doLast {
            if (!Files.exists(metadataPath)) Files.createFile(metadataPath)
            Files.writeString(
                metadataPath,
                """
        app:
          name: availability
          version: ${project.version}
          kind: http
          dependencies:
            mongodb: true
            redis: true
        image:
          name: $imageName
    """.trimIndent(), StandardOpenOption.SYNC
            )
        }
    }
    assemble {
        dependsOn(deploymentZip)
    }
}

contracts {
    setTestFramework(JUNIT5)
    setTestMode(WEBTESTCLIENT)
    setFailOnNoContracts(false)
    setBasePackageForTests("com.github.jntakpe.sbavailability")
    setBaseClassForTests("com.github.jntakpe.commons.web.test.ContractBaseClass")
}

publishing {
    repositories {
        mavenGithub("equidis/spring-boot-availability-service")
    }
}

fun RepositoryHandler.mavenGithub(repository: String) = maven {
    name = "Github_packages"
    setUrl("https://maven.pkg.github.com/$repository")
    credentials {
        val githubActor: String? by project
        val githubToken: String? by project
        username = githubActor
        password = githubToken
    }
}

