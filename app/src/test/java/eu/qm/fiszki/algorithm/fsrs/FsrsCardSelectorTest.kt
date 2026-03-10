package eu.qm.fiszki.algorithm.fsrs

import eu.qm.fiszki.model.flashcard.Flashcard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FsrsCardSelectorTest {

    private lateinit var selector: FsrsCardSelector

    @Before
    fun setUp() {
        selector = FsrsCardSelector()
    }

    private fun makeCard(id: Int): Flashcard {
        val card = Flashcard()
        card.id = id
        card.setWord("word$id")
        card.setTranslation("trans$id")
        return card
    }

    @Test
    fun singleCardPool_returnsThatCard() {
        val card = makeCard(1)
        val result = selector.selectNext(listOf(card))
        assertEquals(1, result.id)
    }

    @Test
    fun singleCardPool_returnsItAgainOnSecondCall() {
        val card = makeCard(1)
        val first = selector.selectNext(listOf(card))
        val second = selector.selectNext(listOf(card))
        assertEquals(first.id, second.id)
    }

    @Test
    fun twoCards_doesNotReturnSameCardTwiceInARow() {
        val pool = listOf(makeCard(1), makeCard(2))
        val first = selector.selectNext(pool)
        val second = selector.selectNext(pool)
        assertNotEquals(first.id, second.id)
    }

    @Test
    fun multipleCards_allCardsDrawnBeforeReshuffle() {
        val pool = (1..5).map { makeCard(it) }
        val drawn = mutableSetOf<Int>()
        repeat(5) {
            drawn.add(selector.selectNext(pool).id)
        }
        assertEquals(5, drawn.size)
    }

    @Test
    fun queueReshufflesWhenExhausted() {
        val pool = (1..3).map { makeCard(it) }
        // Draw all 3
        repeat(3) { selector.selectNext(pool) }
        // Should still return a card after exhaustion (reshuffle)
        val next = selector.selectNext(pool)
        assertTrue(next.id in 1..3)
    }

    @Test
    fun reinsertForRetry_putsCardAheadInQueue() {
        val pool = (1..6).map { makeCard(it) }
        val first = selector.selectNext(pool) // draws card, leaves 5 in queue
        selector.reinsertForRetry(first)
        // The reinserted card should not be drawn in the next 1-2 draws
        // (RETRY_GAP = 3, so it goes to position 3)
        val second = selector.selectNext(pool)
        assertNotEquals(first.id, second.id)
        val third = selector.selectNext(pool)
        assertNotEquals(first.id, third.id)
    }

    @Test
    fun reinsertForRetry_smallQueue_putsAtEnd() {
        val pool = listOf(makeCard(1), makeCard(2))
        val first = selector.selectNext(pool) // draws one, 1 left in queue
        selector.reinsertForRetry(first)
        // Next draw should be the other card (not the reinserted one at end)
        val second = selector.selectNext(pool)
        assertNotEquals(first.id, second.id)
    }

    @Test
    fun reinsertForRetry_emptyQueue_insertsAtPositionZero() {
        val pool = listOf(makeCard(1))
        val first = selector.selectNext(pool) // queue now empty
        selector.reinsertForRetry(first)
        // Should be able to draw the reinserted card
        val second = selector.selectNext(pool)
        assertEquals(first.id, second.id)
    }
}
