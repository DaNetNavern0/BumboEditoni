package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.util.location.BlockLocation
import java.nio.file.Path

abstract class AbstractEditor
{
    protected val worlds = mutableMapOf<Path, World>()
    val hiddenBlocks = mutableSetOf<BlockLocation>()

    abstract var currentWorld: World

    var selectedArea: BlockArea? = null
        protected set

    var selectedEntity: Entity? = null
        protected set

    var renderDistance = 3

    init
    {
        INSTANCE = this
    }

    fun getWorlds() : Collection<World>
    {
        return worlds.values.toList()
    }

    companion object
    {
        lateinit var INSTANCE : AbstractEditor
            protected set
    }
}
