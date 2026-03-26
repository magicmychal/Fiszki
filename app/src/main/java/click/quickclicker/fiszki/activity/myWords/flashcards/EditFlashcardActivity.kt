package click.quickclicker.fiszki.activity.myWords.flashcards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.model.flashcard.ValidationFlashcards
import click.quickclicker.fiszki.ui.FilledTextField
import click.quickclicker.fiszki.ui.OrientationHelper

class EditFlashcardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FLASHCARD_ID = "flashcard_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val flashcardId = intent.getIntExtra(EXTRA_FLASHCARD_ID, 0)

        setContent {
            FiszkiTheme {
                EditFlashcardScreen(
                    flashcardId = flashcardId,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditFlashcardScreen(
    flashcardId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val flashcardRepository = androidx.compose.runtime.remember { FlashcardRepository(context) }
    val categoryRepository = androidx.compose.runtime.remember { CategoryRepository(context) }
    val validation = androidx.compose.runtime.remember { ValidationFlashcards(context) }

    val flashcard = androidx.compose.runtime.remember { flashcardRepository.getFlashcardByID(flashcardId) }
    if (flashcard == null) {
        androidx.compose.runtime.LaunchedEffect(Unit) { onBack() }
        return
    }

    val category = androidx.compose.runtime.remember { categoryRepository.getCategoryByID(flashcard.categoryID) }
    val langFrom = category?.getLangFrom()?.takeIf { it.isNotEmpty() }
    val langOn = category?.getLangOn()?.takeIf { it.isNotEmpty() }

    val catColor = androidx.compose.runtime.remember {
        val cc = category?.getColor()?.let { findCategoryColor(it) } ?: defaultCategoryColor()
        cc
    }
    val catContainerColor = Color(catColor.container or 0xFF000000.toInt())
    val catPrimaryColor = Color(catColor.primary or 0xFF000000.toInt())

    var word by rememberSaveable { mutableStateOf(flashcard.getWord()) }
    var translation by rememberSaveable { mutableStateOf(flashcard.getTranslation()) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    fun save() {
        flashcard.setWord(word.trim())
        flashcard.setTranslation(translation.trim())
        if (validation.validateAdd(flashcard)) {
            flashcardRepository.updateFlashcard(flashcard)
            Toast.makeText(context, R.string.flashcard_edit_toast, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    fun delete() {
        flashcardRepository.deleteFlashcard(flashcard)
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_flashcard_screen_title),
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold,
                        color = catPrimaryColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = catPrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = catContainerColor
                )
            )
        }
    ) { padding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = navBarPadding + 24.dp)
                    .widthIn(max = 500.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Section overline + headline
                Text(
                    text = stringResource(R.string.edit_flashcard_overline).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.edit_flashcard_headline_1),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = RobotoSerifFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.edit_flashcard_headline_2),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = RobotoSerifFamily,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.edit_flashcard_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Source Language field
                val sourceLabel = if (langFrom != null) {
                    stringResource(R.string.edit_flashcard_source_lang_with, langFrom)
                } else {
                    stringResource(R.string.add_flashcard_source_word_label)
                }
                Text(
                    text = sourceLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTextField(
                    value = word,
                    onValueChange = { word = it },
                    placeholder = "",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Translate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Target Language field
                val targetLabel = if (langOn != null) {
                    stringResource(R.string.edit_flashcard_target_lang_with, langOn)
                } else {
                    stringResource(R.string.add_flashcard_translation_label)
                }
                Text(
                    text = targetLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    placeholder = "",
                    trailingIcon = {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tip banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = stringResource(R.string.add_flashcard_tip_label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(start = 4.dp))
                        Text(
                            text = stringResource(R.string.edit_flashcard_tip_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Changes button
                Button(
                    onClick = { save() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.edit_flashcard_save_button).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete Word button
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(
                        text = stringResource(R.string.edit_flashcard_delete_button).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            text = { Text(stringResource(R.string.flashcard_delete_message)) },
            confirmButton = {
                TextButton(onClick = { delete() }) {
                    Text(stringResource(R.string.button_action_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.button_action_no))
                }
            }
        )
    }
}
