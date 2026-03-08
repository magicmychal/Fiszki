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
}
