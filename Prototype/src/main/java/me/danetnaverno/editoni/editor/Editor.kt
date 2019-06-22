package me.danetnaverno.editoni.editor

import com.jogamp.opengl.glu.GLU
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.block.BlockDictionary
import me.danetnaverno.editoni.common.block.BlockType
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.editor.operations.OperationStack
import me.danetnaverno.editoni.editor.operations.SetBlocksOperation
import me.danetnaverno.editoni.minecraft.world.MinecraftBlock
import me.danetnaverno.editoni.minecraft.world.MinecraftRegion
import me.danetnaverno.editoni.minecraft.world.MinecraftWorld
import net.querz.nbt.mca.MCAUtil
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.io.File
import java.util.regex.Pattern

object Editor
{
    var selectedBlock: Block? = null
        private set

    lateinit var world: MinecraftWorld
        private set

    private val hiddenBlocks = mutableSetOf<Block>()

    private val mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca")

    fun loadWorld(worldFolder: File)
    {
        val world = MinecraftWorld()
        val regionFolder = File(worldFolder, "region")
        for (file in regionFolder.listFiles()!!)
        {
            val matcher = mcaRegex.matcher(file.name)
            if (matcher.matches())
            {
                val x = Integer.parseInt(matcher.group(1))
                val z = Integer.parseInt(matcher.group(2))
                world.addRegion(MinecraftRegion(MCAUtil.readMCAFile(file), x, z))
            }
        }
        this.world = world
    }

    fun displayLoop()
    {
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
            val type = BlockDictionary.getBlockType(ResourceLocation("minecraft", "chest"))
            blocks.add(MinecraftBlock(world.getChunkByBlockCoord(10, 10), Vector3i(7, 12, 7), type, null, null))
            val operation = SetBlocksOperation(blocks)
            OperationStack.add(operation)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_T))
        {
            val blocks = ArrayList<Block>()
            val type1 = BlockDictionary.getBlockType(ResourceLocation("minecraft", "stone"))
            val type2 = BlockDictionary.getBlockType(ResourceLocation("minecraft", "diorite"))
            blocks.add(MinecraftBlock(world.getChunkByBlockCoord(10, 10), Vector3i(7, 12, 7), type1, null, null))
            blocks.add(MinecraftBlock(world.getChunkByBlockCoord(10, 10), Vector3i(7, 13, 7), type2, null, null))
            val operation = SetBlocksOperation(blocks)
            OperationStack.add(operation)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_F))
        {
            OperationStack.moveBack()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_G))
        {
            OperationStack.moveForward()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_H))
        {
            val block = selectedBlock
            if (block!=null)
                hiddenBlocks.add(block)
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_Y))
            hiddenBlocks.clear()
        InputHandler.update()
    }

    fun onMouseClick(x: Int, y: Int)
    {
        val ass = raycast(x, y)
        selectBlock(findBlock(ass))
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

    fun getHiddenBlocks() : List<Block>
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
                        val block = world.getBlockAt(x, y, z)
                        if (block != null && block.type != BlockType.AIR && !getHiddenBlocks().contains(block))
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
    }

    private fun selectBlock(block: Block?)
    {
        selectedBlock = block
        EditorGUI.blockInfoLabel.text = "Type: " + (block?.type ?: "-")
        EditorGUI.setStateButton(block?.state != null)
        EditorGUI.setTileEntityButton(block?.tileEntity != null)
    }
}
