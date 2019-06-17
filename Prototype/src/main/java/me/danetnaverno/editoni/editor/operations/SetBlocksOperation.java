package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.common.world.Block;

import java.util.ArrayList;
import java.util.List;

public class SetBlocksOperation extends Operation
{
    public List<Block> rollback = new ArrayList<>();
    public List<Block> blocks = new ArrayList<>();

    public SetBlocksOperation()
    {
    }

    public void invoke()
    {

    }
}
