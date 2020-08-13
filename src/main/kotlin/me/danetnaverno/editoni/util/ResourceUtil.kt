package me.danetnaverno.editoni.util

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

object ResourceUtil
{
    private var jarFileSystem : FileSystem? = null

    fun getBuiltInResourcePath(resourceName: String): Path
    {
        val resourceURI = ResourceUtil::class.java.getResource(resourceName).toURI()

        if (resourceURI.scheme.equals("file", ignoreCase = true))
            return Paths.get(resourceURI)
        else if (resourceURI.scheme.equals("jar", ignoreCase = true))
        {
            if (jarFileSystem == null)
                jarFileSystem = FileSystems.newFileSystem(resourceURI, emptyMap<String, Any>())
            return jarFileSystem!!.getPath(resourceName)
        }
        throw IllegalArgumentException("Unknown resource scheme: " + resourceURI.scheme)
    }
}
