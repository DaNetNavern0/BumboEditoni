package me.danetnaverno.editoni.clipboard;

import me.danetnaverno.editoni.world.Block;

import java.util.ArrayList;
import java.util.List;

public class ClipboardPrototype
{
    private static List<Block> clipboard = new ArrayList<>();

    public static void set(List<Block> blocks)
    {
        clipboard = blocks;
    }

    public static void add(List<Block> blocks)
    {
        clipboard.addAll(blocks);
    }

    public static List<Block> get()
    {
        return new ArrayList<>(clipboard);
    }

    public static int size()
    {
        return clipboard.size();
    }
}