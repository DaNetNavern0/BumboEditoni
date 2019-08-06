package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Chunk
{
    public final World world;
    public final ChunkLocation location;

    public Chunk(World world, ChunkLocation location)
    {
        this.world = world;
        this.location = location;
    }

    @NotNull
    public abstract BlockType[][] getBlockTypes();

    @Nullable
    public abstract Block getBlockAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockType getBlockTypeAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockState getBlockStateAt(@NotNull BlockLocation location);

    @Nullable
    public abstract TileEntity getTileEntityAt(@NotNull BlockLocation location);

    public abstract void setBlock(@NotNull Block block);

    public abstract Collection<Entity> getEntities();

    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        return getEntities().stream().filter( it-> it.getLocation().distance(location) < radius).collect(Collectors.toList());
    }
}
