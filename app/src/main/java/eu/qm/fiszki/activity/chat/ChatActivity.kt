package eu.qm.fiszki.activity.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.findCategoryColor
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ChatActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: ChatAdapter
    private lateinit var mInput: EditText
    private lateinit var mSendButton: ImageButton
    private lateinit var mAlgorithm: Algorithm
    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardsPool: ArrayList<Flashcard>
    private lateinit var mCurrentFlashcard: Flashcard
    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_chat)

        init()
        buildToolbar()
        buildInput()
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

        mChatAdapter = ChatAdapter()
        mRecyclerView = findViewById(R.id.chat_recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mChatAdapter

        mInput = findViewById(R.id.chat_input)
        mSendButton = findViewById(R.id.chat_send_button)
    }

    private fun buildToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.chat_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun buildInput() {
        mSendButton.setOnClickListener { sendUserAnswer() }

        mInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendUserAnswer()
                true
            } else {
                false
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

        // Apply category color to toolbar + status bar
        if (category != null) {
            val catColor = findCategoryColor(category.getColor())
            if (catColor != null) {
                val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.chat_toolbar)
                toolbar.setBackgroundColor(catColor.primary)
                window.statusBarColor = catColor.primary
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

    private fun sendUserAnswer() {
        val answer = mInput.text.toString().trim()
        if (answer.isEmpty()) return

        addUserMessage(answer)
        mInput.setText("")

        val correct = answer.equals(mCurrentFlashcard.getTranslation().trim(), ignoreCase = true)

        if (correct) {
            eu.qm.fiszki.HapticFeedback.vibrateCorrect(this)
            mFlashcardRepository.upFlashcardPassStatistic(mCurrentFlashcard)
            mFlashcardRepository.upFlashcardPriority(mCurrentFlashcard)
            addBotMessage(getString(R.string.chat_correct))
        } else {
            eu.qm.fiszki.HapticFeedback.vibrateWrong(this)
            mFlashcardRepository.upFlashcardFailStatistic(mCurrentFlashcard)
            mFlashcardRepository.downFlashcardPriority(mCurrentFlashcard)
            addBotMessage(getString(R.string.chat_wrong, mCurrentFlashcard.getTranslation()))
        }

        mHandler.postDelayed({ drawAndPrompt() }, 800)
    }

    private fun addBotMessage(text: String) {
        mChatAdapter.addMessage(ChatMessage(text, isUser = false))
        scrollToBottom()
    }

    private fun addUserMessage(text: String) {
        mChatAdapter.addMessage(ChatMessage(text, isUser = true))
        scrollToBottom()
    }

    private fun scrollToBottom() {
        mRecyclerView.scrollToPosition(mChatAdapter.getMessageCount() - 1)
    }
}
