package eu.qm.fiszki.database.ORM

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.flashcard.Flashcard
import java.io.IOException
import java.sql.SQLException

object DBConfigUtility {
    private val classes = arrayOf<Class<*>>(Category::class.java, Flashcard::class.java)

    @JvmStatic
    @Throws(SQLException::class, IOException::class)
    fun main(args: Array<String>) {
        OrmLiteConfigUtil.writeConfigFile("ormlite_config.txt", classes)
    }
}
