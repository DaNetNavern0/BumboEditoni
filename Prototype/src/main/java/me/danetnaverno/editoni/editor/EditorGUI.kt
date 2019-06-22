package me.danetnaverno.editoni.editor

import lwjgui.geometry.Insets
import lwjgui.geometry.Pos
import lwjgui.scene.Window
import lwjgui.scene.control.*
import lwjgui.scene.layout.GridPane
import lwjgui.scene.layout.Pane
import lwjgui.scene.layout.StackPane
import java.awt.Toolkit
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTextArea


object EditorGUI
{
    lateinit var blockInfoLabel: Label
        private set

    private lateinit var blockStateButton: Button

    private lateinit var tileEntityButton: Button

    private lateinit var grid : GridPane

    fun init(window: Window): Pane
    {
        val root = StackPane()
        root.alignment = Pos.TOP_LEFT
        root.padding = Insets(0.0)
        root.background = null
        root.setOnMouseClicked { Editor.onMouseClick(it.mouseX.toInt(),it.mouseY.toInt()) }

        val bar = MenuBar()
        bar.minWidth = EditorApplication.WIDTH.toDouble()
        root.children.add(bar)

        val file = Menu("File")
        file.items.add(MenuItem("New"))
        file.items.add(MenuItem("Open"))
        file.items.add(MenuItem("Save"))
        bar.items.add(file)

        val edit = Menu("Edit")
        edit.items.add(MenuItem("Undo"))
        edit.items.add(MenuItem("Redo"))
        bar.items.add(edit)

        grid = GridPane()
        grid.padding = Insets(10.0, 5.0, 5.0, 5.0)
        grid.background = null
        grid.hgap = 1
        grid.vgap = 1

        blockInfoLabel = Label("Type: -")
        blockInfoLabel.alignment = Pos.TOP_LEFT
        blockInfoLabel.fontSize = 20f
        blockInfoLabel.minWidth = 300.0
        grid.add(blockInfoLabel, 5, 20)

        blockStateButton = Button("State")
        blockStateButton.alignment = Pos.TOP_LEFT
        blockStateButton.fontSize = 20f
        blockStateButton.setOnAction { popup("ass") }
        grid.add(blockStateButton, 5, 22)

        tileEntityButton = Button("TileEntity")
        tileEntityButton.alignment = Pos.TOP_LEFT
        tileEntityButton.fontSize = 20f
        grid.add(tileEntityButton, 5, 24)

        root.children.add(grid)

        return root
    }

    //There is a bug with setDisabled and buttons that makes once disabled buttons never draw like they're enabled again.
    fun setStateButton(enabled: Boolean)
    {
        grid.children.remove(blockStateButton)
        blockStateButton = Button("State")
        blockStateButton.alignment = Pos.TOP_LEFT
        blockStateButton.fontSize = 20f
        blockStateButton.isDisabled = !enabled
        blockStateButton.setOnAction { popup(Editor.selectedBlock?.state?.toString() ?: "") }
        grid.add(blockStateButton, 5, 22)
    }

    fun setTileEntityButton(enabled: Boolean)
    {
        grid.children.remove(tileEntityButton)
        tileEntityButton = Button("TileEntity")
        tileEntityButton.alignment = Pos.TOP_LEFT
        tileEntityButton.fontSize = 20f
        tileEntityButton.isDisabled = !enabled
        tileEntityButton.setOnAction { popup(Editor.selectedBlock?.tileEntity?.toString() ?: "") }
        grid.add(tileEntityButton, 5, 24)
    }

    //todo tmp
    val popWidth = 400
    val popHeight = 400

    fun popup(popup: String)
    {
        val frame = JFrame()
        frame.layout = null
        val text = JTextArea()
        text.lineWrap = true
        val cancelButton = JButton()
        val applyButton = JButton()

        text.text = popup
        cancelButton.text = "Cancel"
        applyButton.text = "Apply"
        frame.add(text)
        frame.add(cancelButton)
        frame.add(applyButton)
        frame.pack()
        val dim = Toolkit.getDefaultToolkit().screenSize
        frame.setBounds(dim.width / 2 - popWidth / 2, dim.height / 2 - popHeight / 2, popWidth + 20, popHeight + 50)
        text.setBounds(10, 10, popWidth - 20, popHeight - 60)
        cancelButton.setBounds(20, popHeight - 40, 64, 32)
        applyButton.setBounds(popWidth - 64 - 20, popHeight - 40, 64, 32)
        frame.isVisible = true

        cancelButton.addActionListener { frame.dispose() }
        applyButton.addActionListener { /* todo */ }
    }
}