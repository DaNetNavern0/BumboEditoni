package me.danetnaverno.editoni.editor.control

import lwjgui.scene.Context
import lwjgui.scene.control.Label
import lwjgui.scene.control.Labeled
import me.danetnaverno.editoni.editor.Editor.logger
import java.lang.reflect.Field

/**
 * A modification of [Label] that refreshes its own text once in a while with a supplied callable
 */
class DynamicLabel(private val frequency: Int, private val callable: () -> String) : Label()
{
    private var nextUpdate = System.currentTimeMillis()
    override fun render(context: Context)
    {
        try
        {
            if (System.currentTimeMillis() > nextUpdate)
            {
                textField[this] = callable()
                nextUpdate = System.currentTimeMillis() + frequency
            }
        }
        catch (e: Exception)
        {
            logger.error("Failed to process dynamic text", e)
        }
        super.render(context)
    }

    companion object
    {
        private lateinit var textField: Field

        init
        {
            try
            {
                textField = Labeled::class.java.getDeclaredField("text")
                textField.isAccessible = true
            }
            catch (e: NoSuchFieldException)
            {
                logger.error("Failed to access field 'text'", e)
            }
        }
    }
}