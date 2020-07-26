package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.blockrender.BlockRendererDictionary
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.blocktype.BlockTypeError
import me.danetnaverno.editoni.util.JsonUtil
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import java.nio.file.Files

object BlockDictionary
{
    private val logger = LogManager.getLogger("BlockDictionary")
    private val blockTypes = mutableMapOf<ResourceLocation, BlockType>()

    init
    {
        for (domainFolder in Files.list(ResourceUtil.getBuiltInResourcePath("/assets/blocks")))
        {
            if (Files.isDirectory(domainFolder))
            {
                for (file in Files.list(domainFolder))
                {
                    val resource = ResourceLocation(domainFolder.fileName.toString().removeSuffix("/"),
                            file.fileName.toString().substringBeforeLast("."))
                    val json = JsonUtil.fromFile(file)
                    val renderer = BlockRendererDictionary.create(json.getJSONObject("renderer"))
                    blockTypes[resource] = BlockType(resource, renderer
                            , json.getBoolean("opaque") ?: true
                            , json.getBoolean("hidden") ?: false)
                }
            }
        }
    }

    fun getAllBlockTypes(): Map<ResourceLocation, BlockType>
    {
        return blockTypes.toMutableMap()
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

    @JvmStatic
    fun register(id: ResourceLocation, blockType: BlockType)
    {
        blockTypes[id] = blockType
    }
}