package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.util.location.BlockLocation
import java.nio.file.Path

abstract class AbstractEditor
{
    lateinit var currentWorld: World
    protected val worlds = mutableMapOf<Path, World>()
    protected val hiddenBlocks = mutableSetOf<BlockLocation>()

    var selectedBlock: Block? = null
        protected set

    var selectedEntity: Entity? = null
        protected set

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
