package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.operation.OperationList
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.World
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL33
import org.lwjgl.system.MemoryStack
import kotlin.math.abs

class EditorTab(var world: World)
{
    var selectedEntity: Entity? = null
        private set
    var selectedArea: BlockArea? = null
        private set
    val operationList = OperationList(this)
    var camera = Camera()

    fun selectEntity(entity: Entity?)
    {
        selectedArea = null
        selectedEntity = entity
        EditorUserInputHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun selectArea(area: BlockArea?)
    {
        selectedEntity = null
        selectedArea = area
        EditorUserInputHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun raycast(screenX: Int, screenY: Int): Vector3f?
    {
        val visibleViewport = EditorApplication.viewportDimensions
        MemoryStack.stackPush().use { stack ->
            val buf = stack.mallocFloat(1)
            val reverseY = EditorApplication.windowHeight - screenY
            GL33.glReadPixels(screenX, reverseY, 1, 1, GL33.GL_DEPTH_COMPONENT, GL33.GL_FLOAT, buf)
            val screenZ = buf.get(0)
            return unProject(screenX.toFloat(), reverseY.toFloat(), screenZ,
                    EditorApplication.combinedMatrix,
                    visibleViewport.x.toFloat(), 0f,
                    visibleViewport.width.toFloat(), visibleViewport.height.toFloat())
        }
    }

    private fun unProject(screenX: Float, screenY: Float, screenZ: Float,
                          combinedMatrix: Matrix4f,
                          viewportOffsetX: Float, viewportOffsetY: Float, viewportWidth: Float, viewportHeight: Float): Vector3f?
    {
        val inVector = Vector4f()
        val inv = Matrix4f()
        combinedMatrix.invert(inv)

        inVector.x = ((screenX - viewportOffsetX) / viewportWidth) * 2.0f - 1.0f
        inVector.y = ((screenY - viewportOffsetY) / viewportHeight) * 2.0f - 1.0f
        inVector.z = screenZ * 2.0f - 1.0f
        inVector.w = 1.0f

        val outVector = inv.transform(inVector)
        if (abs(outVector.w) < 0.0001)
            return null
        outVector.w = 1.0f / outVector.w
        return Vector3f(outVector.x * outVector.w, outVector.y * outVector.w, outVector.z * outVector.w)
    }

    override fun toString(): String
    {
        return world.toString()
    }

    class Camera
    {
        var x = 23.0
        var y = 81.0
        var z = 12.0
        var pitch = 315.0
        var yaw = 74.0

        val mutableLocation: BlockLocationMutable = BlockLocationMutable(0, 0, 0)
            get()
            {
                field.set(x.toInt(), y.toInt(), z.toInt())
                return field
            }
    }
}