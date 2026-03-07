package eu.qm.fiszki.activity.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import eu.qm.fiszki.HapticFeedback
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.activity.findCategoryColor
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ChatActivity : AppCompatActivity() {

    private lateinit var mAlgorithm: Algorithm
    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardsPool: ArrayList<Flashcard>
    private lateinit var mCurrentFlashcard: Flashcard
    private val mHandler = Handler(Looper.getMainLooper())

    private val messages = mutableStateListOf<ChatMessage>()
    private val toolbarColor = mutableStateOf<Color?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()

        init()
        buildComposeContent()
        sendWelcomeAndFirstPrompt()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        ChangeActivityManager(this).exitChatMode()
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        mAlgorithm = Algorithm(this)
        mFlashcardRepository = FlashcardRepository(this)
        mCategoryRepository = CategoryRepository(this)
        mFlashcardsPool = intent.getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT)
            as ArrayList<Flashcard>
    }

    private fun buildComposeContent() {
        setContent {
            FiszkiTheme {
                ChatScreen(
                    messages = messages,
                    toolbarColor = toolbarColor.value,
                    onSendMessage = { answer -> sendUserAnswer(answer) },
                    onNavigateBack = { onBackPressed() }
                )
            }
        }
    }

    private fun sendWelcomeAndFirstPrompt() {
        addBotMessage(getString(R.string.chat_welcome))
        mHandler.postDelayed({ drawAndPrompt() }, 500)
    }

    private fun drawAndPrompt() {
        mCurrentFlashcard = mAlgorithm.drawCardAlgorithm(mFlashcardsPool)
        val category = mCategoryRepository.getCategoryByID(mCurrentFlashcard.categoryID)

        if (category != null) {
            val catColor = findCategoryColor(category.getColor())
            if (catColor != null) {
                toolbarColor.value = Color(catColor.primary)
                @Suppress("DEPRECATION")
                window.statusBarColor = catColor.primary
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightStatusBars = false
            }
        }

        val prompt = if (category != null &&
            !category.getLangFrom().isNullOrEmpty() &&
            !category.getLangOn().isNullOrEmpty()
        ) {
            getString(R.string.chat_prompt_translate_lang, category.getLangFrom(), category.getLangOn()) +
                "\n${mCurrentFlashcard.getWord()}"
        } else {
            getString(R.string.chat_prompt_translate, mCurrentFlashcard.getWord())
        }

        addBotMessage(prompt)
    }

    private fun sendUserAnswer(answer: String) {
        if (answer.isEmpty()) return

        addUserMessage(answer)

        val correct = answer.equals(mCurrentFlashcard.getTranslation().trim(), ignoreCase = true)

        if (correct) {
            HapticFeedback.vibrateCorrect(this)
            mFlashcardRepository.upFlashcardPassStatistic(mCurrentFlashcard)
            mFlashcardRepository.upFlashcardPriority(mCurrentFlashcard)
            addBotMessage(getString(R.string.chat_correct))
        } else {
            HapticFeedback.vibrateWrong(this)
            mFlashcardRepository.upFlashcardFailStatistic(mCurrentFlashcard)
            mFlashcardRepository.downFlashcardPriority(mCurrentFlashcard)
            addBotMessage(getString(R.string.chat_wrong, mCurrentFlashcard.getTranslation()))
        }

        mHandler.postDelayed({ drawAndPrompt() }, 800)
    }

    private fun addBotMessage(text: String) {
        messages.add(ChatMessage(text, isUser = false))
    }

    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, isUser = true))
    }
}
