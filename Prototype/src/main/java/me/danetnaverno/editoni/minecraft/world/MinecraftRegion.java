package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.mca.MCAFile;

import java.util.ArrayList;
import java.util.List;

public class MinecraftRegion
{
    public int x,z;
    public List<Chunk> chunks = new ArrayList<>();

    public MinecraftRegion(MCAFile mcaFile, int regionX, int regionZ)
    {
        x = regionX;
        z = regionZ;
        for (int x = 0; x < 32; x++)
            for (int z = 0; z < 32; z++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(x, z);
                if (mcaChunk != null)
                    chunks.add(new MinecraftChunk(mcaChunk, x, z));
            }
    }
}