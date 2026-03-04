package eu.qm.fiszki.activity.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.exam.SetRangeExamDialog
import eu.qm.fiszki.dialogs.exam.SetRepeatExamDialog
import eu.qm.fiszki.listeners.exam.ExamGoExaming

class ExamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_exam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildToolbar(view)
        buildFAB(view)
        buildClickHandlers(view)
    }

    private fun buildToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exam_toolbar_title)
    }

    private fun buildFAB(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(ExamGoExaming(requireActivity()))
    }

    private fun buildClickHandlers(view: View) {
        view.findViewById<View>(R.id.exam_range).setOnClickListener {
            SetRangeExamDialog(requireActivity()).show()
        }
        view.findViewById<View>(R.id.exam_repeat).setOnClickListener {
            SetRepeatExamDialog(requireActivity()).show()
        }
    }
}
