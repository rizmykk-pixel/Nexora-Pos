plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nexova.pos"
    compileSdk = libs.versions.compileSdk.get().toInt()

    val releaseStoreFile = providers.environmentVariable("NEXOVA_RELEASE_STORE_FILE").orNull
    val releaseStorePassword = providers.environmentVariable("NEXOVA_RELEASE_STORE_PASSWORD").orNull
    val releaseKeyAlias = providers.environmentVariable("NEXOVA_RELEASE_KEY_ALIAS").orNull
    val releaseKeyPassword = providers.environmentVariable("NEXOVA_RELEASE_KEY_PASSWORD").orNull
    val supabaseUrl = providers.gradleProperty("NEXOVA_SUPABASE_URL")
        .orElse(providers.environmentVariable("NEXOVA_SUPABASE_URL"))
        .orNull
    val supabaseAnonKey = providers.gradleProperty("NEXOVA_SUPABASE_ANON_KEY")
        .orElse(providers.environmentVariable("NEXOVA_SUPABASE_ANON_KEY"))
        .orNull
    val hasReleaseSigning = listOf(
        releaseStoreFile,
        releaseStorePassword,
        releaseKeyAlias,
        releaseKeyPassword,
    ).all { !it.isNullOrBlank() }
    val hasSupabaseConfig = !supabaseUrl.isNullOrBlank() && !supabaseAnonKey.isNullOrBlank()

    if (gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }) {
        require(hasSupabaseConfig) {
            "Release builds require NEXOVA_SUPABASE_URL and NEXOVA_SUPABASE_ANON_KEY"
        }
        require(hasReleaseSigning) {
            "Release builds require NEXOVA_RELEASE_STORE_FILE, NEXOVA_RELEASE_STORE_PASSWORD, NEXOVA_RELEASE_KEY_ALIAS, and NEXOVA_RELEASE_KEY_PASSWORD"
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("releaseCi") {
                storeFile = file(requireNotNull(releaseStoreFile))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    defaultConfig {
        applicationId = "com.nexova.pos"
        minSdk = 26
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
        buildConfigField("String", "BUSINESS_NAME", "\"Es Teh Kangen\"")
        buildConfigField("String", "SUPPORT_EMAIL", "\"rizmykaka@gmail.com\"")
        buildConfigField("String", "BUSINESS_CURRENCY", "\"IDR\"")
        buildConfigField("String", "BUSINESS_TIME_ZONE", "\"Asia/Jakarta\"")
        buildConfigField("String", "SUPABASE_URL", "\"${supabaseUrl}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${supabaseAnonKey}\"")
        buildConfigField("String", "BACKEND_URL", "\"https://nexova-pos-backend.rizmykk.workers.dev\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures { buildConfig = true; compose = true }
    buildTypes {
        getByName("debug") { isMinifyEnabled = false; buildConfigField("String", "NEXOVA_ENVIRONMENT", "\"debug\"") }
        create("staging") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "NEXOVA_ENVIRONMENT", "\"staging\"")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "NEXOVA_ENVIRONMENT", "\"release\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("releaseCi")
        }
    }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:catalog"))
    implementation(project(":feature:pos"))
    implementation(project(":feature:reports"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.toolingPreview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.adaptive)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
