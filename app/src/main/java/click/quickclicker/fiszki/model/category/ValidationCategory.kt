package click.quickclicker.fiszki.model.category

import android.content.Context
import android.widget.Toast
import click.quickclicker.fiszki.R

class ValidationCategory(private val context: Context) {

    fun validate(category: Category): Boolean {
        if (category.categoryDB.isEmpty()) {
            Toast.makeText(context, R.string.validation_category_empty, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
