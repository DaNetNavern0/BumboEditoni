package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.scene.Node
import lwjgui.scene.control.*
import lwjgui.scene.layout.*
import lwjgui.style.BackgroundSolid
import lwjgui.theme.ThemeWhite
import me.danetnaverno.editoni.editor.control.DynamicLabel
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.ChunkManager
import net.querz.nbt.tag.CompoundTag
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

        val fileOpen = MenuItem(Translation.translate("top_bar.file.open"))
        fileOpen.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                val world = Editor.loadWorld(fc.selectedFile.toPath())
                val tab = Editor.createNewTab(world)
                Editor.openTab(tab)
                refreshWorldList()
            }
        }
        file.items.add(fileOpen)

        val fileReload = MenuItem(Translation.translate("top_bar.file.reload"))
        fileReload.setOnAction {
            val world = Editor.currentTab.world
            Editor.unloadWorld(world)
            val newWorld = Editor.loadWorld(world.path)
            val tab = Editor.createNewTab(newWorld)
            Editor.openTab(tab)
        }
        file.items.add(fileReload)

        val fileSaveAs = MenuItem(Translation.translate("top_bar.file.save_as"))
        fileSaveAs.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                Editor.currentTab.world.worldIOProvider.writeWorld(Editor.currentTab.world, fc.selectedFile.toPath())
                ChunkManager.unloadExcessChunks(Editor.currentTab.world)
                Editor.currentTab.operationList.savePosition = Editor.currentTab.operationList.getPosition()
            }
        }
        file.items.add(fileSaveAs)

        bar.items.add(file)
        return bar
    }


    private fun leftPanel(): Node
    {
        val leftPanelBcg = VBox()
        leftPanelBcg.background = BackgroundSolid(ThemeWhite().pane)
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

        if (Editor.tabs.isNotEmpty()) //todo hacky check to prevent "lateinit property currentTab has not been initialized" exception. Gotta fix this
            refreshSelectInfoLabel()

        val statusBar = VBox()
        statusBar.alignment = Pos.TOP_LEFT
        val fpsLabel = DynamicLabel(500) { Translation.translate("status_bar.fps", EditorApplication.fps) }
        statusBar.children.add(fpsLabel)
        val chunkLabel = DynamicLabel(500) { Translation.translate("status_bar.loaded_chunks", "?", Editor.currentTab.world.getLoadedChunks().size) }
        statusBar.children.add(chunkLabel)

        leftPanel.children.add(statusBar)
        return leftPanelBcg
    }

    private fun rightPanel(): Node
    {
        val rightPanelBcg = VBox()
        rightPanelBcg.background = BackgroundSolid(ThemeWhite().pane)
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

    //Buttons don't update for some reason, had to re-create the entire UI section
    fun refreshSelectInfoLabel()
    {
        selectInfoBox.children.clear()

        if (Editor.currentTab.selectedEntity != null)
        {
            refreshEntityInfoLabel()
            return
        }
        val selectedArea = Editor.currentTab.selectedArea
        if (selectedArea != null)
        {
            if (selectedArea.min != selectedArea.max)
            {
                refreshAreaInfoLabel()
                return
            }
        }
        refreshBlockInfoLabel()
    }

    private fun refreshBlockInfoLabel()
    {
        val selectedBlock = Editor.currentTab.selectedArea?.world?.getLoadedBlockAt(Editor.currentTab.selectedArea!!.min)

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", selectedBlock?.type ?: "-"))
        blockInfoLabel.isFillToParentWidth = true
        blockInfoLabel.alignment = Pos.TOP_LEFT

        val blockGlobalLocLabel = if (selectedBlock == null)
            Label(Translation.translate("gui.block_info.location.global", "-", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.global", selectedBlock.location.globalX, selectedBlock.location.globalY, selectedBlock.location.globalZ))

        val blockChunkLocLabel = if (selectedBlock == null)
            Label(Translation.translate("gui.block_info.location.local", "-", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.local", selectedBlock.location.localX, selectedBlock.location.localY, selectedBlock.location.localZ))

        blockGlobalLocLabel.isFillToParentWidth = true
        blockGlobalLocLabel.alignment = Pos.TOP_LEFT
        blockChunkLocLabel.isFillToParentWidth = true
        blockChunkLocLabel.alignment = Pos.TOP_LEFT

        val blockStatePane = buildPane(selectedBlock?.state?.tag, EditorApplication.PANEL_WIDTH - 50)
        val tileEntityPane = buildPane(selectedBlock?.tileEntity?.tag, EditorApplication.PANEL_WIDTH - 50)

        selectInfoBox.children.add(blockInfoLabel)
        selectInfoBox.children.add(blockGlobalLocLabel)
        selectInfoBox.children.add(blockChunkLocLabel)
        selectInfoBox.children.add(blockStatePane)
        selectInfoBox.children.add(tileEntityPane)
    }

    private fun refreshAreaInfoLabel()
    {
        val selectedArea = Editor.currentTab.selectedArea!!

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", "Area"))
        blockInfoLabel.isFillToParentWidth = true
        blockInfoLabel.alignment = Pos.TOP_LEFT

        val minLabel = Label(Translation.translate("gui.block_info.location.local", selectedArea.min.globalX, selectedArea.min.globalY, selectedArea.min.globalZ))
        val maxLabel = Label(Translation.translate("gui.block_info.location.local", selectedArea.max.globalX, selectedArea.max.globalY, selectedArea.max.globalZ))

        minLabel.isFillToParentWidth = true
        minLabel.alignment = Pos.TOP_LEFT
        maxLabel.isFillToParentWidth = true
        maxLabel.alignment = Pos.TOP_LEFT

        val tagPane = buildPane(CompoundTag(), EditorApplication.PANEL_WIDTH)

        selectInfoBox.children.add(blockInfoLabel)
        selectInfoBox.children.add(minLabel)
        selectInfoBox.children.add(maxLabel)
        selectInfoBox.children.add(tagPane)
    }

    private fun refreshEntityInfoLabel()
    {
        val selectedEntity = Editor.currentTab.selectedEntity!!

        selectInfoBox.children.clear()

        val infoLabel = Label(Translation.translate("gui.block_info.type", selectedEntity.type))
        infoLabel.isFillToParentWidth = true
        infoLabel.alignment = Pos.TOP_LEFT

        val locationLabel = Label(Translation.translate("gui.block_info.location.global", selectedEntity.location.globalX, selectedEntity.location.globalY, selectedEntity.location.globalZ))
        locationLabel.isFillToParentWidth = true
        locationLabel.alignment = Pos.TOP_LEFT

        val tagPane = buildPane(selectedEntity.tag, EditorApplication.PANEL_WIDTH)

        selectInfoBox.children.add(infoLabel)
        selectInfoBox.children.add(locationLabel)
        selectInfoBox.children.add(tagPane)
    }

    fun refreshOperationHistory()
    {
        val operations = Editor.currentTab.operationList
        val ohContainer = TreeView<String>()
        for ((i, operation) in operations.all.withIndex())
        {
            val item = TreeItem(operation.displayName)
            item.setOnMouseClicked { operations.setPosition(i) }
            ohContainer.items.add(item)
            if (i == operations.getPosition())
                ohContainer.selectItem(item)
        }
        operationHistory.content = ohContainer
    }

    private fun buildPane(compoundTag: CompoundTag?, height: Double): ScrollPane
    {
        val mainPane = ScrollPane()
        mainPane.isFillToParentWidth = true
        mainPane.prefHeight = height
        val hBox = HBox()
        if (compoundTag == null)
            return mainPane

        mainPane.content = hBox

        val leftVBox = VBox()
        leftVBox.padding = Insets(5.0)
        hBox.children.add(leftVBox)
        val rightVBox = VBox()
        rightVBox.padding = Insets(5.0)
        hBox.children.add(rightVBox)

        for (entry in compoundTag.entrySet())
        {
            val leftHbox = HBox()
            val rightHbox = HBox()

            val keyBox = Label(entry.key)
            leftHbox.children.add(keyBox)
            val valueBox = Label(entry.value.toString())
            rightHbox.children.add(valueBox)

            leftVBox.children.add(leftHbox)
            rightVBox.children.add(rightHbox)
        }

        return mainPane
    }

    fun refreshWorldList()
    {
        worldList.items.clear()
        for (entry in Editor.tabs.entries)
            worldList.items.add(entry.value)
        worldList.value = Editor.currentTab
    }
}