package me.danetnaverno.editoni.common.block;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.engine.render.BlockRenderer;
import me.danetnaverno.editoni.engine.render.BlockRendererAir;

public class BlockType
{
    public static BlockType AIR = new BlockType(new ResourceLocation("minecraft","air"), new BlockRendererAir());

    public final ResourceLocation id;
    public final BlockRenderer renderer;

    public BlockType(ResourceLocation id, BlockRenderer renderer)
    {
        this.id = id;
        this.renderer = renderer;
    }

    @Override
    public String toString()
    {
        return id.toString();
    }
}
