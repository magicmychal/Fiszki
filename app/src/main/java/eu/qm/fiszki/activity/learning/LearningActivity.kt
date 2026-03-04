package eu.qm.fiszki.activity.learning

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.dialogs.learning.ByCategoryLearningDialog
import eu.qm.fiszki.dialogs.learning.ByLanguageLearningDialog
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningActivity : AppCompatActivity() {

    private lateinit var mFlashcardRepository: FlashcardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_learning)

        buildToolbar()
        mFlashcardRepository = FlashcardRepository(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.learning_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun onAllClick(view: View) {
        ChangeActivityManager(this).goToLearningCheck(mFlashcardRepository.getAllFlashcards())
    }

    fun onCategoryClick(view: View) {
        ByCategoryLearningDialog(this).show()
    }

    fun onLangClick(view: View) {
        ByLanguageLearningDialog(this).show()
    }
}
