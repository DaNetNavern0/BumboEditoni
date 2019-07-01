package me.danetnaverno.editoni.editor

import com.jogamp.opengl.glu.GLU
import me.danetnaverno.editoni.Prototype
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRendererDictionary
import me.danetnaverno.editoni.common.blocktype.BlockDictionary
import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.editor.operations.Operations
import me.danetnaverno.editoni.editor.operations.SetBlocksOperation
import me.danetnaverno.editoni.minecraft.world.MinecraftBlock
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.io.File

object Editor
{
    var selectedBlock: Block? = null
        private set
    lateinit var currentWorld: World

    private val worlds = mutableMapOf<File, World>()
    private val hiddenBlocks = mutableSetOf<Block>()

    fun loadWorld(worldFolder: File) : World
    {
        if (!worlds.containsKey(worldFolder))
        {
            val world = WorldIO.readWorld(worldFolder)
            worlds[worldFolder] = world
            return world
        }
        else
        {
            throw NotImplementedError() //todo
        }
    }

    fun displayLoop()
    {
        Editor.currentWorld.worldRenderer.render()

        val block = Editor.selectedBlock
        if (block != null)
        {
            GL11.glPushMatrix()
            GL11.glTranslatef(block.globalX.toFloat(), block.globalY.toFloat(), block.globalZ.toFloat())
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glColor4f(1f, 1f, 0f, 0.7f)
            BlockRendererDictionary.ERROR.draw(block)
            GL11.glColor3f(1f, 1f, 1f)
            GL11.glPopMatrix()
        }
    }

    fun controls()
    {
        if (InputHandler.keyDown(GLFW.GLFW_KEY_A))
            Camera.x += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_D))
            Camera.x -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
            Camera.z += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
            Camera.z -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
            Camera.y += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_SPACE))
            Camera.y -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_1))
            Camera.yaw -= 4f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_2))
            Camera.yaw += 4f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_3))
            Camera.pitch -= 4f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_4))
            Camera.pitch += 4f
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_R))
        {
            val blocks = ArrayList<Block>()
            val type = BlockDictionary.getBlockType(ResourceLocation("minecraft", "torch"))
            blocks.add(MinecraftBlock(currentWorld, Vector3i(-6, 4, 0), type, null, null))
            val operation = SetBlocksOperation(blocks)
            Operations.apply(operation)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_T))
        {
            val blocks = ArrayList<Block>()
            val type1 = BlockDictionary.getBlockType(ResourceLocation("minecraft", "stone"))
            val type2 = BlockDictionary.getBlockType(ResourceLocation("minecraft", "diorite"))
            blocks.add(MinecraftBlock(currentWorld.getChunkByBlockCoord(10, 10), Vector3i(7, 12, 7), type1, null, null))
            blocks.add(MinecraftBlock(currentWorld.getChunkByBlockCoord(10, 10), Vector3i(7, 13, 7), type2, null, null))
            val operation = SetBlocksOperation(blocks)
            Operations.apply(operation)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_F))
        {
            Operations.moveBack()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_G))
        {
            Operations.moveForward()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_H))
        {
            val block = selectedBlock
            if (block != null)
                hiddenBlocks.add(block)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_Y))
            hiddenBlocks.clear()
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_DELETE))
        {
            val block = selectedBlock
            if (block != null)
            {
                val air = MinecraftBlock(block.chunk, block.localPos, BlockType.airType, block.state, block.tileEntity)
                val operation = SetBlocksOperation(listOf(air))
                Operations.apply(operation)
            }
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_S) && InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            WorldIO.writeWorld(currentWorld, File("data/output"), WorldIO.SavingMethod.MC114)
            Prototype.logger.info("Saved!")
        }

        InputHandler.update()
    }

    fun onMouseClick(x: Int, y: Int)
    {
        selectBlock(findBlock(raycast(x, y)))
    }

    fun raycast(screenX: Int, screenY: Int): Vector3f
    {
        val viewport = BufferUtils.createIntBuffer(4)
        val mvmatrix = BufferUtils.createFloatBuffer(16)
        val projmatrix = BufferUtils.createFloatBuffer(16)
        val output = BufferUtils.createFloatBuffer(4)

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mvmatrix)
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projmatrix)
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport)

        val reverseY = viewport.get(3) - screenY
        val winZ = BufferUtils.createFloatBuffer(1)
        GL11.glReadPixels(screenX, reverseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, winZ)
        val z = winZ.get(0)
        GLU().gluUnProject(screenX.toFloat(), reverseY.toFloat(), z, mvmatrix, projmatrix, viewport, output)
        return Vector3f(output.get(0), output.get(1), output.get(2))
    }

    fun getHiddenBlocks(): List<Block>
    {
        return ArrayList<Block>(hiddenBlocks)
    }

    private fun findBlock(point: Vector3f): Block?
    {
        val floor = Vector3i(Math.floor(point.x.toDouble()).toInt() - 1, Math.floor(point.y.toDouble()).toInt() - 1, Math.floor(point.z.toDouble()).toInt() - 1)
        val ceiling = Vector3i(Math.ceil(point.x.toDouble()).toInt() + 1, Math.ceil(point.y.toDouble()).toInt() + 1, Math.ceil(point.z.toDouble()).toInt() + 1)

        var closest: Block? = null
        var min = java.lang.Float.MAX_VALUE

        for (x in floor.x..ceiling.x)
            for (y in floor.y..ceiling.y)
                for (z in floor.z..ceiling.z)
                {
                    val distance = point.distanceSquared(x + 0.5f, y + 0.5f, z + 0.5f)
                    if (distance < min)
                    {
                        val block = currentWorld.getBlockAt(x, y, z)
                        if (block != null && block.type != BlockType.airType && !getHiddenBlocks().contains(block))
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
    }

    fun selectBlock(block: Block?)
    {
        selectedBlock = block
        EditorGUI.refreshBlockInfoLabel()
    }
}