package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.io.IMinecraftWorldIO
import me.danetnaverno.editoni.world.World
import org.apache.logging.log4j.LogManager
import java.nio.file.Path

/**
 * This class represents editor-related actions (opening tabs, raycasting etc)
 * For the editor's core at [EditorApplication]
 */
object Editor
{
    val logger = LogManager.getLogger("Editor")!!

    val editorTabs : List<EditorTab> = arrayListOf()
    lateinit var currentTab: EditorTab
        private set
    val currentWorld: World
        get() = currentTab.world

    fun loadWorldIntoTab(worldPath: Path): EditorTab
    {
        val world = IMinecraftWorldIO.getWorldIO(worldPath).openWorld(worldPath)
        val editorTab = EditorTab(world)
        (editorTabs as MutableList<EditorTab>).add(editorTab)
        return editorTab
    }

    fun closeTabAndSwitch(tabToClose: EditorTab, tabToOpen: EditorTab)
    {
        (editorTabs as MutableList<EditorTab>).remove(tabToClose)
        switchTab(tabToOpen)
    }

    fun switchTab(tab: EditorTab)
    {
        currentTab = tab
    }

    fun getTab(world: World) : EditorTab
    {
        return editorTabs.first { it.world == world }
    }
}