package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.renderer.Renderer;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.Entity;
import me.danetnaverno.editoni.common.world.WorldRenderer;
import me.danetnaverno.editoni.editor.AbstractEditor;
import me.danetnaverno.editoni.minecraft.util.location.LocationUtilsKt;
import me.danetnaverno.editoni.texture.Texture;
import me.danetnaverno.editoni.util.Camera;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinecraftWorldRenderer extends WorldRenderer
{
    private Map<Chunk, boolean[][]> renderCache = new HashMap<>();

    public MinecraftWorldRenderer(MinecraftWorld world)
    {
        super(world);
    }

    @Override
    public void render()
    {
        ChunkLocation cameraLocation = new BlockLocation((int) Camera.x, (int) Camera.y, (int) Camera.z).toChunkLocation();

        List<MinecraftRegion> visibleRegions = ((MinecraftWorld) world).getRegions().stream().filter(it ->
                it.location.distance(LocationUtilsKt.toRegionLocation(cameraLocation)) <= 2).collect(Collectors.toList());

        for (MinecraftRegion region : visibleRegions)
        {
            int renderDistance = AbstractEditor.Companion.getINSTANCE().getRenderDistance();
            for(int x=-renderDistance; x<=renderDistance;x++)
                for(int z=-renderDistance; z<=renderDistance;z++)
                {
                    if (LocationUtilsKt.toRegionLocation(cameraLocation.add(x, z)).equals(region.location))
                        region.loadChunkAt(cameraLocation.add(x, z));
                }

            List<Chunk> visibleChunks = region.getLoadedChunks().stream()
                    .filter(it -> it.location.distance(cameraLocation) <= AbstractEditor.Companion.getINSTANCE().getRenderDistance())
                    .collect(Collectors.toList());

            for (Chunk chunk : visibleChunks)
            {
                if ((chunk.location.x + chunk.location.z) % 2 == 0)
                    Texture.get(new ResourceLocation("common", "chunk_a")).bind();
                else
                    Texture.get(new ResourceLocation("common", "chunk_b")).bind();
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3i(chunk.location.x * 16, 0, chunk.location.z * 16);
                GL11.glVertex3i(chunk.location.x * 16, 0, (chunk.location.z + 1) * 16);
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, (chunk.location.z + 1) * 16);
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, chunk.location.z * 16);
                GL11.glEnd();

                BlockType[][] sections = chunk.getBlockTypes();
                boolean[][] chunkCache = renderCache.get(chunk);

                if (chunkCache == null)
                {
                    chunkCache = new boolean[16][];

                    for (int section = 0; section < 16; section++)
                    {
                        BlockType[] blockTypes = sections[section];
                        if (blockTypes == null)
                            continue;
                        boolean[] sectionCache = new boolean[4096];

                        chunkCache[section] = sectionCache;
                        for (int index = 0; index < 4096; index++)
                        {
                            BlockType blockType = blockTypes[index];
                            if (blockType == null)
                                continue;
                            BlockLocation location = LocationUtilsKt.blockLocationFromSectionIndex(chunk, section, index);
                            sectionCache[index] = blockType.getRenderer().draw(world, location);
                        }
                    }
                    renderCache.put(chunk, chunkCache);
                }
                else
                {
                    for (int section = 0; section < 16; section++)
                    {
                        BlockType[] blockTypes = sections[section];
                        if (blockTypes == null)
                            continue;
                        boolean[] sectionCache = chunkCache[section];

                        for (int index = 0; index < 4096; index++)
                        {
                            if (sectionCache[index])
                            {
                                BlockType blockType = blockTypes[index];
                                if (blockType == null)
                                    continue;
                                BlockLocation location = LocationUtilsKt.blockLocationFromSectionIndex(chunk, section, index);
                                blockType.getRenderer().draw(world, location);
                            }
                        }
                    }
                }
                for (Entity entity : chunk.getEntities())
                    entity.getType().getRenderer().draw(entity);

                Renderer.draw();
            }
        }
    }

    @Override
    public void refreshRenderCache()
    {
        renderCache.clear();
    }
}
