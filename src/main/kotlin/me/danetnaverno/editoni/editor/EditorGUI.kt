package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.paint.Color
import lwjgui.scene.Node
import lwjgui.scene.control.*
import lwjgui.scene.layout.*
import lwjgui.style.BackgroundSolid
import lwjgui.theme.ThemeWhite
import me.danetnaverno.editoni.editor.control.DynamicLabel
import me.danetnaverno.editoni.editor.raw.RawInputHandler
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.ChunkManager
import net.querz.nbt.tag.CompoundTag
import javax.swing.JFileChooser

/**
 * This class create the User Interface of the Editor.
 */
object EditorGUI
{
    private lateinit var selectInfoBox: VBox
    private lateinit var operationHistory: ScrollPane
    private var worldList = ComboBox<EditorTab>()

    internal fun initialize(): Pane
    {
        check(!this::selectInfoBox.isInitialized) { "EditorGUI had already been initialized"}

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

    fun refreshWorldList()
    {
        worldList.items.clear()
        for (entry in Editor.editorTabs)
            worldList.items.add(entry)
        worldList.value = Editor.currentTab
    }

    private fun workArea(): Node
    {
        val workArea = StackPane()
        workArea.isFillToParentHeight = true
        workArea.isFillToParentWidth = true
        workArea.background = null
        workArea.isMouseTransparent = false
        workArea.setOnMousePressed { RawInputHandler.registerMousePress(it); }
        workArea.setOnMouseReleased { RawInputHandler.registerMouseRelease(it); }
        workArea.setOnMouseDragged { RawInputHandler.registerMouseDrag(it); }
        return workArea
    }

    private fun menuBar(): Node
    {
        val bar = MenuBar()
        bar.minWidth = EditorApplication.windowWidth.toDouble()
        addFileMenuTab(bar)
        return bar
    }

    private fun addFileMenuTab(bar: MenuBar)
    {
        val fileTab = Menu(Translation.translate("top_bar.file"))

        val fileOpen = MenuItem(Translation.translate("top_bar.file.open"))
        fileOpen.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                Editor.switchTab(Editor.loadWorldIntoTab(fc.selectedFile.toPath()))
                refreshWorldList()
            }
        }
        fileTab.items.add(fileOpen)

        val fileReload = MenuItem(Translation.translate("top_bar.file.reload"))
        fileReload.setOnAction {
            Editor.closeTabAndSwitch(Editor.currentTab, Editor.loadWorldIntoTab(Editor.currentWorld.path))
        }
        fileTab.items.add(fileReload)

