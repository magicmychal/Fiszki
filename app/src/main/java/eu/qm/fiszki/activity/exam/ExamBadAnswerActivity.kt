package eu.qm.fiszki.activity.exam

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.flashcard.Flashcard

class ExamBadAnswerActivity : AppCompatActivity() {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var mActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_exam_bad_answer)
        mActivity = this
        @Suppress("UNCHECKED_CAST")
        badAnswer = intent.getSerializableExtra(ChangeActivityManager.EXAM_BAD_ANSWER_KEY_INTENT) as ArrayList<*>
        buildToolbar()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.container)
        mViewPager.adapter = mSectionsPagerAdapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        ChangeActivityManager(mActivity).exitExamBadAnswer()
    }

    fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exam_bad_answer_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_exam_bad_answer, container, false)
            val cunt = rootView.findViewById<TextView>(R.id.exam_bad_answer_cunt)
            val word = rootView.findViewById<TextView>(R.id.exam_bad_answer_word)
            val answer = rootView.findViewById<TextView>(R.id.exam_bad_answer_uncorrect)
            val correct = rootView.findViewById<TextView>(R.id.exam_bad_answer_correct)

            val position = requireArguments().getInt(POSITION)
            @Suppress("UNCHECKED_CAST")
            val entry = badAnswer!![position] as ArrayList<*>
            val flashcard = entry[0] as Flashcard

            cunt.text = "${position + 1} ${getString(R.string.exam_bad_answer_cunt)} ${badAnswer!!.size}"
            word.text = flashcard.getWord()
            answer.text = entry[1] as String
            correct.text = flashcard.getTranslation()
            return rootView
        }

        companion object {
            private const val POSITION = "POSITION"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                return PlaceholderFragment().apply {
                    arguments = Bundle().apply {
                        putInt(POSITION, sectionNumber)
                    }
                }
            }
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return badAnswer!!.size
        }
    }

    companion object {
        private var badAnswer: ArrayList<*>? = null
    }
}
