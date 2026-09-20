package com.nexova.pos.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTokenTest {
    @Test fun brandNavyMatchesApprovedToken() {
        assertEquals(0x0B / 255f, NexovaBrand.navy.red, 0.001f)
        assertEquals(0x10 / 255f, NexovaBrand.navy.green, 0.001f)
        assertEquals(0x26 / 255f, NexovaBrand.navy.blue, 0.001f)
    }
}
