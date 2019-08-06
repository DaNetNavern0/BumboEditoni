package me.danetnaverno.editoni.editor;

import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.editor.operations.Operations;
import me.danetnaverno.editoni.util.Camera;
import me.danetnaverno.editoni.util.RobertoGarbagio;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GarbageChunkCollector
{
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static List<Chunk> chunksToUnload = null;
    public static void unloadExcessChunks(World world)
    {
        if (chunksToUnload==null)
        {
            executor.submit(() -> {
                Operations operations = Operations.get(world);
                List<Chunk> result = world.getLoadedChunks();
                ChunkLocation cameraLocation = new BlockLocation((int) Camera.x, (int) Camera.y, (int) Camera.z).toChunkLocation();

                if (operations.savePosition < operations.getPosition())
                    for (int i = operations.savePosition; i <= operations.getPosition(); i++)
                        result.removeAll(operations.getOperation(i).getAlteredChunks());
                else
                    for (int i = operations.savePosition; i >= operations.getPosition(); i--)
                        result.removeAll(operations.getOperation(i).getAlteredChunks());

                int renderDistance = AbstractEditor.INSTANCE.getRenderDistance();
                result.removeIf(it -> Math.abs(it.location.x - cameraLocation.x) <= renderDistance
                        || Math.abs(it.location.z - cameraLocation.z) <= renderDistance);
                chunksToUnload = result;
            });
        }
        else
        {
            RobertoGarbagio.logger.info("total chunks:" + world.getLoadedChunks().size());
            world.unloadChunks(chunksToUnload);
            RobertoGarbagio.logger.info("unloaded " + chunksToUnload.size() + " chunks");
            chunksToUnload = null;
        }
    }
}
