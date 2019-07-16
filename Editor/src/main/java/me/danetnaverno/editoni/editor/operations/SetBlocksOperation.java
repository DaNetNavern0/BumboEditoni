package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.editor.Editor;

import java.util.Collection;
import java.util.stream.Collectors;

public class SetBlocksOperation extends Operation
{
    private Collection<Block> rollback;
    private Collection<Block> blocks;

    public SetBlocksOperation(Collection<Block> blocks)
    {
        this.blocks = blocks;
        rollback = blocks.stream().map(it -> it.getChunk().getBlockAt(it.getLocation())).collect(Collectors.toList());
    }

    public void apply()
    {
        innerApply(blocks);
    }

    public void rollback()
    {
        innerApply(rollback);
    }

    public void innerApply(Collection<Block> blocks)
    {
        for (Block block : blocks)
            Editor.INSTANCE.getCurrentWorld().setBlock(block);
    }
}
