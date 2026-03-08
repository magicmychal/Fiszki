package eu.qm.fiszki.dialogs.csv

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import eu.qm.fiszki.R
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository
import java.io.BufferedReader
import java.io.InputStreamReader

class CsvImportBottomSheet : BottomSheetDialogFragment() {

    private enum class ImportFormat(val labelRes: Int, val helperRes: Int) {
        CSV(R.string.import_format_csv, R.string.import_format_csv_helper),
        ANKIDROID_TSV(R.string.import_format_ankidroid, R.string.import_format_ankidroid_helper)
    }

    private var parsedRows: List<Pair<String, String>> = emptyList()
    private var categories: List<Category> = emptyList()
    private var selectedCategory: Category? = null
    private var selectedFormat: ImportFormat = ImportFormat.CSV

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                parseFile(uri, selectedFormat)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_csv_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatDropdown = view.findViewById<MaterialAutoCompleteTextView>(R.id.csv_format_dropdown)
        val formatHelper = view.findViewById<TextView>(R.id.csv_format_helper)
        val pickFileButton = view.findViewById<MaterialButton>(R.id.csv_pick_file_button)
        val statusText = view.findViewById<TextView>(R.id.csv_status_text)
        val categoryLayout = view.findViewById<TextInputLayout>(R.id.csv_category_layout)
        val categoryDropdown = view.findViewById<MaterialAutoCompleteTextView>(R.id.csv_category_dropdown)
        val importButton = view.findViewById<MaterialButton>(R.id.csv_import_button)

        val formats = ImportFormat.values().toList()
        val formatLabels = formats.map { getString(it.labelRes) }
        formatDropdown.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, formatLabels)
        )
        formatDropdown.setText(getString(selectedFormat.labelRes), false)
        formatHelper.text = getString(selectedFormat.helperRes)
        formatDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedFormat = formats[position]
            formatHelper.text = getString(selectedFormat.helperRes)
            resetUI()
            statusText.visibility = View.GONE
            categoryLayout.visibility = View.GONE
            importButton.visibility = View.GONE
        }

        pickFileButton.setOnClickListener {
            filePickerLauncher.launch(
                arrayOf(
                    "text/csv",
                    "text/tab-separated-values",
                    "text/plain",
                    "text/*"
                )
            )
        }

        categoryDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
            importButton.visibility = View.VISIBLE
        }

        importButton.setOnClickListener {
            performImport()
        }
    }

    private fun parseFile(uri: Uri, format: ImportFormat) {
        val ctx = context ?: return
        val view = view ?: return

        val statusText = view.findViewById<TextView>(R.id.csv_status_text)
        val categoryLayout = view.findViewById<TextInputLayout>(R.id.csv_category_layout)
        val categoryDropdown = view.findViewById<MaterialAutoCompleteTextView>(R.id.csv_category_dropdown)
        val importButton = view.findViewById<MaterialButton>(R.id.csv_import_button)

        try {
            val rows = mutableListOf<Pair<String, String>>()
            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    var firstLine = true
                    reader.forEachLine { rawLine ->
                        var line = rawLine
                        if (firstLine) {
                            line = line.removePrefix("\uFEFF")
                            firstLine = false
                        }
                        if (line.isBlank()) return@forEachLine
                        if (format == ImportFormat.ANKIDROID_TSV && line.startsWith("#")) {
                            return@forEachLine
                        }

                        val columns = when (format) {
                            ImportFormat.CSV -> parseCsvLine(line)
                            ImportFormat.ANKIDROID_TSV -> parseTsvLine(line)
                        }

                        val word: String
                        val translation: String
                        when (format) {
                            ImportFormat.CSV -> {
                                if (columns.size != 2) throw CsvFormatException()
                                word = columns[0].trim()
                                translation = columns[1].trim()
                            }
                            ImportFormat.ANKIDROID_TSV -> {
                                if (columns.size < 2) throw TsvFormatException()
                                word = columns[0].trim()
                                translation = columns[1].trim()
                            }
                        }

                        if (word.isEmpty() || translation.isEmpty()) {
                            when (format) {
                                ImportFormat.CSV -> throw CsvFormatException()
                                ImportFormat.ANKIDROID_TSV -> throw TsvFormatException()
                            }
                        }
                        rows.add(word to translation)
                    }
                }
            } ?: run {
                Toast.makeText(ctx, R.string.import_csv_error_read, Toast.LENGTH_SHORT).show()
                return
            }

            if (rows.isEmpty()) {
                Toast.makeText(ctx, R.string.import_csv_error_empty, Toast.LENGTH_SHORT).show()
                resetUI()
                return
            }

            parsedRows = rows
            statusText.text = getString(R.string.import_csv_found, rows.size)
            statusText.visibility = View.VISIBLE

            categories = CategoryRepository(ctx).getAllCategory()
            val categoryNames = categories.map { it.getCategory() }
            val adapter = ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, categoryNames)
            categoryDropdown.setAdapter(adapter)
            categoryLayout.visibility = View.VISIBLE

            selectedCategory = null
            importButton.visibility = View.GONE

        } catch (e: CsvFormatException) {
            Toast.makeText(ctx, R.string.import_csv_error_format, Toast.LENGTH_SHORT).show()
            resetUI()
        } catch (e: TsvFormatException) {
            Toast.makeText(ctx, R.string.import_tsv_error_format, Toast.LENGTH_SHORT).show()
            resetUI()
        } catch (e: Exception) {
            Toast.makeText(ctx, R.string.import_csv_error_read, Toast.LENGTH_SHORT).show()
            resetUI()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            when {
                inQuotes -> {
                    if (c == '"') {
                        if (i + 1 < line.length && line[i + 1] == '"') {
                            current.append('"')
                            i++
                        } else {
                            inQuotes = false
                        }
                    } else {
                        current.append(c)
                    }
                }
                c == '"' -> inQuotes = true
                c == ',' -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(c)
            }
            i++
        }
        result.add(current.toString())
        return result
    }

    private fun parseTsvLine(line: String): List<String> = line.split('\t')

    private fun performImport() {
        val ctx = context ?: return
        val category = selectedCategory ?: return
        if (parsedRows.isEmpty()) return

        val flashcards = ArrayList<Flashcard>()
        for ((word, translation) in parsedRows) {
            val flashcard = Flashcard().apply {
                setWord(word)
                setTranslation(translation)
                categoryID = category.id
                priority = 0
            }
            flashcards.add(flashcard)
        }

        FlashcardRepository(ctx).addFlashcards(flashcards)
        Toast.makeText(ctx, getString(R.string.import_csv_success, flashcards.size), Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun resetUI() {
        val view = view ?: return
        parsedRows = emptyList()
        selectedCategory = null
        view.findViewById<TextView>(R.id.csv_status_text).visibility = View.GONE
        view.findViewById<TextInputLayout>(R.id.csv_category_layout).visibility = View.GONE
        view.findViewById<MaterialButton>(R.id.csv_import_button).visibility = View.GONE
    }

    private class CsvFormatException : Exception()
    private class TsvFormatException : Exception()
}
