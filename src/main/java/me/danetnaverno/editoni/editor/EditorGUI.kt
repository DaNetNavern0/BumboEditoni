package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.scene.Node
import lwjgui.scene.control.*
import lwjgui.scene.layout.BorderPane
import lwjgui.scene.layout.Pane
import lwjgui.scene.layout.StackPane
import lwjgui.scene.layout.VBox
import me.danetnaverno.editoni.editor.control.DynamicLabel
import me.danetnaverno.editoni.util.Translation
import javax.swing.JFileChooser

object EditorGUI
{
    private lateinit var selectInfoBox: VBox
    private lateinit var operationHistory: ScrollPane
    private var worldList = ComboBox<EditorTab>()

    fun init(): Pane
    {
        val root = BorderPane()
        root.alignment = Pos.TOP_CENTER
        root.background = null
        root.isMouseTransparent = false

        root.setCenter(workArea())
        root.setTop(menuBar())
        root.setLeft(leftPanel())
        root.setRight(rightPanel())

        return root
    }

    private fun workArea(): Node
    {
        val workArea = StackPane()
        workArea.isFillToParentHeight = true
        workArea.isFillToParentWidth = true
        workArea.background = null
        workArea.isMouseTransparent = false
        workArea.setOnMousePressed { InputHandler.registerMousePress(it); }
        workArea.setOnMouseReleased { InputHandler.registerMouseRelease(it); }
        workArea.setOnMouseDragged { InputHandler.registerMouseDrag(it); }
        return workArea
    }

    private fun menuBar(): Node
    {
        val bar = MenuBar()
        bar.minWidth = EditorApplication.WIDTH.toDouble()

        // "File"
        val file = Menu(Translation.translate("top_bar.file"))

        val open = MenuItem(Translation.translate("top_bar.file.open"))
        open.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                Editor.loadWorlds(fc.selectedFile.toPath()).forEach { Editor.createNewTab(it) }
                refreshWorldList()
            }
        }
        file.items.add(open)

        val saveAs = MenuItem(Translation.translate("top_bar.file.save_as"))
        saveAs.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                //WorldIO.writeWorld(Editor.currentWorld, fc.selectedFile.toPath())
                //GarbageChunkCollector.unloadExcessChunks(Editor.currentWorld)
                //Operations.get(Editor.currentWorld).savePosition = Operations.get(Editor.currentWorld).position
            }
        }
        file.items.add(saveAs)

        bar.items.add(file)
        return bar
    }


    private fun leftPanel(): Node
    {
        val leftPanelBcg = VBox()
        leftPanelBcg.padding = Insets(7.0)
        leftPanelBcg.prefWidth = EditorApplication.PANEL_WIDTH
        leftPanelBcg.isFillToParentWidth = true
        leftPanelBcg.isFillToParentHeight = true

        val leftPanel = VBox()
        leftPanel.minWidth = EditorApplication.PANEL_WIDTH - 14
        leftPanel.maxWidth = EditorApplication.PANEL_WIDTH - 14
        leftPanelBcg.children.add(leftPanel)

        selectInfoBox = VBox()
        selectInfoBox.isFillToParentWidth = true
        leftPanel.children.add(selectInfoBox)

        //refreshSelectInfoLabel()

        val statusBar = VBox()
        statusBar.alignment = Pos.TOP_LEFT
        val fpsLabel = DynamicLabel(500) { Translation.translate("status_bar.fps", EditorApplication.fps) }
        statusBar.children.add(fpsLabel)
        //val chunkLabel = DynamicLabel(500) { Translation.translate("status_bar.loaded_chunks", "?", Editor.currentWorld.loadedChunks.size) }
        val chunkLabel = DynamicLabel(500) { Translation.translate("status_bar.loaded_chunks", "?", 1) }
        statusBar.children.add(chunkLabel)

        leftPanel.children.add(statusBar)
        return leftPanelBcg
    }

    private fun rightPanel(): Node
    {
        val rightPanelBcg = VBox()
        rightPanelBcg.padding = Insets(7.0)
        rightPanelBcg.prefWidth = EditorApplication.PANEL_WIDTH
        rightPanelBcg.isFillToParentWidth = true
        rightPanelBcg.isFillToParentHeight = true
        val rightPanel = VBox()
        rightPanel.maxWidth = EditorApplication.PANEL_WIDTH - 14
        rightPanelBcg.children.add(rightPanel)

        rightPanel.children.add(Label(Translation.translate("gui.world.label", EditorApplication.fps)))

        worldList.prefWidth = EditorApplication.PANEL_WIDTH - 20
        worldList.alignment = Pos.CENTER
        worldList.setOnAction {
            if (Editor.currentTab != worldList.value)
                Editor.openTab(worldList.value)
        }
        rightPanel.children.add(worldList)

        operationHistory = ScrollPane()
        operationHistory.padding = Insets(5.0)
        operationHistory.prefHeight = 500.0
        operationHistory.isFillToParentWidth = true
        rightPanel.children.add(operationHistory)
        return rightPanelBcg
    }


    fun refreshWorldList()
    {
        worldList.items.clear()
        for (entry in Editor.tabs.entries)
            worldList.items.add(entry.value)
        worldList.value = Editor.currentTab
    }
}