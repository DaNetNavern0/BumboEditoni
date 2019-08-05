package me.danetnaverno.editoni.common.world;

import org.jetbrains.annotations.NotNull;

public abstract class WorldRenderer
{
    public final World world;

    public WorldRenderer(@NotNull World world)
    {
        this.world = world;
    }

    public abstract void render();

    public abstract void refreshRenderCache();
}