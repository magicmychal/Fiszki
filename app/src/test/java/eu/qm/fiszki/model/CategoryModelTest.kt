package eu.qm.fiszki.model

import eu.qm.fiszki.model.category.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryModelTest {

    @Test
    fun setCategory_getCategory_apostropheRoundtrip() {
        val cat = Category()
        cat.setCategory("it's a test")
        assertEquals("it's a test", cat.getCategory())
    }

    @Test
    fun categoryDB_returnsEncodedForm() {
        val cat = Category()
        cat.setCategory("don't")
        assertEquals("don%sq%t", cat.categoryDB)
    }

    @Test
    fun setLangFrom_getLangFrom_apostropheRoundtrip() {
        val cat = Category()
        cat.setLangFrom("l'anglais")
        assertEquals("l'anglais", cat.getLangFrom())
    }

    @Test
    fun setLangOn_getLangOn_apostropheRoundtrip() {
        val cat = Category()
        cat.setLangOn("fran'ais")
        assertEquals("fran'ais", cat.getLangOn())
    }

    @Test
    fun getCategory_null_returnsEmptyString() {
        val cat = Category()
        assertEquals("", cat.getCategory())
    }

    @Test
    fun categoryDB_null_returnsEmptyString() {
        val cat = Category()
        assertEquals("", cat.categoryDB)
    }

    @Test
    fun getLangFrom_null_returnsNull() {
        val cat = Category()
        assertNull(cat.getLangFrom())
    }

    @Test
    fun getLangOn_null_returnsNull() {
        val cat = Category()
        assertNull(cat.getLangOn())
    }

    @Test
    fun color_getAndSet() {
        val cat = Category()
        assertNull(cat.getColor())
        cat.setColor("#FF0000")
        assertEquals("#FF0000", cat.getColor())
    }
}
