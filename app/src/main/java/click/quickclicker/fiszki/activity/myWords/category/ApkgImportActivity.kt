package click.quickclicker.fiszki.activity.myWords.category

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.`import`.AnkiNote
import click.quickclicker.fiszki.`import`.AnkiNoteType
import click.quickclicker.fiszki.`import`.ApkgParseException
import click.quickclicker.fiszki.`import`.ApkgParseResult
import click.quickclicker.fiszki.`import`.ApkgParser
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.ui.OrientationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApkgImportActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URI = "apkg_uri"
        const val EXTRA_CATEGORY_ID = "category_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        val uriString = intent.getStringExtra(EXTRA_URI)
        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, 0)

        if (uriString == null) {
            finish()
            return
        }

        val uri = Uri.parse(uriString)
        val flashcardRepository = FlashcardRepository(this)

        setContent {
            FiszkiTheme {
                ApkgImportScreen(
                    uri = uri,
                    onImport = { notes, frontIndex, backIndex ->
                        val flashcards = ArrayList<Flashcard>()
                        for (note in notes) {
                            val word = note.fields.getOrElse(frontIndex) { "" }.trim()
                            val translation = note.fields.getOrElse(backIndex) { "" }.trim()
                            if (word.isNotEmpty() && translation.isNotEmpty()) {
                                flashcards.add(Flashcard().apply {
                                    setWord(word)
                                    setTranslation(translation)
                                    this.categoryID = categoryId
                                    priority = 0
                                })
                            }
                        }
                        if (flashcards.isNotEmpty()) {
                            flashcardRepository.addFlashcards(flashcards)
                        }
                        flashcards.size
                    },
                    onSuccess = { count ->
                        Toast.makeText(
                            this,
                            getString(R.string.import_anki_success, count),
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

private enum class FieldMapping { FRONT, BACK, IGNORE }

private enum class ImportState { LOADING, CONFIGURE, ERROR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApkgImportScreen(
    uri: Uri,
    onImport: (List<AnkiNote>, Int, Int) -> Int,
    onSuccess: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var state by rememberSaveable { mutableStateOf(ImportState.LOADING) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var parseResult by remember { mutableStateOf<ApkgParseResult?>(null) }

    LaunchedEffect(uri) {
        try {
            val result = withContext(Dispatchers.IO) {
                ApkgParser(context).parse(uri)
            }
            if (result.notesByType.values.all { it.isEmpty() }) {
                errorMessage = context.getString(R.string.import_anki_error_empty)
                state = ImportState.ERROR
            } else {
                parseResult = result
                state = ImportState.CONFIGURE
            }
        } catch (e: ApkgParseException) {
            errorMessage = e.message ?: context.getString(R.string.import_anki_error_parse)
            state = ImportState.ERROR
        } catch (_: Exception) {
            errorMessage = context.getString(R.string.import_anki_error_parse)
            state = ImportState.ERROR
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.import_anki_title),
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = state,
            modifier = Modifier.padding(padding),
            label = "import_state"
        ) { currentState ->
            when (currentState) {
                ImportState.LOADING -> LoadingContent()
                ImportState.ERROR -> ErrorContent(errorMessage, onBack)
                ImportState.CONFIGURE -> {
                    val result = parseResult
                    if (result != null) {
                        ConfigureContent(
                            result = result,
                            onImport = onImport,
                            onSuccess = onSuccess,
                            onBack = onBack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WavyCircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.import_anki_loading),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WavyCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color,
    trackColor: androidx.compose.ui.graphics.Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wavy")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.1f
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val waveAmplitude = strokeWidth * 0.8f
        val arcSweep = 270f
        val waveCount = 4

        // Draw track — smooth wavy circle
        val trackPath = Path()
        val trackSteps = 240
        for (i in 0..trackSteps) {
            val fraction = i.toFloat() / trackSteps
            val angleDeg = fraction * 360f
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val wave = waveAmplitude * kotlin.math.sin(waveCount * fraction * 2.0 * Math.PI + wavePhase).toFloat()
            val r = radius + wave
            val x = center.x + r * kotlin.math.cos(angleRad).toFloat()
            val y = center.y + r * kotlin.math.sin(angleRad).toFloat()
            if (i == 0) trackPath.moveTo(x, y) else trackPath.lineTo(x, y)
        }
        trackPath.close()
        drawPath(
            path = trackPath,
            color = trackColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw active arc — smooth wavy segment
        val activePath = Path()
        val activeSteps = 180
        for (i in 0..activeSteps) {
            val fraction = i.toFloat() / activeSteps
            val angleDeg = rotation + fraction * arcSweep
            val globalFraction = (angleDeg % 360f) / 360f
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val wave = waveAmplitude * kotlin.math.sin(waveCount * globalFraction * 2.0 * Math.PI + wavePhase).toFloat()
            val r = radius + wave
            val x = center.x + r * kotlin.math.cos(angleRad).toFloat()
            val y = center.y + r * kotlin.math.sin(angleRad).toFloat()
            if (i == 0) activePath.moveTo(x, y) else activePath.lineTo(x, y)
        }
        drawPath(
            path = activePath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.import_anki_back))
            }
        }
    }
}

@Composable
private fun ConfigureContent(
    result: ApkgParseResult,
    onImport: (List<AnkiNote>, Int, Int) -> Int,
    onSuccess: (Int) -> Unit,
    onBack: () -> Unit
) {
    val noteTypes = result.noteTypes
    var selectedNoteTypeIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedNoteType = noteTypes[selectedNoteTypeIndex]
    val notes = result.notesByType[selectedNoteType.id] ?: emptyList()

    // Field mappings: one per field in the selected note type
    var mappings by rememberSaveable(selectedNoteTypeIndex) {
        mutableStateOf(autoDetectMappings(selectedNoteType))
    }

    val frontIndex = mappings.indexOfFirst { it == FieldMapping.FRONT }
    val backIndex = mappings.indexOfFirst { it == FieldMapping.BACK }
    val isValid = frontIndex >= 0 && backIndex >= 0 && frontIndex != backIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .widthIn(max = 500.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Text(
            text = stringResource(R.string.import_anki_configure_title),
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = RobotoSerifFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.import_anki_configure_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Note type selector (if multiple)
        if (noteTypes.size > 1) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.import_anki_note_type),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            noteTypes.forEachIndexed { index, noteType ->
                val noteCount = result.notesByType[noteType.id]?.size ?: 0
                Surface(
                    onClick = { selectedNoteTypeIndex = index },
                    shape = RoundedCornerShape(12.dp),
                    color = if (index == selectedNoteTypeIndex)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == selectedNoteTypeIndex,
                            onClick = { selectedNoteTypeIndex = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = noteType.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$noteCount ${if (noteCount == 1) "note" else "notes"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Field mapping
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.import_anki_configure_title).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        selectedNoteType.fieldNames.forEachIndexed { fieldIndex, fieldName ->
            FieldMappingCard(
                fieldName = fieldName,
                mapping = mappings[fieldIndex],
                onMappingChange = { newMapping ->
                    mappings = mappings.toMutableList().apply {
                        // If selecting FRONT or BACK, clear any other field with the same mapping
                        if (newMapping == FieldMapping.FRONT || newMapping == FieldMapping.BACK) {
                            for (i in indices) {
                                if (i != fieldIndex && this[i] == newMapping) {
                                    this[i] = FieldMapping.IGNORE
                                }
                            }
                        }
                        this[fieldIndex] = newMapping
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Data preview
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.import_anki_preview_title).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.import_anki_preview_count, minOf(3, notes.size), notes.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        notes.take(3).forEach { note ->
            PreviewCard(
                note = note,
                fieldNames = selectedNoteType.fieldNames,
                mappings = mappings
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Smart detection banner
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.import_anki_smart_detection),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.import_anki_smart_detection_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Validation error
        if (!isValid) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.import_anki_error_no_mapping),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        // Bottom actions
        Spacer(modifier = Modifier.height(24.dp))
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = navBarPadding + 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.import_anki_back))
            }
            Button(
                onClick = {
                    if (isValid) {
                        val count = onImport(notes, frontIndex, backIndex)
                        onSuccess(count)
                    }
                },
                enabled = isValid,
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.import_anki_complete),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FieldMappingCard(
    fieldName: String,
    mapping: FieldMapping,
    onMappingChange: (FieldMapping) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = fieldName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.0.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MappingOption(
                    label = stringResource(R.string.import_anki_field_front),
                    selected = mapping == FieldMapping.FRONT,
                    onClick = { onMappingChange(FieldMapping.FRONT) },
                    modifier = Modifier.weight(1f)
                )
                MappingOption(
                    label = stringResource(R.string.import_anki_field_back),
                    selected = mapping == FieldMapping.BACK,
                    onClick = { onMappingChange(FieldMapping.BACK) },
                    modifier = Modifier.weight(1f)
                )
                MappingOption(
                    label = stringResource(R.string.import_anki_field_ignore),
                    selected = mapping == FieldMapping.IGNORE,
                    onClick = { onMappingChange(FieldMapping.IGNORE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MappingOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        modifier = modifier
            .then(
                if (selected) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(8.dp)
                ) else Modifier
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .then(Modifier),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PreviewCard(
    note: AnkiNote,
    fieldNames: List<String>,
    mappings: List<FieldMapping>
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            note.fields.forEachIndexed { index, value ->
                if (index >= mappings.size) return@forEachIndexed
                val mapping = mappings[index]
                val fieldName = fieldNames.getOrElse(index) { "Field $index" }

                val (bgColor, borderColor, label) = when (mapping) {
                    FieldMapping.FRONT -> Triple(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.primary,
                        stringResource(R.string.import_anki_front_mapped)
                    )
                    FieldMapping.BACK -> Triple(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.secondary,
                        stringResource(R.string.import_anki_back_mapped)
                    )
                    FieldMapping.IGNORE -> Triple(
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        stringResource(R.string.import_anki_ignored)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .then(
                            if (mapping != FieldMapping.IGNORE) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = borderColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else Modifier
                        )
                        .alpha(if (mapping == FieldMapping.IGNORE) 0.6f else 1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fieldName,
                                style = MaterialTheme.typography.labelSmall,
                                color = borderColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = value.take(80) + if (value.length > 80) "..." else "",
                                style = if (mapping == FieldMapping.FRONT)
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = RobotoSerifFamily,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                else MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontStyle = FontStyle.Italic,
                            color = borderColor
                        )
                    }
                }
            }
        }
    }
}

private fun autoDetectMappings(noteType: AnkiNoteType): List<FieldMapping> {
    val frontPatterns = listOf("front", "word", "expression", "question", "term", "source", "vocab")
    val backPatterns = listOf("back", "meaning", "translation", "answer", "definition", "target", "gloss")

    val mappings = MutableList(noteType.fieldNames.size) { FieldMapping.IGNORE }
    var frontFound = false
    var backFound = false

    // Try pattern matching first
    for ((index, name) in noteType.fieldNames.withIndex()) {
        val lower = name.lowercase()
        if (!frontFound && frontPatterns.any { lower.contains(it) }) {
            mappings[index] = FieldMapping.FRONT
            frontFound = true
        } else if (!backFound && backPatterns.any { lower.contains(it) }) {
            mappings[index] = FieldMapping.BACK
            backFound = true
        }
    }

    // If only 2 fields and no match, default to first=front, second=back
    if (!frontFound && !backFound && noteType.fieldNames.size >= 2) {
        mappings[0] = FieldMapping.FRONT
        mappings[1] = FieldMapping.BACK
    } else if (!frontFound && noteType.fieldNames.isNotEmpty()) {
        // Assign first unassigned to front
        val firstUnassigned = mappings.indexOfFirst { it == FieldMapping.IGNORE }
        if (firstUnassigned >= 0) mappings[firstUnassigned] = FieldMapping.FRONT
    } else if (!backFound && noteType.fieldNames.size >= 2) {
        val firstUnassigned = mappings.indexOfFirst { it == FieldMapping.IGNORE }
        if (firstUnassigned >= 0) mappings[firstUnassigned] = FieldMapping.BACK
    }

    return mappings
}
