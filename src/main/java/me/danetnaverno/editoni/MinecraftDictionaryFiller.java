package me.danetnaverno.editoni;

import me.danetnaverno.editoni.blockrender.BlockRendererAir;
import me.danetnaverno.editoni.blockrender.BlockRendererCube;
import me.danetnaverno.editoni.blockrender.BlockRendererDictionary;
import me.danetnaverno.editoni.util.ResourceLocation;
import me.danetnaverno.editoni.blocktype.BlockDictionary;
import me.danetnaverno.editoni.blocktype.BlockType;

public class MinecraftDictionaryFiller
{
    public static BlockType AIR;

    public static void init()
    {
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "air"), BlockRendererAir.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "cube"), BlockRendererCube.class);
        AIR = BlockDictionary.getBlockType(new ResourceLocation("minecraft:air"));
    }
}