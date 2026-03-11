package click.quickclicker.fiszki

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalSharedPreferencesTest {

    private lateinit var prefs: LocalSharedPreferences

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Clear prefs before each test
        context.getSharedPreferences("fiszki_prefs", 0).edit().clear().commit()
        prefs = LocalSharedPreferences(context)
    }

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("fiszki_prefs", 0).edit().clear().commit()
    }

    @Test
    fun notificationEnabled_defaultsToFalse() {
        assertFalse(prefs.notificationEnabled)
    }

    @Test
    fun notificationHour_defaultsTo9() {
        assertEquals(9, prefs.notificationHour)
    }

    @Test
    fun notificationDays_defaultsToAll7Days() {
        val days = prefs.notificationDays
        assertEquals(7, days.size)
        for (i in 1..7) {
            assertTrue("Missing day $i", days.contains(i.toString()))
        }
    }

    @Test
    fun useFsrsAlgorithm_defaultsToTrue() {
        assertTrue(prefs.useFsrsAlgorithm)
    }

    @Test
    fun colorPalette_defaultsToPurple() {
        assertEquals(LocalSharedPreferences.PALETTE_PURPLE, prefs.colorPalette)
    }

    @Test
    fun writeAndRead_notificationEnabled() {
        prefs.notificationEnabled = true
        assertTrue(prefs.notificationEnabled)
        prefs.notificationEnabled = false
        assertFalse(prefs.notificationEnabled)
    }

    @Test
    fun writeAndRead_notificationHour() {
        prefs.notificationHour = 14
        assertEquals(14, prefs.notificationHour)
    }

    @Test
    fun writeAndRead_colorPalette() {
        prefs.colorPalette = LocalSharedPreferences.PALETTE_YELLOW
        assertEquals(LocalSharedPreferences.PALETTE_YELLOW, prefs.colorPalette)
    }

    @Test
    fun writeAndRead_useFsrsAlgorithm() {
        prefs.useFsrsAlgorithm = false
        assertFalse(prefs.useFsrsAlgorithm)
    }

    @Test
    fun diagnosticDataEnabled_defaultsToFalse() {
        assertFalse(prefs.diagnosticDataEnabled)
    }
}
