package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.world.World
import me.danetnaverno.editoni.world.WorldRenderer

class EditorTab(var world: World)
{
    val worldRenderer = WorldRenderer(this)
    var selectedArea: BlockArea? = null
    var camera = Camera()

    override fun toString(): String
    {
        return world.path.parent.fileName.toString()
    }

    class Camera
    {
        var x = 23.0
        var y = 11.0
        var z = 12.0
        var pitch = 315.0
        var yaw = 74.0
    }
}