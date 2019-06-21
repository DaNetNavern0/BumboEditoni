package me.danetnaverno.editoni.editor;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Window;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;

public class EditorGUI
{
    public static Label blockInfo;

    public static Pane init(Window window)
    {
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(0));
        root.setBackground(null);

        MenuBar bar = new MenuBar();
        bar.setMinWidth(EditorApplication.WIDTH);
        root.getChildren().add(bar);

        Menu file = new Menu("File");
        file.getItems().add(new MenuItem("New"));
        file.getItems().add(new MenuItem("Open"));
        file.getItems().add(new MenuItem("Save"));
        bar.getItems().add(file);

        Menu edit = new Menu("Edit");
        edit.getItems().add(new MenuItem("Undo"));
        edit.getItems().add(new MenuItem("Redo"));
        bar.getItems().add(edit);


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,5,5,5));
        grid.setBackground(null);
        grid.setHgap(1);
        grid.setVgap(1);

        blockInfo = new Label("Type: -");
        blockInfo.setAlignment(Pos.TOP_LEFT);
        blockInfo.setFontSize(20);
        blockInfo.setMinWidth(300);
        grid.add(blockInfo, 5, 20);

        Button state = new Button("State");
        state.setAlignment(Pos.TOP_LEFT);
        state.setFontSize(20);
        grid.add(state, 5, 22);

        Button tile = new Button("TileEntity");
        tile.setAlignment(Pos.TOP_LEFT);
        tile.setFontSize(20);
        grid.add(tile, 5, 24);


        root.getChildren().add(grid);

        return root;
    }
}