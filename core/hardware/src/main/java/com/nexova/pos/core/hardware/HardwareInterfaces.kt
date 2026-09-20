package com.nexova.pos.core.hardware

import kotlinx.coroutines.flow.Flow

/**
 * Rule 381: Hardware Abstraction Interfaces.
 */

enum class DeviceStatus {
    CONNECTED, CONNECTING, DISCONNECTED, ERROR, OUT_OF_PAPER, UNKNOWN
}

interface PosPrinter {
    val status: Flow<DeviceStatus>
    suspend fun connect()
    suspend fun disconnect()
    suspend fun printReceipt(receiptData: String): Boolean
}

interface BarcodeScanner {
    val status: Flow<DeviceStatus>
    val scannedData: Flow<String>
    suspend fun startScanning()
    suspend fun stopScanning()
}

interface CashDrawer {
    val status: Flow<DeviceStatus>
    suspend fun open(): Boolean
}

interface CustomerDisplay {
    val status: Flow<DeviceStatus>
    suspend fun displayMessage(line1: String, line2: String)
    suspend fun clear()
}
