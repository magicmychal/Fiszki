package click.quickclicker.fiszki.`import`

import android.content.Context
import android.net.Uri
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

data class AnkiNoteType(
    val id: Long,
    val name: String,
    val fieldNames: List<String>
)

data class AnkiNote(
    val noteTypeId: Long,
    val fields: List<String>
)

data class ApkgParseResult(
    val noteTypes: List<AnkiNoteType>,
    val notesByType: Map<Long, List<AnkiNote>>,
    val deckName: String
)

class ApkgParser(private val context: Context) {

    fun parse(uri: Uri): ApkgParseResult {
        val tempDir = File(context.cacheDir, "apkg_temp_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        try {
            val dbFile = extractDatabase(uri, tempDir)
            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                dbFile.absolutePath, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
            )
            try {
                val noteTypes = parseNoteTypes(db)
                val notesByType = parseNotes(db, noteTypes)
                val deckName = parseDeckName(db)
                return ApkgParseResult(noteTypes, notesByType, deckName)
            } finally {
                db.close()
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun extractDatabase(uri: Uri, tempDir: File): File {
        val tempZip = File(tempDir, "deck.apkg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempZip).use { output ->
                input.copyTo(output)
            }
        } ?: throw ApkgParseException("Could not open file")

        val dbFile = File(tempDir, "collection.anki2")
        ZipInputStream(tempZip.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name == "collection.anki2" || entry.name == "collection.anki21") {
                    FileOutputStream(dbFile).use { output ->
                        zip.copyTo(output)
                    }
                    return dbFile
                }
                entry = zip.nextEntry
            }
        }
        throw ApkgParseException("No collection database found in APKG file")
    }

    private fun parseNoteTypes(db: android.database.sqlite.SQLiteDatabase): List<AnkiNoteType> {
        // Try newer format first (notetypes table)
        if (tableExists(db, "notetypes")) {
            return parseNoteTypesFromTable(db)
        }
        // Fall back to col.models JSON
        return parseNoteTypesFromCol(db)
    }

    private fun tableExists(db: android.database.sqlite.SQLiteDatabase, table: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(table)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun parseNoteTypesFromTable(db: android.database.sqlite.SQLiteDatabase): List<AnkiNoteType> {
        val noteTypes = mutableListOf<AnkiNoteType>()
        val cursor = db.rawQuery("SELECT id, name, config FROM notetypes", null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val name = cursor.getString(1)
            val fieldNames = parseFieldsFromNotetypeTable(db, id)
            if (fieldNames.isNotEmpty()) {
                noteTypes.add(AnkiNoteType(id, name, fieldNames))
            }
        }
        cursor.close()

        if (noteTypes.isEmpty()) throw ApkgParseException("No note types found")
        return noteTypes
    }

    private fun parseFieldsFromNotetypeTable(
        db: android.database.sqlite.SQLiteDatabase,
        notetypeId: Long
    ): List<String> {
        val fields = mutableListOf<Pair<Int, String>>()
        if (tableExists(db, "fields")) {
            val cursor = db.rawQuery(
                "SELECT name, ord FROM fields WHERE ntid=?",
                arrayOf(notetypeId.toString())
            )
            while (cursor.moveToNext()) {
                fields.add(cursor.getInt(1) to cursor.getString(0))
            }
            cursor.close()
        }
        return fields.sortedBy { it.first }.map { it.second }
    }

    private fun parseNoteTypesFromCol(db: android.database.sqlite.SQLiteDatabase): List<AnkiNoteType> {
        val cursor = db.rawQuery("SELECT models FROM col", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            throw ApkgParseException("No collection data found")
        }
        val modelsJson = cursor.getString(0)
        cursor.close()

        val models = JSONObject(modelsJson)
        val noteTypes = mutableListOf<AnkiNoteType>()

        val keys = models.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val model = models.getJSONObject(key)
            val id = model.getLong("id")
            val name = model.getString("name")
            val fldsArray = model.getJSONArray("flds")
            val fieldNames = mutableListOf<String>()
            for (i in 0 until fldsArray.length()) {
                fieldNames.add(fldsArray.getJSONObject(i).getString("name"))
            }
            if (fieldNames.isNotEmpty()) {
                noteTypes.add(AnkiNoteType(id, name, fieldNames))
            }
        }

        if (noteTypes.isEmpty()) throw ApkgParseException("No note types found")
        return noteTypes
    }

    private fun parseNotes(
        db: android.database.sqlite.SQLiteDatabase,
        noteTypes: List<AnkiNoteType>
    ): Map<Long, List<AnkiNote>> {
        val noteTypeMap = noteTypes.associateBy { it.id }
        val result = mutableMapOf<Long, MutableList<AnkiNote>>()

        val cursor = db.rawQuery("SELECT mid, flds FROM notes", null)
        while (cursor.moveToNext()) {
            val mid = cursor.getLong(0)
            val flds = cursor.getString(1)
            val noteType = noteTypeMap[mid] ?: continue

            val fieldValues = flds.split("\u001f")
            val stripped = fieldValues.map { stripHtml(it) }

            // Pad or trim to match expected field count
            val adjusted = if (stripped.size >= noteType.fieldNames.size) {
                stripped.take(noteType.fieldNames.size)
            } else {
                stripped + List(noteType.fieldNames.size - stripped.size) { "" }
            }

            result.getOrPut(mid) { mutableListOf() }
                .add(AnkiNote(mid, adjusted))
        }
        cursor.close()

        return result
    }

    private fun parseDeckName(db: android.database.sqlite.SQLiteDatabase): String {
        // Try newer format
        if (tableExists(db, "decks")) {
            val cursor = db.rawQuery("SELECT name FROM decks LIMIT 1", null)
            if (cursor.moveToFirst()) {
                val name = cursor.getString(0)
                cursor.close()
                return name
            }
            cursor.close()
        }
        // Fall back to col.decks JSON
        try {
            val cursor = db.rawQuery("SELECT decks FROM col", null)
            if (cursor.moveToFirst()) {
                val decksJson = cursor.getString(0)
                cursor.close()
                val decks = JSONObject(decksJson)
                val keys = decks.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val deck = decks.getJSONObject(key)
                    val name = deck.getString("name")
                    if (name != "Default") return name
                }
                // If only Default exists, return it
                return "Default"
            }
            cursor.close()
        } catch (_: Exception) {
        }
        return "Anki Deck"
    }

    companion object {
        private val SCRIPT_STYLE_REGEX = Regex("<(script|style)[^>]*>[\\s\\S]*?</\\1>", RegexOption.IGNORE_CASE)
        private val HTML_TAG_REGEX = Regex("<[^>]*>")
        private val ENTITY_MAP = mapOf(
            "&amp;" to "&",
            "&lt;" to "<",
            "&gt;" to ">",
            "&nbsp;" to " ",
            "&quot;" to "\"",
            "&apos;" to "'",
        )
        private val NUMERIC_ENTITY_REGEX = Regex("&#(\\d+);")
        private val HEX_ENTITY_REGEX = Regex("&#x([0-9a-fA-F]+);")
        private val MULTI_SPACE_REGEX = Regex("\\s+")

        fun stripHtml(input: String): String {
            var text = input
            // Remove <script>...</script> and <style>...</style> blocks entirely
            text = SCRIPT_STYLE_REGEX.replace(text, "")
            // Replace <br>, <br/>, <br /> with space
            text = text.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), " ")
            // Remove all other HTML tags
            text = HTML_TAG_REGEX.replace(text, "")
            // Decode named entities
            for ((entity, replacement) in ENTITY_MAP) {
                text = text.replace(entity, replacement)
            }
            // Decode numeric entities
            text = NUMERIC_ENTITY_REGEX.replace(text) {
                val code = it.groupValues[1].toIntOrNull()
                if (code != null) String(Character.toChars(code)) else it.value
            }
            text = HEX_ENTITY_REGEX.replace(text) {
                val code = it.groupValues[1].toIntOrNull(16)
                if (code != null) String(Character.toChars(code)) else it.value
            }
            // Collapse whitespace
            text = MULTI_SPACE_REGEX.replace(text, " ")
            return text.trim()
        }
    }
}

class ApkgParseException(message: String) : Exception(message)
