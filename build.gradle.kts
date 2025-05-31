
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.blackcube"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.netty)

    // Client (http request)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.negotiation)

    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)
    implementation(libs.ktor.server.config.yaml)

    // DB
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.datetime)
    implementation(libs.flyway.core)

    // ENV
    implementation(libs.dotenv)

    // HASH
    implementation(libs.bcrypt)

    // JWT
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // HTTPS
    implementation(libs.ktor.server.http.certificates)
    implementation(libs.ktor.server.http.redirect)

    // tests
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
