package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.render.blockrender.BlockRendererDictionary
import me.danetnaverno.editoni.util.JsonUtil
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

object BlockDictionary
{
    private val logger = LogManager.getLogger("BlockDictionary")
    private val blockTypes : Map<String, BlockType>
    private val unregisteredBlockTypes = ConcurrentHashMap<String, BlockType>()

    init
    {
        val root = ResourceUtil.getBuiltInResourcePath("/assets/blocks")
        blockTypes = Files.list(root).filter { dir -> Files.isDirectory(dir) }.flatMap { dir ->
            Files.list(dir).map { file ->
                val resource = ResourceUtil.toResourceLocation(file, root)
                val json = JsonUtil.fromFile(file)
                val renderer = BlockRendererDictionary.create(json.getJSONObject("renderer"))
                BlockType(resource, renderer, json.getBoolean("opaque") ?: true, json.getBoolean("hidden") ?: false)
            }
        }.collect(Collectors.toMap( { it.id}, { it}))
    }

    fun getAllBlockTypes(): Map<String, BlockType>
    {
        return blockTypes + unregisteredBlockTypes
    }

    //Because _currently_ we receive a string name when we reading the region file, it makes sense
    //  to have blockTypes as a <String, BlockType> map... For now...
    fun getBlockType(id: String): BlockType
    {
        var type = blockTypes[id]
        if (type == null)
        {
            //Yes, tests have shown that this is faster than making the main Map ([blockTypes]) a ConcurrentHashMap
            //And you're unlikely to reach this place anyway once this software is finished,
            //  unless it's a modded world or a world from a newer version of MC
            //Note: this will make it run slightly slower during development, because I can't be bothered to write all vanilla block type configs
            type = unregisteredBlockTypes[id]
            if (type == null)
            {
                type = unregisteredBlockTypes.computeIfAbsent(id) { BlockTypeError(id) }
                logger.warn("Unregistered block type found: $id")
            }
        }
        return type
    }
}