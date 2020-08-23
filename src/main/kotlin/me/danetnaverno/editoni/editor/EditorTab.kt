package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.operations.OperationList
import me.danetnaverno.editoni.render.WorldRenderer
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.World

class EditorTab(var world: World)
{
    val worldRenderer = WorldRenderer(this)
    var selectedEntity: Entity? = null
        private set
    var selectedArea: BlockArea? = null
        private set
    var camera = Camera()
    val operationList = OperationList(this)

    fun selectEntity(entity: Entity?)
    {
        selectedArea = null
        selectedEntity = entity
        EditorUserHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun selectArea(area: BlockArea?)
    {
        selectedEntity = null
        selectedArea = area
        EditorUserHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    override fun toString(): String
    {
        return world.path.parent.fileName.toString()
    }

    class Camera
    {
        var x = 23.0
        var y = 11.0
        //var y = 81.0
        var z = 12.0
        var pitch = 315.0
        var yaw = 74.0
    }
}