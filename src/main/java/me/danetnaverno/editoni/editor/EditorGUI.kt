package me.danetnaverno.editoni.editor

import lwjgui.geometry.Pos
import lwjgui.scene.Node
import lwjgui.scene.control.ScrollPane
import lwjgui.scene.layout.BorderPane
import lwjgui.scene.layout.Pane

object EditorGUI
{
    fun init(): Pane
    {
        val root = BorderPane()
        root.alignment = Pos.TOP_CENTER
        root.background = null

        root.setCenter(workArea())

        return root
    }

    private fun workArea(): Node
    {
        val workArea = ScrollPane()
        workArea.isFillToParentHeight = true
        workArea.isFillToParentWidth = true
        workArea.background = null
        return workArea
    }
}