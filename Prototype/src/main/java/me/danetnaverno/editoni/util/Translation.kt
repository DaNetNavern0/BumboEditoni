package me.danetnaverno.editoni.util

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.Prototype
import java.io.IOException
import java.nio.file.Paths
import java.text.MessageFormat
import java.util.*

object Translation
{
    val EN = BumboLocaloni("english", Locale.ROOT)
    val RU = BumboLocaloni("russian", Locale("ru", "RU"))
    var language = EN

    fun translate(locale: String, vararg format: Any?): String
    {
        return language.translate(locale, *format)
    }

    class BumboLocaloni(name: String, private val locale: Locale)
    {
        private val data: JSONObject

        init
        {
            data = try
            {
                JsonUtil.fromFile(Paths.get("data/lang/$name.lang"))
            }
            catch (e: IOException)
            {
                Prototype.logger.error("Failed to load locale '$name' for '$locale'")
                EN.data
            }
        }

        fun translate(locale: String, vararg format: Any?): String
        {
            return MessageFormat.format(data.getString(locale) ?: locale, *format)
        }
    }
}
