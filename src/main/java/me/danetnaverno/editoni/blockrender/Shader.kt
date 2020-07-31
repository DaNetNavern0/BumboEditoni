package me.danetnaverno.editoni.blockrender

import me.danetnaverno.editoni.Main
import me.danetnaverno.editoni.texture.FreeTexture
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.util.ResourceUtil.getBuiltInResourcePath
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL44.*
import java.nio.file.Files


object Shader
{
    var program = 0

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

    fun bind()
    {
        glUseProgram(program)
        glActiveTexture(GL_TEXTURE0)
        val loc = glGetUniformLocation(program, "a_texture")
        if (false)
            FreeTexture[ResourceLocation("minecraft", "granite")].bind()
        else
            glBindTexture(GL_TEXTURE_2D_ARRAY, TextureAtlas.todoInstance.atlasTexture)
        glUniform1i(loc, 0)
    }
}