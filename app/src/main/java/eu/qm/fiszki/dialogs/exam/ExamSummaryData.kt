package eu.qm.fiszki.dialogs.exam

import java.io.Serializable

data class ExamSummaryData(
    val categoryName: String,
    val languagePair: String?,
    val totalShown: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val incorrectAnswers: ArrayList<ArrayList<*>>
) : Serializable

