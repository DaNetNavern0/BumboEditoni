package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.editor.Editor;

import java.util.ArrayList;
import java.util.List;

public class Operations
{
    public static List<Operation> operations = new ArrayList<>();
    public static int position = 0;

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
            Editor.INSTANCE.selectBlock(null);
        }
        else
        {
            //todo confirmation window
        }
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

    public static void moveBack()
    {
        if (position > 0)
        {
            operations.get(position).rollback();
            position--;
            operations.get(position).apply();
        }
    }

    public static void moveForward()
    {
        if (position < operations.size() - 1)
        {
            position++;
            operations.get(position).apply();
        }
    }
}