package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.Main
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.util.ResourceUtil.getBuiltInResourcePath
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL44.*
import org.lwjgl.system.MemoryStack
import java.nio.file.Files

object Shader
{
    var program = 0
    private var texture = 0
    private var proj = 0

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

        texture = glGetUniformLocation(program, "in_texture")
        proj = glGetUniformLocation(program, "combined_matrix")
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
        glUniform1i(texture, 0)

        MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            EditorApplication.combinedMatrix.get(buffer)
            glUniformMatrix4fv(proj, false, buffer)
        }
    }
}