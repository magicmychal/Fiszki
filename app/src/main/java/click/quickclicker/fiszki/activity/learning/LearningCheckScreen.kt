package click.quickclicker.fiszki.activity.learning

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.Checker
import click.quickclicker.fiszki.HapticFeedback
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.algorithm.Algorithm
import click.quickclicker.fiszki.algorithm.debug.SessionCardRecord
import click.quickclicker.fiszki.algorithm.fsrs.FsrsCardSelector
import click.quickclicker.fiszki.algorithm.fsrs.FsrsRatingMapper
import click.quickclicker.fiszki.algorithm.fsrs.FsrsScheduler
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState
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
    val debugEnabled = prefs.debugAlgorithmEnabled
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
    var attemptCount by remember { mutableIntStateOf(0) }
    var cardStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val sessionHistory = remember { mutableStateListOf<SessionCardRecord>() }
    var showDebugReport by remember { mutableStateOf(false) }

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
            val elapsed = System.currentTimeMillis() - cardStartTime
            val stabilityBefore = currentFlashcard.fsrsStability
            val difficultyBefore = currentFlashcard.fsrsDifficulty
            val fsrsStateBefore = FsrsState.entries[currentFlashcard.fsrsState]
            if (useFsrs) {
                // Always update FSRS state on correct answer — attemptCount > 1
                // ensures the mapper returns Hard for retried cards
                val ed = Checker.editDistance(expected.lowercase(), answer.lowercase())
                val rating = FsrsRatingMapper.mapToRating(false, attemptCount, true, elapsed, ed)
                val cardBefore = currentFlashcard.toFsrsCard()
                val retrievability = fsrsScheduler.retrievability(cardBefore)
                val updated = fsrsScheduler.schedule(cardBefore, rating, Date())
                currentFlashcard.applyFsrsCard(updated)
                currentFlashcard.fsrsLastRating = rating.value
                flashcardRepository.updateFsrsState(currentFlashcard)
                if (debugEnabled) {
                    sessionHistory.add(SessionCardRecord(
                        word = currentFlashcard.getWord(),
                        translation = currentFlashcard.getTranslation(),
                        rating = rating,
                        attemptCount = attemptCount,
                        elapsedTimeMs = elapsed,
                        wasSkipped = false,
                        wasCorrect = true,
                        stabilityBefore = stabilityBefore,
                        stabilityAfter = updated.stability,
                        difficultyBefore = difficultyBefore,
                        difficultyAfter = updated.difficulty,
                        retrievability = retrievability,
                        scheduledDays = updated.scheduledDays,
                        fsrsState = updated.state,
                        reps = updated.reps,
                        lapses = updated.lapses,
                        priority = currentFlashcard.priority
                    ))
                }
            } else if (attemptCount == 1) {
                // Legacy path: only update priority on first-attempt success
                flashcardRepository.upFlashcardPriority(currentFlashcard)
                if (debugEnabled) {
                    sessionHistory.add(SessionCardRecord(
                        word = currentFlashcard.getWord(),
                        translation = currentFlashcard.getTranslation(),
                        rating = null,
                        attemptCount = attemptCount,
                        elapsedTimeMs = elapsed,
                        wasSkipped = false,
                        wasCorrect = true,
                        stabilityBefore = stabilityBefore,
                        stabilityAfter = currentFlashcard.fsrsStability,
                        difficultyBefore = difficultyBefore,
                        difficultyAfter = currentFlashcard.fsrsDifficulty,
                        retrievability = 0.0,
                        scheduledDays = 0,
                        fsrsState = fsrsStateBefore,
                        reps = currentFlashcard.fsrsReps,
                        lapses = currentFlashcard.fsrsLapses,
                        priority = currentFlashcard.priority
                    ))
                }
            }
            correctCount++
            totalCount++
            showCorrectPopup = true
            buttonsEnabled = false
        } else {
            if (activity != null) HapticFeedback.vibrateWrong(activity)
            flashcardRepository.upFlashcardFailStatistic(currentFlashcard)
            if (!useFsrs) {
                flashcardRepository.downFlashcardPriority(currentFlashcard)
            }
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
                    fsrsCardSelector!!.reinsertForRetry(currentFlashcard)
                }
                val elapsed = System.currentTimeMillis() - cardStartTime
                val stabilityBefore = currentFlashcard.fsrsStability
                val difficultyBefore = currentFlashcard.fsrsDifficulty
                val fsrsStateBefore = FsrsState.entries[currentFlashcard.fsrsState]
                if (useFsrs) {
                    val rating = FsrsRatingMapper.mapToRating(true, attemptCount, false, 0, 0)
                    val cardBefore = currentFlashcard.toFsrsCard()
                    val retrievability = fsrsScheduler.retrievability(cardBefore)
                    val updated = fsrsScheduler.schedule(cardBefore, rating, Date())
                    currentFlashcard.applyFsrsCard(updated)
                    currentFlashcard.fsrsLastRating = rating.value
                    flashcardRepository.updateFsrsState(currentFlashcard)
                    if (debugEnabled) {
                        sessionHistory.add(SessionCardRecord(
                            word = currentFlashcard.getWord(),
                            translation = currentFlashcard.getTranslation(),
                            rating = rating,
                            attemptCount = attemptCount,
                            elapsedTimeMs = elapsed,
                            wasSkipped = true,
                            wasCorrect = false,
                            stabilityBefore = stabilityBefore,
                            stabilityAfter = updated.stability,
                            difficultyBefore = difficultyBefore,
                            difficultyAfter = updated.difficulty,
                            retrievability = retrievability,
                            scheduledDays = updated.scheduledDays,
                            fsrsState = updated.state,
                            reps = updated.reps,
                            lapses = updated.lapses,
                            priority = currentFlashcard.priority
                        ))
                    }
                } else if (debugEnabled) {
                    sessionHistory.add(SessionCardRecord(
                        word = currentFlashcard.getWord(),
                        translation = currentFlashcard.getTranslation(),
                        rating = null,
                        attemptCount = attemptCount,
                        elapsedTimeMs = elapsed,
                        wasSkipped = true,
                        wasCorrect = false,
                        stabilityBefore = stabilityBefore,
                        stabilityAfter = currentFlashcard.fsrsStability,
                        difficultyBefore = difficultyBefore,
                        difficultyAfter = currentFlashcard.fsrsDifficulty,
                        retrievability = 0.0,
                        scheduledDays = 0,
                        fsrsState = fsrsStateBefore,
                        reps = currentFlashcard.fsrsReps,
                        lapses = currentFlashcard.fsrsLapses,
                        priority = currentFlashcard.priority
                    ))
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

    fun handleFinish() {
        if (debugEnabled && sessionHistory.isNotEmpty()) {
            showDebugReport = true
        } else {
            onFinish()
        }
    }

    // Intercept system back press to show debug report
    BackHandler(enabled = !showDebugReport) {
        handleFinish()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.learning_check_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = { handleFinish() }) {
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
                        text = stringResource(R.string.learning_check_status_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${correctCount} / ${totalCount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (totalCount > 0) {
                    LinearProgressIndicator(
                        progress = { correctCount.toFloat() / totalCount.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }

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
                            text = wordText,
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

                // Bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Skip button (text style)
                    TextButton(
                        onClick = {
                            val elapsed = System.currentTimeMillis() - cardStartTime
                            val stabilityBefore = currentFlashcard.fsrsStability
                            val difficultyBefore = currentFlashcard.fsrsDifficulty
                            val fsrsStateBefore = FsrsState.entries[currentFlashcard.fsrsState]
                            if (useFsrs) {
                                val rating = FsrsRatingMapper.mapToRating(true, attemptCount, false, 0, 0)
                                val cardBefore = currentFlashcard.toFsrsCard()
                                val retrievability = fsrsScheduler.retrievability(cardBefore)
                                val updated = fsrsScheduler.schedule(cardBefore, rating, Date())
                                currentFlashcard.applyFsrsCard(updated)
                                currentFlashcard.fsrsLastRating = rating.value
                                flashcardRepository.updateFsrsState(currentFlashcard)
                                if (debugEnabled) {
                                    sessionHistory.add(SessionCardRecord(
                                        word = currentFlashcard.getWord(),
                                        translation = currentFlashcard.getTranslation(),
                                        rating = rating,
                                        attemptCount = attemptCount,
                                        elapsedTimeMs = elapsed,
                                        wasSkipped = true,
                                        wasCorrect = false,
                                        stabilityBefore = stabilityBefore,
                                        stabilityAfter = updated.stability,
                                        difficultyBefore = difficultyBefore,
                                        difficultyAfter = updated.difficulty,
                                        retrievability = retrievability,
                                        scheduledDays = updated.scheduledDays,
                                        fsrsState = updated.state,
                                        reps = updated.reps,
                                        lapses = updated.lapses,
                                        priority = currentFlashcard.priority
                                    ))
                                }
                            } else if (debugEnabled) {
                                sessionHistory.add(SessionCardRecord(
                                    word = currentFlashcard.getWord(),
                                    translation = currentFlashcard.getTranslation(),
                                    rating = null,
                                    attemptCount = attemptCount,
                                    elapsedTimeMs = elapsed,
                                    wasSkipped = true,
                                    wasCorrect = false,
                                    stabilityBefore = stabilityBefore,
                                    stabilityAfter = currentFlashcard.fsrsStability,
                                    difficultyBefore = difficultyBefore,
                                    difficultyAfter = currentFlashcard.fsrsDifficulty,
                                    retrievability = 0.0,
                                    scheduledDays = 0,
                                    fsrsState = fsrsStateBefore,
                                    reps = currentFlashcard.fsrsReps,
                                    lapses = currentFlashcard.fsrsLapses,
                                    priority = currentFlashcard.priority
                                ))
                            }
                            drawNext()
                        },
                        enabled = buttonsEnabled
                    ) {
                        Text(
                            stringResource(R.string.learning_check_btn_skip),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Check button (primary, filled, large rounded)
                    Button(
                        onClick = { doCheck() },
                        enabled = buttonsEnabled,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
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

    // Debug report full-screen overlay
    if (showDebugReport) {
        AlgorithmDebugReportScreen(
            sessionHistory = sessionHistory,
            useFsrs = useFsrs,
            onClose = {
                showDebugReport = false
                onFinish()
            }
        )
    }
}
