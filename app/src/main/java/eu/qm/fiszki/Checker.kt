package eu.qm.fiszki

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
            return dp[m][n]
        }
    }
}
