package click.quickclicker.fiszki.algorithm.exam

import click.quickclicker.fiszki.model.flashcard.Flashcard
import org.junit.Assert.*
import org.junit.Test

class ExamCardSelectorTest {

    private fun makeFlashcard(id: Int): Flashcard {
        val f = Flashcard()
        f.id = id
        f.word = "word_$id"
        f.translation = "trans_$id"
        return f
    }

    @Test
    fun allCardsReturnedExactlyOnce() {
        val pool = (1..20).map { makeFlashcard(it) }
        val selector = ExamCardSelector(pool)

        val drawn = mutableListOf<Flashcard>()
        while (true) {
            val card = selector.selectNext() ?: break
            drawn.add(card)
        }

        assertEquals(pool.size, drawn.size)
        assertEquals(pool.map { it.id }.toSet(), drawn.map { it.id }.toSet())
    }

    @Test
    fun noRepeats() {
        val pool = (1..50).map { makeFlashcard(it) }
        val selector = ExamCardSelector(pool)

        val seenIds = mutableSetOf<Int>()
        while (true) {
            val card = selector.selectNext() ?: break
            assertFalse("Card ${card.id} was already drawn", card.id in seenIds)
            seenIds.add(card.id)
        }
    }

    @Test
    fun returnsNullWhenExhausted() {
        val pool = listOf(makeFlashcard(1), makeFlashcard(2))
        val selector = ExamCardSelector(pool)

        assertNotNull(selector.selectNext())
        assertNotNull(selector.selectNext())
        assertNull(selector.selectNext())
        assertNull(selector.selectNext()) // still null on repeated calls
    }

    @Test
    fun singleCardPool() {
        val pool = listOf(makeFlashcard(42))
        val selector = ExamCardSelector(pool)

        val card = selector.selectNext()
        assertNotNull(card)
        assertEquals(42, card!!.id)
        assertNull(selector.selectNext())
    }

    @Test
    fun emptyPoolReturnsNullImmediately() {
        val selector = ExamCardSelector(emptyList())
        assertNull(selector.selectNext())
    }

    @Test
    fun remainingCountDecrements() {
        val pool = (1..5).map { makeFlashcard(it) }
        val selector = ExamCardSelector(pool)

        assertEquals(5, selector.remaining())
        selector.selectNext()
        assertEquals(4, selector.remaining())
        selector.selectNext()
        assertEquals(3, selector.remaining())
    }
}

