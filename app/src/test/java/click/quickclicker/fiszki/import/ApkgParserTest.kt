package click.quickclicker.fiszki.`import`

import org.junit.Assert.assertEquals
import org.junit.Test

class ApkgParserTest {

    @Test
    fun stripHtml_removesSimpleTags() {
        assertEquals("hello world", ApkgParser.stripHtml("<b>hello</b> <i>world</i>"))
    }

    @Test
    fun stripHtml_replacesBrWithSpace() {
        assertEquals("line one line two", ApkgParser.stripHtml("line one<br>line two"))
        assertEquals("line one line two", ApkgParser.stripHtml("line one<br/>line two"))
        assertEquals("line one line two", ApkgParser.stripHtml("line one<br />line two"))
        assertEquals("line one line two", ApkgParser.stripHtml("line one<BR>line two"))
    }

    @Test
    fun stripHtml_decodesNamedEntities() {
        assertEquals("A & B", ApkgParser.stripHtml("A &amp; B"))
        assertEquals("x < y > z", ApkgParser.stripHtml("x &lt; y &gt; z"))
        assertEquals("he said \"hi\"", ApkgParser.stripHtml("he said &quot;hi&quot;"))
    }

    @Test
    fun stripHtml_decodesNumericEntities() {
        assertEquals("A", ApkgParser.stripHtml("&#65;"))
        assertEquals("A", ApkgParser.stripHtml("&#x41;"))
    }

    @Test
    fun stripHtml_collapsesWhitespace() {
        assertEquals("a b c", ApkgParser.stripHtml("a   b\n\tc"))
    }

    @Test
    fun stripHtml_handlesComplexHtml() {
        val input = "<div style=\"color:red\">Hello <span class=\"word\">World</span></div>"
        assertEquals("Hello World", ApkgParser.stripHtml(input))
    }

    @Test
    fun stripHtml_handlesEmptyAndPlainText() {
        assertEquals("", ApkgParser.stripHtml(""))
        assertEquals("plain text", ApkgParser.stripHtml("plain text"))
    }

    @Test
    fun stripHtml_decodesNbsp() {
        assertEquals("a b", ApkgParser.stripHtml("a&nbsp;b"))
    }

    @Test
    fun stripHtml_trimsResult() {
        assertEquals("hello", ApkgParser.stripHtml("  <p>hello</p>  "))
    }

    @Test
    fun stripHtml_removesScriptBlocks() {
        assertEquals("hello world", ApkgParser.stripHtml("hello<script>alert('xss')</script> world"))
        assertEquals("hello world", ApkgParser.stripHtml("hello<script type=\"text/javascript\">var x=1;</script> world"))
    }

    @Test
    fun stripHtml_removesStyleBlocks() {
        assertEquals("hello", ApkgParser.stripHtml("<style>.cls{color:red}</style>hello"))
        assertEquals("text", ApkgParser.stripHtml("<STYLE>body{font-size:12px}</STYLE>text"))
    }

    @Test
    fun stripHtml_handlesNestedHtmlInScripts() {
        val input = "word<script>document.write('<b>injected</b>')</script> translation"
        assertEquals("word translation", ApkgParser.stripHtml(input))
    }
}
