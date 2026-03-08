package eu.qm.fiszki.activity.exam

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.dialogs.exam.ExamSummaryData
import eu.qm.fiszki.model.flashcard.Flashcard

class ExamBadAnswerActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mSummaryData: ExamSummaryData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        mActivity = this
        @Suppress("UNCHECKED_CAST")
        mSummaryData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(ChangeActivityManager.EXAM_SUMMARY_DATA_KEY_INTENT, ExamSummaryData::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(ChangeActivityManager.EXAM_SUMMARY_DATA_KEY_INTENT) as ExamSummaryData
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ChangeActivityManager(mActivity).exitExamBadAnswer()
            }
        })

        setContent {
            FiszkiTheme {
                ExamSummaryScreen(
                    summaryData = mSummaryData,
                    onBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSummaryScreen(
    summaryData: ExamSummaryData,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exam_bad_answer_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Summary header
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        text = stringResource(R.string.exam_summary_set, summaryData.categoryName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    summaryData.languagePair?.let {
                        Text(
                            text = stringResource(R.string.exam_summary_language, it),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.exam_summary_total, summaryData.totalShown),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.exam_summary_correct, summaryData.correctCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.exam_summary_incorrect, summaryData.incorrectCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Incorrect answers section header
            if (summaryData.incorrectAnswers.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.exam_summary_incorrect_section),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                }

                // Incorrect answer cards
                items(summaryData.incorrectAnswers) { badAnswer ->
                    @Suppress("UNCHECKED_CAST")
                    val flashcard = (badAnswer as ArrayList<*>)[0] as Flashcard
                    val userAnswer = badAnswer[1] as String

                    IncorrectAnswerCard(
                        flashcard = flashcard,
                        userAnswer = userAnswer,
                        correctAnswer = flashcard.getTranslation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun IncorrectAnswerCard(
    flashcard: Flashcard,
    userAnswer: String,
    correctAnswer: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Flashcard label + word
            Text(
                text = stringResource(R.string.exam_bad_answer_flashcard),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = flashcard.getWord(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Your answer label
            Text(
                text = stringResource(R.string.exam_bad_answer_your_answer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // User answer with diff highlighting
            Text(
                text = buildDiffAnnotatedString(userAnswer, correctAnswer, isCorrect = false),
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Correct answer label
            Text(
                text = stringResource(R.string.exam_bad_answer_correct),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Correct answer with diff highlighting
            Text(
                text = buildDiffAnnotatedString(correctAnswer, userAnswer, isCorrect = true),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun buildDiffAnnotatedString(
    text: String,
    reference: String,
    isCorrect: Boolean
): androidx.compose.ui.text.AnnotatedString {
    val color = if (isCorrect) Color(0xFF388E3C) else Color(0xFFD32F2F)

    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val differs = i >= reference.length || text[i] != reference[i]
            if (differs) {
                var j = i + 1
                while (j < text.length && (j >= reference.length || text[j] != reference[j])) {
                    j++
                }
                withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                    append(text.substring(i, j))
                }
                i = j
            } else {
                append(text[i])
                i++
            }
        }
    }
}
