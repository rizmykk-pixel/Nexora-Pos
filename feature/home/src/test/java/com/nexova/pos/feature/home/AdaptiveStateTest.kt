package com.nexova.pos.feature.home

import com.nexova.pos.core.domain.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveStateTest {
    @Test fun windowClassesRemainExplicit() {
        assertEquals(
            listOf(
                WindowSizeClass.COMPACT,
                WindowSizeClass.MEDIUM,
                WindowSizeClass.EXPANDED,
                WindowSizeClass.LARGE,
                WindowSizeClass.EXTRA_LARGE
            ),
            WindowSizeClass.entries
        )
    }
}
