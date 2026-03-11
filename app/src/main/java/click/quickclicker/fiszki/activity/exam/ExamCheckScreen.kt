package click.quickclicker.fiszki.activity.exam

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import click.quickclicker.fiszki.ui.BlobShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.HapticFeedback
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.algorithm.Algorithm
import click.quickclicker.fiszki.dialogs.exam.ExamSummaryData
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.ui.buildCorrectAnswerAnnotated
import click.quickclicker.fiszki.ui.buildUserAnswerAnnotated
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamCheckScreen(
    flashcardsPool: List<Flashcard>,
    totalRounds: Int,
    categoryName: String?,
    languagePair: String?,
    onBack: () -> Unit,
    onShowSummary: (ExamSummaryData) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val focusRequester = remember { FocusRequester() }

    val algorithm = remember { Algorithm(context) }
    val categoryRepository = remember { CategoryRepository(context) }
    val pool = remember { ArrayList(flashcardsPool) }
    val goodAnswers = remember { mutableStateListOf<Flashcard>() }
    val badAnswers = remember { mutableStateListOf<ArrayList<*>>() }

    var currentRound by rememberSaveable { mutableIntStateOf(1) }
    var correctCount by rememberSaveable { mutableIntStateOf(0) }
    var wrongCount by rememberSaveable { mutableIntStateOf(0) }
    var answerText by rememberSaveable { mutableStateOf("") }
    var buttonsEnabled by remember { mutableStateOf(true) }
    var showCorrectPopup by remember { mutableStateOf(false) }

    var showWrongDialog by remember { mutableStateOf(false) }
    var wrongExpected by remember { mutableStateOf("") }
    var wrongUser by remember { mutableStateOf("") }

    var showExitDialog by remember { mutableStateOf(false) }

    var currentFlashcard by remember { mutableStateOf(algorithm.drawCardAlgorithm(pool)) }
    var currentCategory by remember {
        mutableStateOf(categoryRepository.getCategoryByID(currentFlashcard.categoryID)!!)
    }

    fun finishExam() {
        val summaryData = ExamSummaryData(
            categoryName = categoryName ?: context.getString(R.string.learning_category_all),
            languagePair = languagePair,
            totalShown = totalRounds,
            correctCount = correctCount,
            incorrectCount = wrongCount,
            incorrectAnswers = ArrayList(badAnswers)
        )
        onShowSummary(summaryData)
    }

    fun drawNext() {
        if (currentRound >= totalRounds) {
            finishExam()
            return
        }
        currentRound++
        currentFlashcard = algorithm.drawCardAlgorithm(pool)
        currentCategory = categoryRepository.getCategoryByID(currentFlashcard.categoryID)!!
        answerText = ""
    }

    fun doCheck() {
        if (!buttonsEnabled) return
        val answer = answerText.trim()
        val correctAnswer = currentFlashcard.getTranslation()
        if (answer.equals(correctAnswer, ignoreCase = true)) {
            if (activity != null) HapticFeedback.vibrateCorrect(activity)
            goodAnswers.add(currentFlashcard)
            correctCount++
            showCorrectPopup = true
            buttonsEnabled = false
        } else {
            if (activity != null) HapticFeedback.vibrateWrong(activity)
            val bad = ArrayList<Any>().apply { add(currentFlashcard); add(answer) }
            badAnswers.add(bad)
            wrongCount++
            wrongExpected = correctAnswer
            wrongUser = answer
            showWrongDialog = true
            buttonsEnabled = false
        }
    }

    // Correct popup auto-dismiss
    LaunchedEffect(showCorrectPopup) {
        if (showCorrectPopup) {
            delay(1800L)
            showCorrectPopup = false
            buttonsEnabled = true
            drawNext()
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // Wrong answer dialog
    if (showWrongDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.alert_title_fail)) },
            text = {
                Column {
                    Text(stringResource(R.string.learning_check_dialog_your_answer_label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(buildUserAnswerAnnotated(wrongUser, wrongExpected), fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))
                    Text(stringResource(R.string.learning_check_dialog_bad_answer_1_new), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(buildCorrectAnswerAnnotated(wrongExpected, wrongUser), fontSize = 20.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showWrongDialog = false
                    buttonsEnabled = true
                    drawNext()
                }) { Text(stringResource(R.string.button_action_ok)) }
            }
        )
    }

    // Exit confirmation
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            text = { Text(stringResource(R.string.exam_check_exit_question)) },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onBack() }) {
                    Text(stringResource(R.string.button_action_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.button_action_no))
                }
            }
        )
    }

    val langText = if (currentCategory.getLangFrom().isNullOrEmpty() || currentCategory.getLangOn().isNullOrEmpty()) {
        stringResource(R.string.learning_check_lang_translate)
    } else {
        "${stringResource(R.string.learning_check_lang_translate_1)} ${currentCategory.getLangFrom()} ${stringResource(R.string.learning_check_lang_translate_2)} ${currentCategory.getLangOn()}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exam_check_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
            ) {
                Text(langText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(currentFlashcard.getWord(), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("(${currentCategory.getCategory()})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    enabled = buttonsEnabled,
                    modifier = Modifier.fillMaxWidth().height(88.dp).focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { doCheck() })
                )

                Spacer(Modifier.height(20.dp))

                OutlinedButton(onClick = { doCheck() }, enabled = buttonsEnabled, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.learning_check_btn_check))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.learning_check_status_title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.learning_check_status_correct, correctCount), color = Color(0xFF388E3C), modifier = Modifier.padding(top = 4.dp))
                        Text(stringResource(R.string.exam_check_status_wrong, wrongCount), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(stringResource(R.string.exam_check_status_remaining, totalRounds - currentRound), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            AnimatedVisibility(
                visible = showCorrectPopup,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier.size(220.dp).background(Color(0xFF376A3E), BlobShape).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.learning_check_correct_popup), fontSize = 36.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = Color.White)
                }
            }
        }
    }
}
