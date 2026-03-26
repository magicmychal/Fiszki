package click.quickclicker.fiszki.activity.myWords.category

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.ui.BentoLanguageSelector
import click.quickclicker.fiszki.ui.FilledTextField
import click.quickclicker.fiszki.ui.LanguagePickerSheet
import click.quickclicker.fiszki.ui.OrientationHelper

class CreateSetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        setContent {
            FiszkiTheme {
                CreateSetScreen(onClose = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSetScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val categoryRepository = remember { CategoryRepository(context) }
    val validation = remember { ValidationCategory(context) }
    val languages = remember { context.resources.getStringArray(R.array.support_lang).toList() }

    var name by rememberSaveable { mutableStateOf("") }
    var langFrom by rememberSaveable { mutableStateOf("") }
    var langOn by rememberSaveable { mutableStateOf("") }

    var showLangFromPicker by remember { mutableStateOf(false) }
    var showLangOnPicker by remember { mutableStateOf(false) }

    val selectLanguageLabel = stringResource(R.string.create_set_select_language)

    fun createSet() {
        val selectedColor = defaultCategoryColor()
        val category = Category().apply {
            setCategory(name.trim())
            isEntryByUser = true
            setLangOn(langOn.trim())
            setLangFrom(langFrom.trim())
            setColor(String.format("#%06X", 0xFFFFFF and selectedColor.primary))
        }
        if (validation.validate(category)) {
            categoryRepository.addCategory(category)
            Toast.makeText(context, R.string.category_toast, Toast.LENGTH_SHORT).show()
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_set_title),
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = navBarPadding + 24.dp)
                .widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Info banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Text(
                        text = stringResource(R.string.create_set_info_banner),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Section number + title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "01",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = RobotoSerifFamily,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(start = 12.dp))
                        Text(
                            text = stringResource(R.string.create_set_details_section),
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = RobotoSerifFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SET NAME label
                    Text(
                        text = stringResource(R.string.category_dialog_et_name).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Filled text field
                    FilledTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = stringResource(R.string.create_set_name_hint)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language selectors in their own inner card
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Source language bento
                            BentoLanguageSelector(
                                icon = Icons.Default.Language,
                                label = stringResource(R.string.create_set_source_language),
                                value = langFrom.ifEmpty { selectLanguageLabel },
                                onClick = { showLangFromPicker = true }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Target language bento
                            BentoLanguageSelector(
                                icon = Icons.Default.Translate,
                                label = stringResource(R.string.create_set_target_language),
                                value = langOn.ifEmpty { selectLanguageLabel },
                                onClick = { showLangOnPicker = true }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Set CTA button
            Button(
                onClick = { createSet() },
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
                    text = stringResource(R.string.create_set_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = RobotoSerifFamily,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(text = "\u2728", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Helper text
            Text(
                text = stringResource(R.string.create_set_helper),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        }
    }

    // Language picker sheets
    if (showLangFromPicker) {
        LanguagePickerSheet(
            languages = languages,
            onSelect = {
                langFrom = it
                showLangFromPicker = false
            },
            onDismiss = { showLangFromPicker = false }
        )
    }

    if (showLangOnPicker) {
        LanguagePickerSheet(
            languages = languages,
            onSelect = {
                langOn = it
                showLangOnPicker = false
            },
            onDismiss = { showLangOnPicker = false }
        )
    }
}

