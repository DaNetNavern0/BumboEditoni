package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.Main
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.util.ResourceUtil.getBuiltInResourcePath
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import java.nio.file.Files

object Shader
{
    private var program = 0
    private var textureLocation = 0
    private var matrixLocation = 0

    init
    {
        program = glCreateProgram()
        val vertShader = loadShader(program, String(Files.readAllBytes(getBuiltInResourcePath("/shader.vert"))), GL_VERTEX_SHADER)
        val fragShader = loadShader(program, String(Files.readAllBytes(getBuiltInResourcePath("/shader.frag"))), GL_FRAGMENT_SHADER)

        glLinkProgram(program)
        val isLinked = IntArray(1)
        glGetProgramiv(program, GL_LINK_STATUS, isLinked)
        if(isLinked[0] == GL_FALSE)
        {
            val logger = LogManager.getLogger(Shader::class)
            logger.error("Failed to link a shader program")
            logger.error(glGetProgramInfoLog(program, 2048))
            Main.terminate()
        }
        glValidateProgram(program)
        val isValid = IntArray(1)
        glGetProgramiv(program, GL_VALIDATE_STATUS, isValid)
        if(isValid[0] == GL_FALSE)
        {
            val logger = LogManager.getLogger(Shader::class)
            logger.error("Failed to validate a shader program")
            logger.error(glGetProgramInfoLog(program, 2048))
            Main.terminate()
        }

        glDeleteShader(vertShader)
        glDeleteShader(fragShader)

        textureLocation = glGetUniformLocation(program, "in_texture")
        matrixLocation = glGetUniformLocation(program, "combined_matrix")
    }

    private fun loadShader(program: Int, shaderStr: String, type: Int) : Int
    {
        val shaderIndex = glCreateShader(type)
        glShaderSource(shaderIndex, shaderStr)
        glCompileShader(shaderIndex)

        val isCompiled = IntArray(1)
        glGetShaderiv(shaderIndex, GL_COMPILE_STATUS, isCompiled)
        if(isCompiled[0] == GL_FALSE)
        {
            val logger = LogManager.getLogger(Shader::class)
            logger.error("Failed to compile a shader")
            logger.error(glGetShaderInfoLog(shaderIndex, 2048))
            Main.terminate()
        }
        glAttachShader(program, shaderIndex)

        return shaderIndex
    }

    fun use()
    {
        glUseProgram(program)
        glUniform1i(textureLocation, 0)

        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            EditorApplication.combinedMatrix.get(buffer)
            glUniformMatrix4fv(matrixLocation, false, buffer)
        }
    }
}