package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.editor.EditorGUI;
import me.danetnaverno.editoni.util.Translation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operations
{
    private static Map<World, Operations> operationManagers = new HashMap<>();

    private List<Operation> operationList = new ArrayList<>();
    private int position = 0;
    private World world;

    public static Operations get(World world)
    {
        return operationManagers.computeIfAbsent(world, it -> new Operations(world));
    }

    private static void createOperations(World world)
    {
        if (!operationManagers.containsKey(world))
            operationManagers.put(world, new Operations(world));
    }

    private Operations(World world)
    {
        this.world = world;
    }

    public void apply(Operation operation)
    {
        if (position >= operationList.size() - 1)
        {
            operationList.add(operation);
            position = operationList.size() - 1;
            operation.apply();
            world.worldRenderer.refreshRenderCache();
        }
        else
        {
            int dialogButton = JOptionPane.showConfirmDialog(null,
                    Translation.INSTANCE.translate("operation.confirm_forced_operation", operationList.size() - position),
                    "", JOptionPane.YES_NO_OPTION);
            if (dialogButton == JOptionPane.YES_OPTION)
                applyForced(operation);
        }
        EditorGUI.INSTANCE.refreshOperationHistory();
    }

    /**
     * Erases all operations after selected position and applies the operation
     */
    public void applyForced(Operation operation)
    {
        operationList.subList(position, operationList.size()).clear();
        operationList.add(operation);
        position = operationList.size() - 1;
    }

    public List<Operation> getAll()
    {
        return new ArrayList<>(operationList);
    }

    public Operation getOperation(int i)
    {
        return operationList.get(i);
    }

    public void moveBack()
    {
        setPosition(position--);
    }

    public void moveForward()
    {
        setPosition(position++);
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int newPosition)
    {
        newPosition = Math.max(0, Math.min(newPosition, operationList.size() - 1));
        if (newPosition < position)
        {
            for (int i = position; i > newPosition; i--)
                operationList.get(i).rollback();
            operationList.get(newPosition).apply();
        }
        else
        {
            for (int i = position + 1; i <= newPosition; i++)
                operationList.get(i).apply();
        }
        world.worldRenderer.refreshRenderCache();
        position = newPosition;
    }
}