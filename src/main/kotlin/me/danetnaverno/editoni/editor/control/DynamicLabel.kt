package me.danetnaverno.editoni.editor.control

import lwjgui.scene.Context
import lwjgui.scene.control.Label
import me.danetnaverno.editoni.editor.Editor.logger

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
                text = callable()
                nextUpdate = System.currentTimeMillis() + frequency
            }
        }
        catch (e: Exception)
        {
            logger.error("Failed to process dynamic text", e)
        }
        super.render(context)
    }
}