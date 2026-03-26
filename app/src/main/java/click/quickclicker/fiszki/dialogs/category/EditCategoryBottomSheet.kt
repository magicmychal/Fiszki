package click.quickclicker.fiszki.dialogs.category

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.CATEGORY_COLORS
import click.quickclicker.fiszki.activity.CategoryColor
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.ColorPickerRow
import click.quickclicker.fiszki.ui.LanguageDropdown
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class EditCategoryBottomSheet : BottomSheetDialogFragment() {

    private var categoryId: Int = 0
    private lateinit var category: Category
    private var selectedColor: CategoryColor = defaultCategoryColor()

    // Mutable state for Compose → Shell bridge
    private var currentName: String = ""
    private var currentLangFrom: String = ""
    private var currentLangOn: String = ""

    private val csvPickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importCsv(uri)
        }

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
    ): View {
        categoryId = arguments?.getInt(ARG_CATEGORY_ID) ?: 0
        val ctx = requireContext()
        val categoryRepository = CategoryRepository(ctx)
        category = categoryRepository.getCategoryByID(categoryId) ?: Category()
        selectedColor = findCategoryColor(category.getColor()) ?: defaultCategoryColor()

        currentName = category.getCategory() ?: ""
        currentLangFrom = category.getLangFrom() ?: ""
        currentLangOn = category.getLangOn() ?: ""

        return ComposeView(ctx).apply {
            setContent {
                FiszkiTheme {
                    EditCategorySheetContent(
                        category = category,
                        selectedColor = selectedColor,
                        name = currentName,
                        onNameChange = { currentName = it },
                        langFrom = currentLangFrom,
                        onLangFromChange = { currentLangFrom = it },
                        langOn = currentLangOn,
                        onLangOnChange = { currentLangOn = it },
                        onColorSelect = {
                            selectedColor = it
                            saveCategory()
                        },
                        languages = ctx.resources.getStringArray(R.array.support_lang).toList(),
                        onExportCsv = { exportCsv() },
                        onImportCsv = {
                            csvPickerLauncher.launch(
                                arrayOf("text/csv", "text/comma-separated-values", "text/plain")
                            )
                        },
                        showDelete = category.isEntryByUser,
                        onDelete = {
                            showDeleteConfirmation()
                        }
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveCategory()
    }

    private fun saveCategory() {
        val ctx = context ?: return
        category.setCategory(currentName.trim())
        category.setLangFrom(currentLangFrom.trim())
        category.setLangOn(currentLangOn.trim())
        category.setColor(String.format("#%06X", 0xFFFFFF and selectedColor.primary))

        val validation = ValidationCategory(ctx)
        if (validation.validate(category)) {
            CategoryRepository(ctx).updateCategory(category)
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

    private fun importCsv(uri: Uri) {
        val ctx = context ?: return

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

                        val columns = parseCsvLine(line)
                        if (columns.size != 2) {
                            throw CsvFormatException()
                        }
                        val word = columns[0].trim()
                        val translation = columns[1].trim()
                        if (word.isEmpty() || translation.isEmpty()) {
                            throw CsvFormatException()
                        }
                        rows.add(word to translation)
                    }
                }
            } ?: run {
                Toast.makeText(ctx, R.string.import_set_csv_error_read, Toast.LENGTH_SHORT).show()
                return
            }

            if (rows.isEmpty()) {
                Toast.makeText(ctx, R.string.import_set_csv_error_empty, Toast.LENGTH_SHORT).show()
                return
            }

            val flashcards = ArrayList<Flashcard>()
            for ((word, translation) in rows) {
                flashcards.add(Flashcard().apply {
                    setWord(word)
                    setTranslation(translation)
                    categoryID = category.id
                    priority = 0
                })
            }

            FlashcardRepository(ctx).addFlashcards(flashcards)
            Toast.makeText(ctx, getString(R.string.import_set_csv_success, flashcards.size), Toast.LENGTH_SHORT).show()
            dismiss()
        } catch (e: CsvFormatException) {
            Toast.makeText(ctx, R.string.import_set_csv_error_format, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(ctx, R.string.import_set_csv_error_read, Toast.LENGTH_SHORT).show()
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

    private class CsvFormatException : Exception()

    private fun escapeCsvField(value: String): String {
        return if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun showDeleteConfirmation() {
        val ctx = context ?: return
        com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
            .setMessage(getString(R.string.edit_category_delete_message))
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
                deleteCategoryWithFlashcards()
            }
            .setNegativeButton(R.string.button_action_no, null)
            .show()
    }

    private fun deleteCategoryWithFlashcards() {
        val ctx = context ?: return
        val categoryRepository = CategoryRepository(ctx)
        val flashcardRepository = FlashcardRepository(ctx)
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(category.id)
        if (flashcards.isNotEmpty()) {
            flashcardRepository.deleteFlashcards(flashcards)
        }
        categoryRepository.deleteCategory(category)
        dismiss()
    }
}

@Composable
private fun EditCategorySheetContent(
    category: Category,
    selectedColor: CategoryColor,
    name: String,
    onNameChange: (String) -> Unit,
    langFrom: String,
    onLangFromChange: (String) -> Unit,
    langOn: String,
    onLangOnChange: (String) -> Unit,
    onColorSelect: (CategoryColor) -> Unit,
    languages: List<String>,
    onExportCsv: () -> Unit,
    onImportCsv: () -> Unit,
    showDelete: Boolean,
    onDelete: () -> Unit
) {
    var nameState by remember { mutableStateOf(name) }
    var langFromState by remember { mutableStateOf(langFrom) }
    var langOnState by remember { mutableStateOf(langOn) }
    var colorState by remember { mutableStateOf(selectedColor) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drag handle
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .then(
                        Modifier.background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.edit_category_title),
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = RobotoSerifFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
                onNameChange(it)
            },
            label = { Text(stringResource(R.string.category_dialog_et_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            LanguageDropdown(
                value = langFromState,
                onValueChange = {
                    langFromState = it
                    onLangFromChange(it)
                },
                label = stringResource(R.string.category_dialog_lang_from),
                languages = languages,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            LanguageDropdown(
                value = langOnState,
                onValueChange = {
                    langOnState = it
                    onLangOnChange(it)
                },
                label = stringResource(R.string.category_dialog_lang_on),
                languages = languages,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.edit_category_color),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        ColorPickerRow(
            colors = CATEGORY_COLORS,
            selected = colorState,
            onSelect = {
                colorState = it
                onColorSelect(it)
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onExportCsv) {
                Text(stringResource(R.string.export_set_csv))
            }
            OutlinedButton(onClick = onImportCsv) {
                Text(stringResource(R.string.import_set_csv))
            }
        }

        if (showDelete) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.edit_category_delete_button),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
