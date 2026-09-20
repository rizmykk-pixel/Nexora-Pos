package com.nexova.pos.core.hardware.di

import com.nexova.pos.core.hardware.BarcodeScanner
import com.nexova.pos.core.hardware.CameraBarcodeScanner
import com.nexova.pos.core.hardware.MockPrinter
import com.nexova.pos.core.hardware.PosPrinter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {
    
    @Binds
    @Singleton
    abstract fun bindPrinter(printer: MockPrinter): PosPrinter

    @Binds
    @Singleton
    abstract fun bindScanner(scanner: CameraBarcodeScanner): BarcodeScanner
}
