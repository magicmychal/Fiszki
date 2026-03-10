package click.quickclicker.fiszki.algorithm.fsrs

enum class FsrsState {
    New, Learning, Review, Relearning
}

enum class FsrsRating(val value: Int) {
    Again(1), Hard(2), Good(3), Easy(4)
}

data class FsrsCard(
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val elapsedDays: Int = 0,
    val scheduledDays: Int = 0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val state: FsrsState = FsrsState.New,
    val lastReview: Long = 0L
)

/**
 * FSRS v6 default parameters (w[0..20]).
 * Source: https://github.com/open-spaced-repetition/fsrs-rs
 */
object FsrsParams {
    val w = doubleArrayOf(
        0.2120,  // w0  - initial stability for Again
        1.2931,  // w1  - initial stability for Hard
        2.3065,  // w2  - initial stability for Good
        8.2956,  // w3  - initial stability for Easy
        6.4133,  // w4  - init difficulty offset
        0.8334,  // w5  - init difficulty rating multiplier
        3.0194,  // w6  - difficulty delta multiplier
        0.0010,  // w7  - mean reversion weight
        1.8722,  // w8  - recall stability: exp base
        0.1666,  // w9  - recall stability: stability exponent
        0.7960,  // w10 - recall stability: retrievability exponent
        1.4835,  // w11 - forget stability: difficulty base
        0.0614,  // w12 - forget stability: difficulty exponent
        0.2629,  // w13 - forget stability: stability exponent
        1.6483,  // w14 - forget stability: retrievability exponent
        0.6014,  // w15 - hard penalty
        1.8729,  // w16 - easy bonus
        0.5425,  // w17 - short-term stability rating base
        0.0912,  // w18 - short-term stability offset
        0.0658,  // w19 - short-term stability exponent
        0.1542   // w20 - decay constant for forgetting curve
    )
}
