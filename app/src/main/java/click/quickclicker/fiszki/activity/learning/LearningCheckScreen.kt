package click.quickclicker.fiszki.activity.learning

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.Checker
import click.quickclicker.fiszki.HapticFeedback
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.algorithm.Algorithm
import click.quickclicker.fiszki.algorithm.fsrs.FsrsCardSelector
import click.quickclicker.fiszki.algorithm.fsrs.FsrsRatingMapper
import click.quickclicker.fiszki.algorithm.fsrs.FsrsScheduler
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.buildCorrectAnswerAnnotated
import click.quickclicker.fiszki.ui.buildUserAnswerAnnotated
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningCheckScreen(
    flashcardsPool: List<Flashcard>,
    strictMode: Boolean,
    reversed: Boolean,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val prefs = remember { LocalSharedPreferences(context) }
    val useFsrs = prefs.useFsrsAlgorithm
    val algorithm = remember { Algorithm(context) }
    val categoryRepository = remember { CategoryRepository(context) }
    val flashcardRepository = remember { FlashcardRepository(context) }
    val fsrsCardSelector = remember { if (useFsrs) FsrsCardSelector() else null }
    val fsrsScheduler = remember { FsrsScheduler() }

    var correctCount by rememberSaveable { mutableIntStateOf(0) }
    var totalCount by rememberSaveable { mutableIntStateOf(0) }
    var answerText by rememberSaveable { mutableStateOf("") }
    var buttonsEnabled by remember { mutableStateOf(true) }
    var showCorrectPopup by remember { mutableStateOf(false) }
    var retrying by remember { mutableStateOf(false) }
    var attemptCount by remember { mutableIntStateOf(0) }
    var cardStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var showBadAnswerDialog by remember { mutableStateOf(false) }
    var badAnswerExpected by remember { mutableStateOf("") }
    var badAnswerUser by remember { mutableStateOf("") }

    val pool = remember { ArrayList(flashcardsPool) }

    var currentFlashcard by remember {
        mutableStateOf(
            if (useFsrs) fsrsCardSelector!!.selectNext(pool)
            else algorithm.drawCardAlgorithm(pool)
        )
    }
    var currentCategory by remember {
        mutableStateOf(categoryRepository.getCategoryByID(currentFlashcard.categoryID)!!)
    }

    fun drawNext() {
        retrying = false
        attemptCount = 0
        currentFlashcard = if (useFsrs) fsrsCardSelector!!.selectNext(pool)
        else algorithm.drawCardAlgorithm(pool)
        cardStartTime = System.currentTimeMillis()
        currentCategory = categoryRepository.getCategoryByID(currentFlashcard.categoryID)!!
        answerText = ""
    }

    fun doCheck() {
        if (!buttonsEnabled) return
        val answer = answerText.trim()
        val expected = if (reversed) currentFlashcard.getWord() else currentFlashcard.getTranslation()
        val checker = Checker()
        attemptCount++
        if (checker.check(expected, answer, strictMode)) {
            if (activity != null) HapticFeedback.vibrateCorrect(activity)
            flashcardRepository.upFlashcardPassStatistic(currentFlashcard)
            if (!retrying) {
                if (useFsrs) {
                    val ed = Checker.editDistance(expected.lowercase(), answer.lowercase())
                    val elapsed = System.currentTimeMillis() - cardStartTime
                    val rating = FsrsRatingMapper.mapToRating(false, attemptCount, true, elapsed, ed)
                    val updated = fsrsScheduler.schedule(currentFlashcard.toFsrsCard(), rating, Date())
                    currentFlashcard.applyFsrsCard(updated)
                    currentFlashcard.fsrsLastRating = rating.value
                    flashcardRepository.updateFsrsState(currentFlashcard)
                } else {
                    flashcardRepository.upFlashcardPriority(currentFlashcard)
                }
            }
            correctCount++
            totalCount++
            showCorrectPopup = true
            buttonsEnabled = false
        } else {
            if (activity != null) HapticFeedback.vibrateWrong(activity)
            flashcardRepository.upFlashcardFailStatistic(currentFlashcard)
            if (useFsrs) {
                fsrsCardSelector!!.reinsertForRetry(currentFlashcard)
            } else {
                flashcardRepository.downFlashcardPriority(currentFlashcard)
            }
            retrying = true
            totalCount++
            badAnswerExpected = expected
            badAnswerUser = answer
            showBadAnswerDialog = true
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

    // Focus input on launch and every time a new card is shown
    LaunchedEffect(currentFlashcard) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // Bad answer dialog
    if (showBadAnswerDialog) {
        BadAnswerDialog(
            expectedAnswer = badAnswerExpected,
            userAnswer = badAnswerUser,
            onRetry = {
                showBadAnswerDialog = false
                answerText = ""
                focusRequester.requestFocus()
                keyboardController?.show()
            },
            onSkip = {
                showBadAnswerDialog = false
                if (useFsrs) {
                    val rating = FsrsRatingMapper.mapToRating(true, attemptCount, false, 0, 0)
                    val updated = fsrsScheduler.schedule(currentFlashcard.toFsrsCard(), rating, Date())
                    currentFlashcard.applyFsrsCard(updated)
                    currentFlashcard.fsrsLastRating = rating.value
                    flashcardRepository.updateFsrsState(currentFlashcard)
                }
                drawNext()
            }
        )
    }

    val langFrom = if (reversed) currentCategory.getLangOn() else currentCategory.getLangFrom()
    val langOn = if (reversed) currentCategory.getLangFrom() else currentCategory.getLangOn()
    val langText = if (langFrom.isNullOrEmpty() || langOn.isNullOrEmpty()) {
        stringResource(R.string.learning_check_lang_translate)
    } else {
        "${stringResource(R.string.learning_check_lang_translate_1)} $langFrom ${stringResource(R.string.learning_check_lang_translate_2)} $langOn"
    }
    val wordText = if (reversed) currentFlashcard.getTranslation() else currentFlashcard.getWord()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.learning_check_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
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
                // Language direction
                Text(langText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Word
                Text(wordText, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("(${currentCategory.getCategory()})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(24.dp))

                // Answer input
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

                // Buttons row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = onFinish, enabled = buttonsEnabled, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.learning_check_btn_finish))
                    }
                    OutlinedButton(onClick = {
                        if (useFsrs) {
                            val rating = FsrsRatingMapper.mapToRating(true, attemptCount, false, 0, 0)
                            val updated = fsrsScheduler.schedule(currentFlashcard.toFsrsCard(), rating, Date())
                            currentFlashcard.applyFsrsCard(updated)
                            currentFlashcard.fsrsLastRating = rating.value
                            flashcardRepository.updateFsrsState(currentFlashcard)
                        }
                        drawNext()
                    }, enabled = buttonsEnabled, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.learning_check_btn_skip))
                    }
                    OutlinedButton(onClick = { doCheck() }, enabled = buttonsEnabled, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.learning_check_btn_check))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

                // Status card
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.learning_check_status_title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.learning_check_status_correct, correctCount), color = Color(0xFF388E3C), modifier = Modifier.padding(top = 4.dp))
                        Text(stringResource(R.string.learning_check_status_total, totalCount), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Correct popup overlay
            AnimatedVisibility(
                visible = showCorrectPopup,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color(0xFF376A3E), BlobShape)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.learning_check_correct_popup),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
            }
        }
    }
}
