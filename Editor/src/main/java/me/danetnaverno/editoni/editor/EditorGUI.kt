package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.scene.Window
import lwjgui.scene.control.*
import lwjgui.scene.layout.BorderPane
import lwjgui.scene.layout.HBox
import lwjgui.scene.layout.Pane
import lwjgui.scene.layout.VBox
import me.danetnaverno.editoni.editor.control.DynamicLabel
import me.danetnaverno.editoni.editor.operations.Operations
import me.danetnaverno.editoni.util.Translation
import net.querz.nbt.CompoundTag
import javax.swing.JFileChooser

object EditorGUI
{
    private lateinit var root : BorderPane
    private lateinit var selectInfoBox : VBox
    private lateinit var operationHistory : ScrollPane
    private lateinit var worldList : ComboBox<String>

    fun init(window: Window): Pane
    {
        root = BorderPane()
        root.alignment = Pos.TOP_CENTER
        root.background = null

        val workArea = Pane()
        root.setCenter(workArea)
        workArea.isFillToParentHeight = true
        workArea.isFillToParentWidth = true
        workArea.background = null
        workArea.setOnMouseClicked { Editor.onMouseClick(it.mouseX.toInt(), it.mouseY.toInt()); }

        val bar = MenuBar()
        bar.minWidth = EditorApplication.WIDTH.toDouble()
        root.setTop(bar)

        val file = Menu(Translation.translate("top_bar.file"))
        val open = MenuItem(Translation.translate("top_bar.file.open"))
        open.setOnAction {
            val fc = JFileChooser()
            fc.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            val state = fc.showOpenDialog(null)
            if (state == JFileChooser.APPROVE_OPTION)
            {
                Editor.currentWorld = Editor.loadWorld(fc.selectedFile.toPath())
                EditorGUI.refreshWorldList()
            }
        }
        file.items.add(open)
        file.items.add(MenuItem(Translation.translate("top_bar.file.save")))
        bar.items.add(file)

        val edit = Menu("Edit")
        edit.items.add(MenuItem("Undo"))
        edit.items.add(MenuItem("Redo"))
        bar.items.add(edit)

        val leftPanelBcg = VBox()
        leftPanelBcg.padding = Insets(7.0)
        leftPanelBcg.prefWidth = EditorApplication.PANEL_WIDTH
        leftPanelBcg.isFillToParentWidth = true
        leftPanelBcg.isFillToParentHeight = true
        root.setLeft(leftPanelBcg)
        val leftPanel = VBox()
        leftPanel.minWidth = EditorApplication.PANEL_WIDTH - 14
        leftPanel.maxWidth = EditorApplication.PANEL_WIDTH - 14
        leftPanelBcg.children.add(leftPanel)

        selectInfoBox = VBox()
        selectInfoBox.isFillToParentWidth = true
        selectInfoBox.setOnMouseClicked { Editor.onMouseClick(it.mouseX.toInt(),it.mouseY.toInt()) }
        leftPanel.children.add(selectInfoBox)

        refreshSelectInfoLabel()

        val statusBar = HBox()
        statusBar.alignment = Pos.TOP_LEFT
        val fpsLabel = DynamicLabel(500) { Translation.translate("status_bar.fps", EditorApplication.fps) }
        statusBar.children.add(fpsLabel)
        leftPanel.children.add(statusBar)

        val rightPanelBcg = VBox()
        rightPanelBcg.padding = Insets(7.0)
        rightPanelBcg.prefWidth = EditorApplication.PANEL_WIDTH
        rightPanelBcg.isFillToParentWidth = true
        rightPanelBcg.isFillToParentHeight = true
        root.setRight(rightPanelBcg)
        val rightPanel = VBox()
        rightPanel.maxWidth = EditorApplication.PANEL_WIDTH - 14
        rightPanelBcg.children.add(rightPanel)

        rightPanel.children.add(Label(Translation.translate("gui.world.label", EditorApplication.fps)))

        worldList = ComboBox()
        worldList.prefWidth = EditorApplication.PANEL_WIDTH - 20
        worldList.alignment = Pos.CENTER
        worldList.setOnAction { action -> Editor.currentWorld = Editor.getWorlds().first { it.name == worldList.value } }
        rightPanel.children.add(worldList)

        operationHistory = ScrollPane()
        operationHistory.padding = Insets(5.0)
        operationHistory.prefHeight = 500.0
        operationHistory.isFillToParentWidth = true
        rightPanel.children.add(operationHistory)

        return root
    }

    //Buttons don't update for some reason, had to re-create the entire UI section
    fun refreshSelectInfoLabel()
    {
        selectInfoBox.children.clear()

        if (Editor.selectedEntity !=null)
        {
            refreshEntityInfoLabel()
            return
        }
        val selectedArea = Editor.selectedArea
        if (selectedArea!=null)
        {
            if (selectedArea.min != selectedArea.max)
            {
                refreshAreaInfoLabel()
                return
            }
        }
        refreshBlockInfoLabel()
    }

    fun refreshBlockInfoLabel()
    {
        val selectedBlock = Editor.selectedArea?.world?.getBlockAt(Editor.selectedArea!!.min)

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", selectedBlock?.type ?: "-"))
        blockInfoLabel.isFillToParentWidth = true
        blockInfoLabel.alignment = Pos.TOP_LEFT

        val blockGlobalLocLabel = if (selectedBlock==null)
            Label(Translation.translate("gui.block_info.location.global", "-", "-", "-"))
        else
            Label(Translation.translate("gui.block_info.location.global", selectedBlock.location.globalX, selectedBlock.location.globalY, selectedBlock.location.globalZ))

        val blockChunkLocLabel = if (selectedBlock==null)
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

    fun refreshAreaInfoLabel()
    {
        val selectedArea = Editor.selectedArea!!

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

    fun refreshEntityInfoLabel()
    {
        val selectedEntity = Editor.selectedEntity!!

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
        val ohContainer = TreeView<String>()
        for (i in 0 until Operations.getOperations().size)
        {
            val item = TreeItem(Operations.getOperation(i).displayName)
            item.setOnMouseClicked { Operations.setPosition(i) }
            ohContainer.items.add(item)
            if (i == Operations.getPosition())
                ohContainer.selectItem(item)
        }
        operationHistory.content = ohContainer
    }

    fun buildPane(compoundTag: CompoundTag?, height: Double) : ScrollPane
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
            val valueBox = Label(entry.value.toTagString())
            rightHbox.children.add(valueBox)

            leftVBox.children.add(leftHbox)
            rightVBox.children.add(rightHbox)
        }

        return mainPane
    }

    fun refreshWorldList()
    {
        refreshOperationHistory()
        worldList.items.clear()
        for (world in Editor.getWorlds())
            worldList.items.add(world.name)
        for(i in 0 until worldList.items.size())
        {
            //todo
        }
        worldList.value = Editor.currentWorld.name
    }

    fun buildTree(base: TreeBase<String>, compoundTag: CompoundTag?)
    {
        if (compoundTag != null)
        {
            for (entry in compoundTag.entrySet())
            {
                val node = TreeItem(entry.key)
                base.items.add(node)
                if (entry.value.id == 10.toByte()) //todo magic value: compound
                    buildTree(node, entry.value as CompoundTag)
                else
                    node.items.add(TreeItem(entry.value.toTagString()))
            }
        }
    }
}