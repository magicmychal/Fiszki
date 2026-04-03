package click.quickclicker.fiszki.activity.exam

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import click.quickclicker.fiszki.ui.BlobShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.HapticFeedback
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.algorithm.exam.ExamCardSelector
import click.quickclicker.fiszki.dialogs.exam.ExamSummaryData
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
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

    val examSelector = remember { ExamCardSelector(flashcardsPool.take(totalRounds)) }
    val categoryRepository = remember { CategoryRepository(context) }
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

    var currentFlashcard by remember { mutableStateOf(examSelector.selectNext()!!) }
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
        val nextCard = examSelector.selectNext()
        if (nextCard == null) {
            finishExam()
            return
        }
        currentRound++
        currentFlashcard = nextCard
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

    val langFrom = currentCategory.getLangFrom()
    val langOn = currentCategory.getLangOn()
    val langText = if (langFrom.isNullOrEmpty() || langOn.isNullOrEmpty()) {
        stringResource(R.string.learning_check_lang_translate)
    } else {
        "${stringResource(R.string.learning_check_lang_translate_1)} $langFrom ${stringResource(R.string.learning_check_lang_translate_2)} $langOn"
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.exam_check_repeat_qustion_1)} $currentRound ${stringResource(R.string.exam_check_repeat_qustion_2)} $totalRounds".uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "$currentRound / $totalRounds",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                LinearProgressIndicator(
                    progress = { currentRound.toFloat() / totalRounds.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )

                Spacer(Modifier.height(24.dp))

                // Flashcard surface
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = langText.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = currentFlashcard.getWord(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = RobotoSerifFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        if (!langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            ) {
                                Text(
                                    text = "$langFrom \u2192 $langOn",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Answer input
                Text(
                    text = stringResource(R.string.learning_check_dialog_your_answer_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    enabled = buttonsEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { doCheck() })
                )

                Spacer(Modifier.height(24.dp))

                // Check button
                Button(
                    onClick = { doCheck() },
                    enabled = buttonsEnabled,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        stringResource(R.string.learning_check_btn_check),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Correct popup overlay
            AnimatedVisibility(
                visible = showCorrectPopup,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(MaterialTheme.colorScheme.tertiary, BlobShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.learning_check_correct_popup),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}
