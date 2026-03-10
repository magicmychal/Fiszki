package click.quickclicker.fiszki.dialogs.category

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.CATEGORY_COLORS
import click.quickclicker.fiszki.activity.CategoryColor
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import java.io.File

class EditCategoryBottomSheet : BottomSheetDialogFragment() {

    private var categoryId: Int = 0
    private lateinit var category: Category
    private var selectedColor: CategoryColor = defaultCategoryColor()
    private val colorViews = mutableListOf<View>()

    companion object {
        private const val ARG_CATEGORY_ID = "category_id"

        fun newInstance(categoryId: Int): EditCategoryBottomSheet {
            return EditCategoryBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryId = arguments?.getInt(ARG_CATEGORY_ID) ?: return
        val context = requireContext()
        val categoryRepository = CategoryRepository(context)
        category = categoryRepository.getCategoryByID(categoryId) ?: return

        val nameEt = view.findViewById<TextInputEditText>(R.id.edit_category_name)
        val langFromEt = view.findViewById<MaterialAutoCompleteTextView>(R.id.edit_category_lang_from)
        val langOnEt = view.findViewById<MaterialAutoCompleteTextView>(R.id.edit_category_lang_on)

        nameEt.setText(category.getCategory())
        langFromEt.setText(category.getLangFrom() ?: "")
        langOnEt.setText(category.getLangOn() ?: "")

        val languages = context.resources.getStringArray(R.array.support_lang)
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, languages)
        langFromEt.setAdapter(adapter)
        langOnEt.setAdapter(adapter)

        // Save on focus loss or when user navigates away
        nameEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveCategory(nameEt, langFromEt, langOnEt, categoryRepository)
        }
        langFromEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveCategory(nameEt, langFromEt, langOnEt, categoryRepository)
        }
        langOnEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveCategory(nameEt, langFromEt, langOnEt, categoryRepository)
        }

        // Color picker
        selectedColor = findCategoryColor(category.getColor()) ?: defaultCategoryColor()
        val colorContainer = view.findViewById<LinearLayout>(R.id.color_picker_container)
        buildColorPicker(colorContainer, nameEt, langFromEt, langOnEt, categoryRepository)

        // Export CSV button
        val exportBtn = view.findViewById<MaterialButton>(R.id.btn_export_csv)
        exportBtn.setOnClickListener {
            exportCsv()
        }

        // Delete button — only for user-created categories
        val deleteBtn = view.findViewById<MaterialButton>(R.id.btn_delete_category)
        if (category.isEntryByUser) {
            deleteBtn.visibility = View.VISIBLE
            deleteBtn.setOnClickListener {
                showDeleteConfirmation(categoryRepository)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val view = view ?: return
        val context = context ?: return
        val nameEt = view.findViewById<TextInputEditText>(R.id.edit_category_name)
        val langFromEt = view.findViewById<MaterialAutoCompleteTextView>(R.id.edit_category_lang_from)
        val langOnEt = view.findViewById<MaterialAutoCompleteTextView>(R.id.edit_category_lang_on)
        val categoryRepository = CategoryRepository(context)
        saveCategory(nameEt, langFromEt, langOnEt, categoryRepository)
    }

    private fun buildColorPicker(
        container: LinearLayout,
        nameEt: TextInputEditText,
        langFromEt: MaterialAutoCompleteTextView,
        langOnEt: MaterialAutoCompleteTextView,
        categoryRepository: CategoryRepository
    ) {
        colorViews.clear()
        val ctx = requireContext()
        val sizePx = (36 * ctx.resources.displayMetrics.density).toInt()
        val marginPx = (8 * ctx.resources.displayMetrics.density).toInt()
        val strokePx = (3 * ctx.resources.displayMetrics.density).toInt()

        for (catColor in CATEGORY_COLORS) {
            val circleView = View(ctx)
            val params = LinearLayout.LayoutParams(sizePx, sizePx)
            params.marginEnd = marginPx
            circleView.layoutParams = params

            updateCircleDrawable(circleView, catColor, catColor == selectedColor, strokePx)

            circleView.setOnClickListener {
                selectedColor = catColor
                category.setColor(String.format("#%06X", 0xFFFFFF and catColor.primary))
                for ((i, v) in colorViews.withIndex()) {
                    updateCircleDrawable(v, CATEGORY_COLORS[i], CATEGORY_COLORS[i] == selectedColor, strokePx)
                }
                saveCategory(nameEt, langFromEt, langOnEt, categoryRepository)
            }

            colorViews.add(circleView)
            container.addView(circleView)
        }
    }

    private fun updateCircleDrawable(view: View, catColor: CategoryColor, isSelected: Boolean, strokePx: Int) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(catColor.primary)
        if (isSelected) {
            val onSurface = android.util.TypedValue().let { tv ->
                view.context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, tv, true)
                tv.data
            }
            drawable.setStroke(strokePx, onSurface)
        }
        view.background = drawable
    }

    private fun saveCategory(
        nameEt: TextInputEditText,
        langFromEt: MaterialAutoCompleteTextView,
        langOnEt: MaterialAutoCompleteTextView,
        categoryRepository: CategoryRepository
    ) {
        val context = context ?: return
        category.setCategory(nameEt.text.toString().trim())
        category.setLangFrom(langFromEt.text.toString().trim())
        category.setLangOn(langOnEt.text.toString().trim())
        category.setColor(String.format("#%06X", 0xFFFFFF and selectedColor.primary))

        val validation = ValidationCategory(context)
        if (validation.validate(category)) {
            categoryRepository.updateCategory(category)
        }
    }

    private fun exportCsv() {
        val ctx = context ?: return
        val flashcardRepository = FlashcardRepository(ctx)
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(category.id)

        if (flashcards.isEmpty()) {
            Toast.makeText(ctx, R.string.export_csv_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val exportDir = File(ctx.cacheDir, "csv_exports")
        exportDir.mkdirs()
        val fileName = (category.getCategory() ?: "flashcards")
            .replace(Regex("[^a-zA-Z0-9\\-_ ]"), "")
            .take(50)
            .ifEmpty { "flashcards" }
        val csvFile = File(exportDir, "$fileName.csv")

        csvFile.bufferedWriter().use { writer ->
            for (flashcard in flashcards) {
                val word = escapeCsvField(flashcard.getWord())
                val translation = escapeCsvField(flashcard.getTranslation())
                writer.write("$word,$translation")
                writer.newLine()
            }
        }

        val uri = FileProvider.getUriForFile(ctx, "click.quickclicker.fiszki.fileprovider", csvFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun escapeCsvField(value: String): String {
        return if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun showDeleteConfirmation(categoryRepository: CategoryRepository) {
        val ctx = context ?: return
        MaterialAlertDialogBuilder(ctx)
            .setMessage(getString(R.string.edit_category_delete_message))
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
                deleteCategoryWithFlashcards(categoryRepository)
            }
            .setNegativeButton(R.string.button_action_no, null)
            .show()
    }

    private fun deleteCategoryWithFlashcards(categoryRepository: CategoryRepository) {
        val ctx = context ?: return
        val flashcardRepository = FlashcardRepository(ctx)
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(category.id)
        if (flashcards.isNotEmpty()) {
            flashcardRepository.deleteFlashcards(flashcards)
        }
        categoryRepository.deleteCategory(category)

        dismiss()

        val activity = activity ?: return
        val rootView = activity.findViewById<View>(android.R.id.content)

        val colorPrimary = TypedValue().let { tv ->
            activity.theme.resolveAttribute(android.R.attr.colorPrimary, tv, true)
            tv.data
        }

        Snackbar.make(rootView, R.string.snackbar_return_category_message, Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_return_word_button) {
                categoryRepository.addCategory(category)
                if (flashcards.isNotEmpty()) {
                    flashcardRepository.addFlashcards(flashcards)
                }
                activity.onWindowFocusChanged(true)
            }
            .setActionTextColor(colorPrimary)
            .show()

        activity.onWindowFocusChanged(true)
    }
}
