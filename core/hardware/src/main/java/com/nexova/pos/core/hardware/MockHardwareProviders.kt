package com.nexova.pos.core.hardware

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPrinter @Inject constructor() : PosPrinter {
    private val _status = MutableStateFlow(DeviceStatus.DISCONNECTED)
    override val status = _status.asStateFlow()

    override suspend fun connect() {
        _status.value = DeviceStatus.CONNECTING
        delay(1000)
        _status.value = DeviceStatus.CONNECTED
    }

    override suspend fun disconnect() {
        _status.value = DeviceStatus.DISCONNECTED
    }

    override suspend fun printReceipt(receiptData: String): Boolean {
        if (_status.value != DeviceStatus.CONNECTED) return false
        delay(2000) // Simulate printing
        return true
    }
}

@Singleton
class CameraBarcodeScanner @Inject constructor() : BarcodeScanner {
    private val _status = MutableStateFlow(DeviceStatus.DISCONNECTED)
    override val status = _status.asStateFlow()

    private val _scannedData = MutableSharedFlow<String>()
    override val scannedData = _scannedData.asSharedFlow()

    override suspend fun startScanning() {
        _status.value = DeviceStatus.CONNECTED
    }

    override suspend fun stopScanning() {
        _status.value = DeviceStatus.DISCONNECTED
    }

    suspend fun simulateScan(barcode: String) {
        _scannedData.emit(barcode)
    }
}
