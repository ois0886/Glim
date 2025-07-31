import java.io.FileInputStream
import java.util.Properties

val props = Properties().apply {
    FileInputStream(rootProject.file("local.properties")).use { load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.ssafy.glim"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.ssafy.glim"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val url: String = props.getProperty("BASE_URL")
        buildConfigField("String", "BASE_URL", "\"$url\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
}

ktlint {
    version.set("1.0.1")
    debug.set(false)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(true)

    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt/detekt.html"))

        xml.required.set(true)
        xml.outputLocation.set(file("build/reports/detekt/detekt.xml"))

        txt.required.set(true)
        txt.outputLocation.set(file("build/reports/detekt/detekt.txt"))

        sarif.required.set(true)
        sarif.outputLocation.set(file("build/reports/detekt/detekt.sarif"))

        md.required.set(true)
        md.outputLocation.set(file("build/reports/detekt/detekt.md"))
    }
}

tasks.getByPath("preBuild").dependsOn("ktlintCheck")

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material3)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.splashscreen)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // ML Kit Text Recognition
    implementation(libs.play.services.mlkit.text.recognition)
    implementation(libs.text.recognition.korean)

    // Orbit MVI
    implementation(libs.orbit.core)
    implementation(libs.orbit.viewmodel)
    implementation(libs.orbit.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.orbit.test)

    // Kotlin Serialization & Immutable Collections
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.ext.compiler)
    implementation(libs.hilt.navigation.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(kotlin("test"))

    // Retrofit + Okhttp + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // detekt
    detektPlugins(libs.detekt.formatting)

    // MockK
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
}

tasks.register("codeQualityCheck") {
    group = "verification"
    description = "코드 품질 전체 검사"
    dependsOn("ktlintCheck", "detekt", "test")
}

tasks.register("codeQualityFix") {
    group = "formatting"
    description = "코드 포맷팅 자동 수정"
    dependsOn("ktlintFormat")
}
