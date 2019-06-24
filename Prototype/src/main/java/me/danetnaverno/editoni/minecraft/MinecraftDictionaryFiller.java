package me.danetnaverno.editoni.minecraft;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.block.BlockDictionary;
import me.danetnaverno.editoni.common.block.BlockType;
import me.danetnaverno.editoni.common.blockrender.BlockRendererAir;
import me.danetnaverno.editoni.common.blockrender.BlockRendererCube;
import me.danetnaverno.editoni.common.blockrender.BlockRendererDictionary;
import me.danetnaverno.editoni.common.world.BlockStateDictionary;
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftChestBlockState;
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftSignBlockState;
import me.danetnaverno.editoni.minecraft.blockrender.BlockRendererMinecraftChest;

public class MinecraftDictionaryFiller
{
    public static void init()
    {
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "air"), BlockRendererAir.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "cube"), BlockRendererCube.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "chest"), BlockRendererMinecraftChest.class);

        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:oak_wall_sign")),
                MinecraftSignBlockState.class);

        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:chest")),
                MinecraftChestBlockState.class);

        BlockDictionary.register(new ResourceLocation("minecraft", "air"), new BlockType(new ResourceLocation("minecraft", "air"), new BlockRendererAir()));
    }
}
