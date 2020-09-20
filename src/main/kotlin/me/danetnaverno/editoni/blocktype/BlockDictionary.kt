package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.render.blockrender.BlockRendererDictionary
import me.danetnaverno.editoni.util.JsonUtil
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap


object BlockDictionary
{
    private val logger = LogManager.getLogger("BlockDictionary")
    private val blockTypes = hashMapOf<String, BlockType>()
    private val unregisteredBlockTypes = ConcurrentHashMap<String, BlockType>()

    init
    {
        for (domainFolder in Files.list(ResourceUtil.getBuiltInResourcePath("/assets/blocks")))
        {
            if (Files.isDirectory(domainFolder))
            {
                for (file in Files.list(domainFolder))
                {
                    val resource = domainFolder.fileName.toString().removeSuffix("/") +
                            ":" + file.fileName.toString().substringBeforeLast(".")
                    val json = JsonUtil.fromFile(file)
                    val renderer = BlockRendererDictionary.create(json.getJSONObject("renderer"))
                    blockTypes[resource] = BlockType(resource, renderer, json.getBoolean("opaque")
                            ?: true, json.getBoolean("hidden") ?: false)
                }
            }
        }
    }

    fun getAllBlockTypes(): Map<String, BlockType>
    {
        return blockTypes.toMutableMap()
    }

    fun getBlockType(id: String): BlockType
    {
        var type = blockTypes[id]
        if (type == null)
        {
            //Yes, tests have shown that this is faster than making the main Map ([blockTypes]) a ConcurrentHashMap
            //And you're unlikely to reach this place anyway once this software is finished,
            //  unless it's a modded world or a world from a newer version of MC
            //Note: this will make it slightly slower during development, because I can't be bothered to write all vanilla block type configs
            type = unregisteredBlockTypes[id]
            if (type == null)
            {
                type = unregisteredBlockTypes.computeIfAbsent(id) { BlockTypeError(id) }
                logger.warn("Unregistered block type found: $id")
            }
        }
        return type
    }

    fun register(id: String, blockType: BlockType)
    {
        blockTypes[id] = blockType
    }
}