        val fileSaveAs = MenuItem(Translation.translate("top_bar.file.save_as"))
        fileSaveAs.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                val editorTab = Editor.currentTab
                val world = editorTab.world
                world.worldIO.writeWorld(Editor.currentWorld, fc.selectedFile.toPath())
                ChunkManager.unloadExcessChunks(world)
                editorTab.operationList.savePosition = editorTab.operationList.getPosition()
            }
        }
        fileTab.items.add(fileSaveAs)

        bar.items.add(fileTab)
    }


    private fun leftPanel(): Node
    {
        val sidePanelWidth = EditorApplication.sidePanelWidth.toDouble()
        val leftPanelBcg = VBox()
        leftPanelBcg.background = BackgroundSolid(ThemeWhite().pane)
        leftPanelBcg.padding = Insets(7.0)
        leftPanelBcg.prefWidth = sidePanelWidth
        leftPanelBcg.isFillToParentWidth = true
        leftPanelBcg.isFillToParentHeight = true

        val leftPanel = VBox()
        leftPanel.minWidth = sidePanelWidth - 14
        leftPanel.maxWidth = sidePanelWidth - 14
        leftPanelBcg.children.add(leftPanel)

        selectInfoBox = VBox()
        selectInfoBox.isFillToParentWidth = true
        leftPanel.children.add(selectInfoBox)

        val statusBar = VBox()
        statusBar.alignment = Pos.TOP_LEFT
        val fpsLabel = DynamicLabel(500) { Translation.translate("status_bar.fps", EditorApplication.fps) }
        statusBar.children.add(fpsLabel)
        val chunkLabel = DynamicLabel(500) { Translation.translate("status_bar.loaded_chunks", Editor.currentWorld.getLoadedChunks().size) }
        statusBar.children.add(chunkLabel)

        leftPanel.children.add(statusBar)
        leftBottomPanel(leftPanelBcg)
        return leftPanelBcg
    }

    private fun leftBottomPanel(leftPanelBcg: VBox)
    {
        val sidePanelWidth = EditorApplication.sidePanelWidth.toDouble()

        val leftBottomPanel = VBox()
        leftBottomPanel.background = BackgroundSolid(Color.AQUA)
        leftBottomPanel.minWidth = sidePanelWidth - 14
        leftBottomPanel.maxWidth = sidePanelWidth - 14
        leftPanelBcg.children.add(leftBottomPanel)

        val hideObservingOperations = CheckBox(Translation.translate("top_bar.settings.hide_observing_operations"))
        hideObservingOperations.isChecked = Settings.hideObservingOperations
        hideObservingOperations.setOnAction {
            Settings.hideObservingOperations = hideObservingOperations.isChecked
        }

        leftBottomPanel.children.add(hideObservingOperations)
    }

    private fun rightPanel(): Node
    {
        val sidePanelWidth = EditorApplication.sidePanelWidth.toDouble()

        val rightPanelBcg = VBox()
        rightPanelBcg.background = BackgroundSolid(ThemeWhite().pane)
        rightPanelBcg.padding = Insets(7.0)
        rightPanelBcg.prefWidth = sidePanelWidth
        rightPanelBcg.isFillToParentWidth = true
        rightPanelBcg.isFillToParentHeight = true
        val rightPanel = VBox()
        rightPanel.maxWidth = sidePanelWidth - 14
        rightPanelBcg.children.add(rightPanel)

        rightPanel.children.add(Label(Translation.translate("gui.world.label", EditorApplication.fps)))

        worldList.prefWidth = sidePanelWidth - 20
        worldList.alignment = Pos.CENTER
        worldList.setOnAction {
            if (Editor.currentTab != worldList.value)
                Editor.switchTab(worldList.value)
        }
        rightPanel.children.add(worldList)

        operationHistory = ScrollPane()
        operationHistory.padding = Insets(5.0)
        operationHistory.prefHeight = 500.0
        operationHistory.isFillToParentWidth = true
        rightPanel.children.add(operationHistory)
        return rightPanelBcg
    }

    private fun refreshBlockInfoLabel()
    {
        val sidePanelWidth = EditorApplication.sidePanelWidth.toDouble()

        val selectedBlock = Editor.currentTab.selectedArea?.world?.getBlockAt(Editor.currentTab.selectedArea!!.min)

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", selectedBlock?.type ?: "-"))
        blockInfoLabel.isFillToParentWidth = true
        blockInfoLabel.alignment = Pos.TOP_LEFT

        val blockGlobalLocLabel = if (selectedBlock == null)
            Label(Translation.translate("gui.block_info.location.global", "-", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.global", selectedBlock.blockLocation.globalX, selectedBlock.blockLocation.globalY, selectedBlock.blockLocation.globalZ))

        val blockChunkLocLabel = if (selectedBlock == null)
            Label(Translation.translate("gui.block_info.location.local", "-", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.local", selectedBlock.blockLocation.localX, selectedBlock.blockLocation.localY, selectedBlock.blockLocation.localZ))

        val blockChunkPosLabel = if (selectedBlock == null)
            Label(Translation.translate("gui.block_info.location.chunk", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.chunk", selectedBlock.chunk.chunkLocation.x, selectedBlock.chunk.chunkLocation.z))

        blockGlobalLocLabel.isFillToParentWidth = true
        blockGlobalLocLabel.alignment = Pos.TOP_LEFT
        blockChunkLocLabel.isFillToParentWidth = true
        blockChunkLocLabel.alignment = Pos.TOP_LEFT
        blockChunkPosLabel.isFillToParentWidth = true
        blockChunkPosLabel.alignment = Pos.TOP_LEFT

        val blockStatePane = buildPane(selectedBlock?.state?.tag, sidePanelWidth - 50)
        val tileEntityPane = buildPane(selectedBlock?.tileEntity?.tag, sidePanelWidth - 50)

        selectInfoBox.children.add(blockInfoLabel)
        selectInfoBox.children.add(blockGlobalLocLabel)
        selectInfoBox.children.add(blockChunkLocLabel)
        selectInfoBox.children.add(blockChunkPosLabel)
        selectInfoBox.children.add(blockStatePane)
        selectInfoBox.children.add(tileEntityPane)
    }

    private fun refreshAreaInfoLabel()
    {
        val selectedArea = Editor.currentTab.selectedArea!!

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", Translation.translate("gui.block_info.type.area")))
        blockInfoLabel.isFillToParentWidth = true
        blockInfoLabel.alignment = Pos.TOP_LEFT

        val minLabel = Label(Translation.translate("gui.block_info.location.global", selectedArea.min.globalX, selectedArea.min.globalY, selectedArea.min.globalZ))
        val maxLabel = Label(Translation.translate("gui.block_info.location.global", selectedArea.max.globalX, selectedArea.max.globalY, selectedArea.max.globalZ))

        minLabel.isFillToParentWidth = true
        minLabel.alignment = Pos.TOP_LEFT
        maxLabel.isFillToParentWidth = true
        maxLabel.alignment = Pos.TOP_LEFT

        val tagPane = buildPane(CompoundTag(), EditorApplication.sidePanelWidth.toDouble())

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

        val locationLabel = Label(Translation.translate("gui.block_info.location.global", selectedEntity.entityLocation.globalX, selectedEntity.entityLocation.globalY, selectedEntity.entityLocation.globalZ))
        locationLabel.isFillToParentWidth = true
        locationLabel.alignment = Pos.TOP_LEFT

        val tagPane = buildPane(selectedEntity.tag, EditorApplication.sidePanelWidth.toDouble())

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
}