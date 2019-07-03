package me.danetnaverno.editoni.minecraft;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blockrender.*;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.entitytype.EntityDictionary;
import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.BlockStateDictionary;
import me.danetnaverno.editoni.minecraft.blockrender.BlockRendererMinecraftChest;
import me.danetnaverno.editoni.minecraft.blockrender.BlockRendererMinecraftLiquid;
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftChestBlockState;
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftLiquidState;
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftSignBlockState;

public class MinecraftDictionaryFiller
{
    public static void init()
    {
        EntityRendererDictionary.register(new ResourceLocation("minecraft", "default"), EntityRendererDefault.class);

        EntityDictionary.register(new ResourceLocation("minecraft", "default"), new EntityType(new ResourceLocation("minecraft", "default"), new EntityRendererDefault()));

        BlockRendererDictionary.register(new ResourceLocation("minecraft", "air"), BlockRendererAir.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "cube"), BlockRendererCube.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "chest"), BlockRendererMinecraftChest.class);
        BlockRendererDictionary.register(new ResourceLocation("minecraft", "liquid"), BlockRendererMinecraftLiquid.class);

        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:oak_wall_sign")),
                MinecraftSignBlockState.class);
        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:chest")),
                MinecraftChestBlockState.class);
        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:water")),
                MinecraftLiquidState.class);
        BlockStateDictionary.register(
                BlockDictionary.getBlockType(new ResourceLocation("minecraft:lava")),
                MinecraftLiquidState.class);

        BlockDictionary.register(new ResourceLocation("minecraft", "air"), new BlockType(new ResourceLocation("minecraft", "air"), new BlockRendererAir()));
    }
}
