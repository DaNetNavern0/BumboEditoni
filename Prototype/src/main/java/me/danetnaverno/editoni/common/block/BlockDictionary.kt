package me.danetnaverno.editoni.common.block

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.engine.render.BlockRendererDictionary
import me.danetnaverno.editoni.util.JsonUtil
import org.apache.logging.log4j.LogManager
import java.io.File

object BlockDictionary
{
    private val logger = LogManager.getLogger("BlockDictionary")
    private val blockTypes = mutableMapOf<ResourceLocation, BlockType>()

    init
    {
        for (domainFolder in File("data/blocks").listFiles())
        {
            if (domainFolder.isDirectory)
            {
                for (file in domainFolder.listFiles())
                {
                    val resource = ResourceLocation(domainFolder.name, file.nameWithoutExtension)
                    val json = JsonUtil.fromFile(file.toPath())
                    val renderer = BlockRendererDictionary.create(json.getJSONObject("renderer"))
                    blockTypes[resource] = BlockType(resource, renderer)
                }
            }
        }
        //Yes it's kinda hacky, but it's more convenient to have BlockType.AIR rather than BlockDictionary.AIR
        blockTypes[ResourceLocation("minecraft","air")] = BlockType.AIR
    }

    fun getAllBlockTypes(): MutableMap<ResourceLocation, BlockType>
    {
        val map = mutableMapOf<ResourceLocation, BlockType>()
        map.putAll(blockTypes)
        return map
    }

    @JvmStatic
    fun getBlockType(id: ResourceLocation): BlockType
    {
        var type = blockTypes[id]
        if (type == null)
        {
            type = BlockTypeError(id)
            blockTypes[id] = type
            logger.warn("Unregistered block type found: $id")
        }
        return type
    }
}