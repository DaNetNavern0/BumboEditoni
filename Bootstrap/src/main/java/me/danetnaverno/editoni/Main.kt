package me.danetnaverno.editoni

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.minecraft.MinecraftDictionaryFiller
import me.danetnaverno.editoni.texture.TextureDictionary
import me.danetnaverno.editoni.texture.TextureProvider
import java.nio.file.Paths

object Main
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.main(args) {

            TextureDictionary.textureProvider = TextureProvider
            MinecraftDictionaryFiller.init()

            val world = Editor.loadWorld(Paths.get("data/1.14.2 world"))
            Editor.currentWorld = world
        };
    }
}