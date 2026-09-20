package com.nexova.pos.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import com.nexova.pos.core.network.payment.PaymentProvider
import com.nexova.pos.core.network.payment.SupabasePaymentProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        check(BuildConfig.SUPABASE_URL.isNotBlank()) {
            "NEXOVA_SUPABASE_URL must be provided through Gradle properties or the environment"
        }
        check(BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) {
            "NEXOVA_SUPABASE_ANON_KEY must be provided through Gradle properties or the environment"
        }

        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun providePaymentProvider(client: SupabaseClient): PaymentProvider {
        return SupabasePaymentProvider(client)
    }

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth = client.auth

    @Provides
    @Singleton
    fun provideSupabaseRealtime(client: SupabaseClient): Realtime = client.realtime
}
