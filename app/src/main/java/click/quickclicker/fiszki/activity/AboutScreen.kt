package click.quickclicker.fiszki.activity

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import click.quickclicker.fiszki.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lang = LocalConfiguration.current.locales[0].language
    var htmlContent by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val background = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val linkColor = MaterialTheme.colorScheme.primary
    val codeBackground = MaterialTheme.colorScheme.surfaceContainerHighest
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    LaunchedEffect(Unit) {
        try {
            val fileName = if (lang == "pl") "about_pl.md" else "about_en.md"
            val markdown = withContext(Dispatchers.IO) {
                context.assets.open(fileName).bufferedReader().use { it.readText() }
            }
            val flavour = GFMFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
            val rawHtml = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
            htmlContent = rawHtml
        } catch (_: Exception) {
            isError = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_toolbar_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isError -> {
                    Text(
                        text = stringResource(R.string.about_load_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp)
                    )
                }
                htmlContent != null -> {
                    MarkdownWebView(
                        html = htmlContent!!,
                        background = background,
                        textColor = textColor,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        borderColor = borderColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun MarkdownWebView(
    html: String,
    background: Color,
    textColor: Color,
    linkColor: Color,
    codeBackground: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val bgHex = background.toCssHex()
    val textHex = textColor.toCssHex()
    val linkHex = linkColor.toCssHex()
    val codeBgHex = codeBackground.toCssHex()
    val borderHex = borderColor.toCssHex()

    val styledHtml = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
          body {
            background-color: $bgHex;
            color: $textHex;
            font-family: sans-serif;
            font-size: 15px;
            line-height: 1.6;
            padding: 16px;
            margin: 0;
            word-wrap: break-word;
          }
          a { color: $linkHex; }
          h1, h2, h3, h4 { margin-top: 1.2em; margin-bottom: 0.4em; }
          hr { border: none; border-top: 1px solid $borderHex; margin: 1.2em 0; }
          code, pre {
            background-color: $codeBgHex;
            border-radius: 4px;
            font-family: monospace;
            font-size: 13px;
          }
          code { padding: 2px 5px; }
          pre { padding: 12px; overflow-x: auto; }
          table {
            border-collapse: collapse;
            width: 100%;
            margin: 1em 0;
            font-size: 14px;
          }
          th, td {
            border: 1px solid $borderHex;
            padding: 8px 12px;
            text-align: left;
          }
          th { background-color: $codeBgHex; font-weight: bold; }
          blockquote {
            border-left: 4px solid $borderHex;
            margin: 0;
            padding-left: 16px;
            color: $textHex;
            opacity: 0.75;
          }
        </style>
        </head>
        <body>
        $html
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setBackgroundColor(background.toArgb())
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

private fun Color.toCssHex(): String {
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return "#%02x%02x%02x".format(r, g, b)
}
