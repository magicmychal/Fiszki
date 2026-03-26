package click.quickclicker.fiszki.dialogs.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.CATEGORY_COLORS
import click.quickclicker.fiszki.activity.CategoryColor
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.category.ValidationCategory
import click.quickclicker.fiszki.ui.ColorPickerRow
import click.quickclicker.fiszki.ui.LanguageDropdown

class AddCategoryDialogFragment : DialogFragment() {

    var onDismissed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FiszkiTheme {
                    AddCategoryDialogContent(
                        onAdd = {
                            dismiss()
                            onDismissed?.invoke()
                        },
                        onCancel = {
                            dismiss()
                            onDismissed?.invoke()
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

@Composable
private fun AddCategoryDialogContent(
    onAdd: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val categoryRepository = remember { CategoryRepository(context) }
    val validation = remember { ValidationCategory(context) }
    val languages = remember { context.resources.getStringArray(R.array.support_lang).toList() }

    var name by remember { mutableStateOf("") }
    var langFrom by remember { mutableStateOf("") }
    var langOn by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(defaultCategoryColor()) }

    fun addCategory() {
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
            onAdd()
        }
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.category_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = RobotoSerifFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.category_dialog_et_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                LanguageDropdown(
                    value = langFrom,
                    onValueChange = { langFrom = it },
                    label = stringResource(R.string.category_dialog_lang_from),
                    languages = languages,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                LanguageDropdown(
                    value = langOn,
                    onValueChange = { langOn = it },
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
                selected = selectedColor,
                onSelect = { selectedColor = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { addCategory() }) {
                    Text(stringResource(R.string.category_positive_btn_text))
                }
            }
        }
    }
}
