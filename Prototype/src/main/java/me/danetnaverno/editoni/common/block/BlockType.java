package me.danetnaverno.editoni.common.block;

import me.danetnaverno.editoni.engine.render.BlockRenderer;
import me.danetnaverno.editoni.engine.render.BlockRendererAir;
import me.danetnaverno.editoni.engine.render.BlockRendererCube;
import me.danetnaverno.editoni.engine.texture.Texture;

public class BlockType
{
    public static BlockType AIR = new BlockType("minecraft:air", new BlockRendererAir());
    public static BlockType STONE = new BlockType("minecraft:stone", new BlockRendererCube(Texture.get("blocks/stone")));

    public final String id;
    public final BlockRenderer renderer;

    public BlockType(String id, BlockRenderer renderer)
    {
        this.id = id;
        this.renderer = renderer;
    }
}
