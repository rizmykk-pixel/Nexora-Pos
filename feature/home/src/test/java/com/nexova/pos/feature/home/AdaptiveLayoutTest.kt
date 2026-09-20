package com.nexova.pos.feature.home

import com.nexova.pos.core.domain.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveLayoutTest {
    @Test fun compactMediumExpandedBreakpointsAreStable() {
        assertEquals(WindowSizeClass.COMPACT, classifyWindow(599f))
        assertEquals(WindowSizeClass.MEDIUM, classifyWindow(600f))
        assertEquals(WindowSizeClass.EXPANDED, classifyWindow(840f))
    }
}
