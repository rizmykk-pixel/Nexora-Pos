plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.nexova.pos.core.network"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildFeatures { buildConfig = true }

    fun configValue(name: String): String = providers.gradleProperty(name)
        .orElse(providers.environmentVariable(name))
        .orElse("")
        .get()
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")

    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"${configValue("NEXOVA_SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${configValue("NEXOVA_SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "BACKEND_URL", "\"${configValue("NEXOVA_BACKEND_URL")}\"")
    }

}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    api(platform(libs.supabase.bom))
    api(libs.supabase.postgrest)
    api(libs.supabase.auth)
    api(libs.supabase.realtime)
    implementation(libs.ktor.client.okhttp)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
}
