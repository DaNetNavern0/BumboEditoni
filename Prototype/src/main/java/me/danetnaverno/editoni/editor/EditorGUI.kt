package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.scene.Window
import lwjgui.scene.control.*
import lwjgui.scene.layout.BorderPane
import lwjgui.scene.layout.HBox
import lwjgui.scene.layout.Pane
import lwjgui.scene.layout.VBox
import me.danetnaverno.editoni.editor.operations.Operations
import me.danetnaverno.editoni.util.Translation
import net.querz.nbt.CompoundTag
import javax.swing.JFileChooser

object EditorGUI
{
    private lateinit var root : BorderPane
    private lateinit var blockInfoBox : VBox
    private lateinit var operationHistory : ScrollPane

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

        val file = Menu("File")
        val open = MenuItem("Open")
        open.setOnAction { JFileChooser().showOpenDialog(null) }
        file.items.add(open)
        file.items.add(MenuItem("Save"))
        bar.items.add(file)

        val edit = Menu("Edit")
        edit.items.add(MenuItem("Undo"))
        edit.items.add(MenuItem("Redo"))
        bar.items.add(edit)

        root.setTop(bar)

        val leftPanel = VBox()
        leftPanel.spacing = 4.0
        leftPanel.padding = Insets(0.0)
        leftPanel.background = null

        blockInfoBox = VBox()
        blockInfoBox.spacing = 4.0
        blockInfoBox.padding = Insets(5.0)
        blockInfoBox.prefWidth = 300.0
        blockInfoBox.setOnMouseClicked { Editor.onMouseClick(it.mouseX.toInt(),it.mouseY.toInt()) }
        leftPanel.children.add(blockInfoBox)

        refreshBlockInfoLabel()

        operationHistory = ScrollPane()
        operationHistory.isFillToParentWidth = true
        leftPanel.children.add(operationHistory)
        root.setLeft(leftPanel)

        return root
    }

    //Buttons don't update for some reason, had to re-create the entire UI section
    fun refreshBlockInfoLabel()
    {
        val selectedBlock = Editor.selectedBlock

        blockInfoBox.children.clear()

        val blockInfoLabel = Label(Translation.translate("gui.block_info.type", selectedBlock?.type ?: "-"))
        blockInfoLabel.alignment = Pos.TOP_LEFT
        blockInfoLabel.isFillToParentWidth = true

        val blockStatePane = buildPane(selectedBlock?.state?.tag)
        val tileEntityPane = buildPane(selectedBlock?.tileEntity?.tag)

        blockInfoBox.children.add(blockInfoLabel)
        blockInfoBox.children.add(blockStatePane)
        blockInfoBox.children.add(tileEntityPane)
    }

    fun refreshOperationHistory()
    {
        val ohContainer = VBox()
        for (i in 1 until Operations.getOperations().size)
        {
            val text = Label(Operations.getOperation(i).toString())
            text.setOnMouseClicked { Operations.setPosition(i) }
            ohContainer.children.add(text)
        }
        operationHistory.content = ohContainer
    }

    fun buildPane(compoundTag: CompoundTag?) : ScrollPane
    {
        val mainPane = ScrollPane()
        val hBox = HBox()
        mainPane.isFillToParentWidth = true
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