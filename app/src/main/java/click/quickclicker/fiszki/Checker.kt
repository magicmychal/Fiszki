package click.quickclicker.fiszki

class Checker {

    fun check(originalWord: String, enteredWord: String): Boolean {
        return originalWord == enteredWord
    }

    fun check(originalWord: String, enteredWord: String, strictMode: Boolean): Boolean {
        return if (strictMode) {
            originalWord == enteredWord
        } else {
            originalWord.trimEnd('.').trim().equals(
                enteredWord.trimEnd('.').trim(),
                ignoreCase = true
            )
        }
    }

    companion object {
        fun editDistance(a: String, b: String): Int {
            val dp = buildDpMatrix(a, b)
            return dp[a.length][b.length]
        }

        /**
         * Returns a BooleanArray for each string marking which characters are
         * "wrong" (insertions, deletions, substitutions) based on optimal
         * edit-distance alignment.
         *
         * Usage: `val (aDiffs, bDiffs) = Checker.alignDiffs(userAnswer, correctAnswer)`
         * - `aDiffs[i]` is true when character i in [a] is part of an error
         * - `bDiffs[j]` is true when character j in [b] is part of an error
         */
        fun alignDiffs(a: String, b: String): Pair<BooleanArray, BooleanArray> {
            val dp = buildDpMatrix(a, b)
            val aDiffs = BooleanArray(a.length)
            val bDiffs = BooleanArray(b.length)

            var i = a.length
            var j = b.length
            while (i > 0 || j > 0) {
                when {
                    i > 0 && j > 0 && a[i - 1] == b[j - 1] -> {
                        // Match — no highlight
                        i--; j--
                    }
                    i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1 -> {
                        // Substitution — both chars are wrong
                        aDiffs[i - 1] = true
                        bDiffs[j - 1] = true
                        i--; j--
                    }
                    i > 0 && dp[i][j] == dp[i - 1][j] + 1 -> {
                        // Deletion — extra char in a
                        aDiffs[i - 1] = true
                        i--
                    }
                    j > 0 && dp[i][j] == dp[i][j - 1] + 1 -> {
                        // Insertion — missing char in a, extra in b
                        bDiffs[j - 1] = true
                        j--
                    }
                    else -> {
                        // Fallback (shouldn't happen) — advance both
                        if (i > 0) { aDiffs[i - 1] = true; i-- }
                        if (j > 0) { bDiffs[j - 1] = true; j-- }
                    }
                }
            }
            return aDiffs to bDiffs
        }

        private fun buildDpMatrix(a: String, b: String): Array<IntArray> {
            val m = a.length
            val n = b.length
            val dp = Array(m + 1) { IntArray(n + 1) }
            for (i in 0..m) dp[i][0] = i
            for (j in 0..n) dp[0][j] = j
            for (i in 1..m) {
                for (j in 1..n) {
                    dp[i][j] = if (a[i - 1] == b[j - 1]) {
                        dp[i - 1][j - 1]
                    } else {
                        minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                    }
                }
            }
            return dp
        }
    }
}
