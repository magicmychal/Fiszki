package click.quickclicker.fiszki.activity

import android.graphics.Color

data class CategoryColor(val name: String, val primary: Int, val container: Int)

val CATEGORY_COLORS: List<CategoryColor> = listOf(
    CategoryColor("Lavender", Color.parseColor("#6750A4"), Color.parseColor("#EADDFF")),
    CategoryColor("Rose", Color.parseColor("#904A77"), Color.parseColor("#FFD8E8")),
    CategoryColor("Sage", Color.parseColor("#4A6741"), Color.parseColor("#C8E6B8")),
    CategoryColor("Ocean", Color.parseColor("#006590"), Color.parseColor("#C3E7FF")),
    CategoryColor("Sand", Color.parseColor("#7C5800"), Color.parseColor("#FFDEA1")),
    CategoryColor("Coral", Color.parseColor("#904B40"), Color.parseColor("#FFDAD5")),
    CategoryColor("Sky", Color.parseColor("#4355B9"), Color.parseColor("#DEE0FF")),
    CategoryColor("Mauve", Color.parseColor("#6B5778"), Color.parseColor("#F3DAFF"))
)

fun findCategoryColor(hex: String?): CategoryColor? {
    if (hex == null) return null
    val normalized = hex.uppercase().let { if (it.startsWith("#")) it else "#$it" }
    return CATEGORY_COLORS.find {
        String.format("#%06X", 0xFFFFFF and it.primary).equals(normalized, ignoreCase = true)
    }
}

fun defaultCategoryColor(): CategoryColor = CATEGORY_COLORS[0]
