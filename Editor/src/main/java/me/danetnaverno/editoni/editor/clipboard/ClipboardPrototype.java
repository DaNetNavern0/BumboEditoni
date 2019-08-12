package me.danetnaverno.editoni.editor.clipboard;

import me.danetnaverno.editoni.common.world.Block;

import java.util.ArrayList;
import java.util.List;

public class ClipboardPrototype
{
    private static List<List<Block>> clipboard = new ArrayList<>();

    public static void add(List<Block> blocks)
    {
        clipboard.add(blocks);
    }

    public static List<Block> get(int i)
    {
        return new ArrayList<>(clipboard.get(i));
    }

    public static int size()
    {
        return clipboard.size();
    }
}
