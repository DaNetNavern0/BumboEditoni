package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.world.World
import org.lwjgl.opengl.GL33.glBindVertexArray

class WorldRenderer(private val world: World)
{
    //todo multi-threaded/off-threaded baking would be really nice
    fun bake()
    {
        val cameraLocation = world.editorTab.camera.mutableLocation.toChunkLocation()

        EditorApplication.chunksToBake.removeIf { chunk ->
            val renderDistance = Settings.renderDistance
            if (!chunk.vertexData.isBuilt && cameraLocation.withinCubicDistance(chunk.location, renderDistance))
            {
                chunk.vertexData.updateVertices()
                return@removeIf true
            }
            false
        }
    }

    fun render()
    {
        for (chunk in world.editorTab.world.getLoadedChunks())
            chunk.vertexData.draw()
        glBindVertexArray(0)
    }
}