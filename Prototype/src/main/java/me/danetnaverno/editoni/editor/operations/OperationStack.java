package me.danetnaverno.editoni.editor.operations;

import java.util.ArrayList;
import java.util.List;

public class OperationStack
{
    public static List<Operation> operations = new ArrayList<>();
    public static int position = 0;

    static
    {
        operations.add(new BlankOperation());
    }

    public static void add(Operation operation)
    {
        operations.add(operation);
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