package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.editor.Editor;
import me.danetnaverno.editoni.editor.EditorGUI;
import me.danetnaverno.editoni.util.Translation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Operations
{
    private static List<Operation> operations = new ArrayList<>();
    private static int position = 0;

    static
    {
        operations.add(new BlankOperation());
    }

    public static void apply(Operation operation)
    {
        if (position == operations.size() - 1)
        {
            operations.add(operation);
            position = operations.size() - 1;
            operation.apply();
            Editor.INSTANCE.currentWorld.worldRenderer.refreshRenderCache();
        }
        else
        {
            int dialogButton = JOptionPane.showConfirmDialog(null,
                    Translation.INSTANCE.translate("operation.confirm_forced_operation", operations.size() - position),
                    "", JOptionPane.YES_NO_OPTION);
            if (dialogButton == JOptionPane.YES_OPTION)
                applyForced(operation);
        }
        EditorGUI.INSTANCE.refreshOperationHistory();
    }

    /**
     * Erases all operations after selected position and applies the operation
     */
    public static void applyForced(Operation operation)
    {
        operations.subList(position, operations.size()).clear();
        operations.add(operation);
        position = operations.size() - 1;
    }

    public static List<Operation> getOperations()
    {
        return new ArrayList<>(operations);
    }

    public static Operation getOperation(int i)
    {
        return operations.get(i);
    }

    public static void moveBack()
    {
        setPosition(position--);
    }

    public static void moveForward()
    {
        setPosition(position++);
    }

    public static int getPosition()
    {
        return position;
    }

    public static void setPosition(int newPosition)
    {
        newPosition = Math.max(0, Math.min(newPosition, operations.size() - 1));
        if (newPosition < position)
        {
            for (int i = position; i > newPosition; i--)
                operations.get(i).rollback();
        }
        else
        {
            for (int i = position + 1; i <= newPosition; i++)
                operations.get(i).apply();
        }
        Editor.INSTANCE.currentWorld.worldRenderer.refreshRenderCache();
        position = newPosition;
    }
}