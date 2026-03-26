package click.quickclicker.fiszki.activity.myWords.category

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.BentoLanguageSelector
import click.quickclicker.fiszki.ui.FilledTextField
import click.quickclicker.fiszki.ui.LanguagePickerSheet
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.ui.SectionHeader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class EditSetActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
    }

    private lateinit var category: Category
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var flashcardRepository: FlashcardRepository

    // Mutable state bridged to Compose
    private val nameState = mutableStateOf("")
    private val langFromState = mutableStateOf("")
    private val langOnState = mutableStateOf("")

    private val csvPickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importCsv(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, 0)
        categoryRepository = CategoryRepository(this)
        flashcardRepository = FlashcardRepository(this)
        category = categoryRepository.getCategoryByID(categoryId) ?: run {
            finish()
            return
        }

        nameState.value = category.getCategory()
        langFromState.value = category.getLangFrom() ?: ""
        langOnState.value = category.getLangOn() ?: ""

        val catColor = findCategoryColor(category.getColor()) ?: defaultCategoryColor()
        val containerColor = Color(catColor.container or 0xFF000000.toInt())
        val primaryColor = Color(catColor.primary or 0xFF000000.toInt())

        setContent {
            FiszkiTheme {
                EditSetScreen(
                    categoryContainerColor = containerColor,
                    categoryPrimaryColor = primaryColor,
                    name = nameState.value,
                    onNameChange = { nameState.value = it },
                    langFrom = langFromState.value,
                    onLangFromChange = { langFromState.value = it },
                    langOn = langOnState.value,
                    onLangOnChange = { langOnState.value = it },
                    showDelete = category.isEntryByUser,
                    onClose = { finish() },
                    onSave = { saveCategory() },
                    onImportCsv = {
                        csvPickerLauncher.launch(
                            arrayOf("text/csv", "text/comma-separated-values", "text/plain")
                        )
                    },
                    onExportCsv = { exportCsv() },
                    onDelete = { showDeleteConfirmation() }
                )
            }
        }
    }

    private fun saveCategory() {
        category.setCategory(nameState.value.trim())
        category.setLangFrom(langFromState.value.trim())
        category.setLangOn(langOnState.value.trim())

        // Preserve existing color
        val existingColor = findCategoryColor(category.getColor()) ?: defaultCategoryColor()
        category.setColor(String.format("#%06X", 0xFFFFFF and existingColor.primary))

        val validation = ValidationCategory(this)
        if (validation.validate(category)) {
            categoryRepository.updateCategory(category)
            Toast.makeText(this, R.string.edit_category_toast, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun exportCsv() {
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(category.id)

        if (flashcards.isEmpty()) {
            Toast.makeText(this, R.string.export_csv_empty, Toast.LENGTH_SHORT).show()
            return
        }

        val exportDir = File(cacheDir, "csv_exports")
        exportDir.mkdirs()
        val fileName = (category.getCategory())
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

        val uri = FileProvider.getUriForFile(this, "click.quickclicker.fiszki.fileprovider", csvFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun importCsv(uri: Uri) {
        try {
            val rows = mutableListOf<Pair<String, String>>()
            contentResolver.openInputStream(uri)?.use { inputStream ->
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
                Toast.makeText(this, R.string.import_set_csv_error_read, Toast.LENGTH_SHORT).show()
                return
            }

            if (rows.isEmpty()) {
                Toast.makeText(this, R.string.import_set_csv_error_empty, Toast.LENGTH_SHORT).show()
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

            flashcardRepository.addFlashcards(flashcards)
            Toast.makeText(this, getString(R.string.import_set_csv_success, flashcards.size), Toast.LENGTH_SHORT).show()
        } catch (e: CsvFormatException) {
            Toast.makeText(this, R.string.import_set_csv_error_format, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.import_set_csv_error_read, Toast.LENGTH_SHORT).show()
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
        MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.edit_category_delete_message))
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
                deleteCategoryWithFlashcards()
            }
            .setNegativeButton(R.string.button_action_no, null)
            .show()
    }

    private fun deleteCategoryWithFlashcards() {
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(category.id)
        if (flashcards.isNotEmpty()) {
            flashcardRepository.deleteFlashcards(flashcards)
        }
        categoryRepository.deleteCategory(category)
        setResult(RESULT_OK)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSetScreen(
    categoryContainerColor: Color,
    categoryPrimaryColor: Color,
    name: String,
    onNameChange: (String) -> Unit,
    langFrom: String,
    onLangFromChange: (String) -> Unit,
    langOn: String,
    onLangOnChange: (String) -> Unit,
    showDelete: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onImportCsv: () -> Unit,
    onExportCsv: () -> Unit,
    onDelete: () -> Unit
) {
    val languages = stringArrayResource(R.array.support_lang).toList()
    var showLangFromPicker by rememberSaveable { mutableStateOf(false) }
    var showLangOnPicker by rememberSaveable { mutableStateOf(false) }

    val selectLanguageLabel = stringResource(R.string.create_set_select_language)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_set_title),
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold,
                        color = categoryPrimaryColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = categoryPrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = categoryContainerColor
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .padding(bottom = navBarPadding)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = stringResource(R.string.edit_set_save),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .widthIn(max = 500.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Section 1: Details
            SectionHeader(
                overline = stringResource(R.string.edit_set_details_label),
                headline = stringResource(R.string.edit_set_general_settings)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Set Name label
            Text(
                text = stringResource(R.string.category_dialog_et_name),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            FilledTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.create_set_name_hint)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Source Language bento
            BentoLanguageSelector(
                icon = Icons.Default.Language,
                label = stringResource(R.string.create_set_source_language),
                value = langFrom.ifEmpty { selectLanguageLabel },
                onClick = { showLangFromPicker = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Target Language bento
            BentoLanguageSelector(
                icon = Icons.Default.Translate,
                label = stringResource(R.string.create_set_target_language),
                value = langOn.ifEmpty { selectLanguageLabel },
                onClick = { showLangOnPicker = true }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Section 2: Data Management
            SectionHeader(
                overline = stringResource(R.string.edit_set_data_management),
                headline = stringResource(R.string.edit_set_csv_workflow)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.edit_set_csv_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Import CSV card
            CsvActionCard(
                icon = Icons.Default.UploadFile,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                title = stringResource(R.string.edit_set_import_csv),
                subtitle = stringResource(R.string.edit_set_import_csv_formats),
                onClick = onImportCsv
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Export CSV card
            CsvActionCard(
                icon = Icons.Default.Download,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                title = stringResource(R.string.edit_set_export_csv),
                subtitle = stringResource(R.string.edit_set_export_csv_subtitle),
                onClick = onExportCsv
            )

            // Danger Zone
            if (showDelete) {
                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                text = stringResource(R.string.edit_set_danger_zone),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                text = stringResource(R.string.edit_set_delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Bottom clearance for the fixed bottom bar
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Language picker sheets
    if (showLangFromPicker) {
        LanguagePickerSheet(
            languages = languages,
            onSelect = {
                onLangFromChange(it)
                showLangFromPicker = false
            },
            onDismiss = { showLangFromPicker = false }
        )
    }

    if (showLangOnPicker) {
        LanguagePickerSheet(
            languages = languages,
            onSelect = {
                onLangOnChange(it)
                showLangOnPicker = false
            },
            onDismiss = { showLangOnPicker = false }
        )
    }
}

@Composable
private fun CsvActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconContainerColor: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = iconContainerColor,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun stringArrayResource(id: Int): Array<String> {
    val context = androidx.compose.ui.platform.LocalContext.current
    return androidx.compose.runtime.remember { context.resources.getStringArray(id) }
}
