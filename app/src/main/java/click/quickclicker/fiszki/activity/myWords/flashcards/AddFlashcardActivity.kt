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
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.model.flashcard.ValidationFlashcards
import click.quickclicker.fiszki.ui.FilledTextField
import click.quickclicker.fiszki.ui.OrientationHelper

class AddFlashcardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY_ID = "category_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, 0)

        setContent {
            FiszkiTheme {
                AddFlashcardScreen(
                    categoryId = categoryId,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFlashcardScreen(
    categoryId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val flashcardRepository = remember { FlashcardRepository(context) }
    val categoryRepository = remember { CategoryRepository(context) }
    val validation = remember { ValidationFlashcards(context) }

    val catColor = remember(categoryId) {
        val category = categoryRepository.getCategoryByID(categoryId)
        val cc = category?.getColor()?.let { findCategoryColor(it) } ?: defaultCategoryColor()
        cc
    }
    val catContainerColor = Color(catColor.container or 0xFF000000.toInt())
    val catPrimaryColor = Color(catColor.primary or 0xFF000000.toInt())

    var word by rememberSaveable { mutableStateOf("") }
    var translation by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    fun addFlashcard() {
        val flashcard = Flashcard().apply {
            setWord(word.trim())
            setTranslation(translation.trim())
            this.categoryID = categoryId
            priority = 1
        }
        if (validation.validateAdd(flashcard)) {
            flashcardRepository.addFlashcard(flashcard)
            Toast.makeText(context, R.string.add_new_flashcard_toast, Toast.LENGTH_SHORT).show()
            word = ""
            translation = ""
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_flashcard_screen_title),
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
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(24.dp))

                // Editorial headline
                Text(
                    text = stringResource(R.string.add_flashcard_headline_1),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = RobotoSerifFamily,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.add_flashcard_headline_2),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = RobotoSerifFamily,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                            text = stringResource(R.string.add_flashcard_tip_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Source Word
                Text(
                    text = stringResource(R.string.add_flashcard_source_word_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTextField(
                    value = word,
                    onValueChange = { word = it },
                    placeholder = stringResource(R.string.add_flashcard_source_word_hint),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.add_flashcard_source_word_helper),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Translation
                Text(
                    text = stringResource(R.string.add_flashcard_translation_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    placeholder = stringResource(R.string.add_flashcard_translation_hint)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.add_flashcard_translation_helper),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ADD TO SET button
                Button(
                    onClick = { addFlashcard() },
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
                        text = stringResource(R.string.add_flashcard_button).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Icon(
                        Icons.Default.AddBox,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
