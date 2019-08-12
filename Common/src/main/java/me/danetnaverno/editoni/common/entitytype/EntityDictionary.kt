package me.danetnaverno.editoni.common.entitytype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.EntityRendererDictionary
import me.danetnaverno.editoni.util.JsonUtil
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import java.nio.file.Files

object EntityDictionary
{
    private val logger = LogManager.getLogger("EntityDictionary")
    private val entityTypes = mutableMapOf<ResourceLocation, EntityType>()

    init
    {
        for (domainFolder in Files.list(ResourceUtil.getBuiltInResourcePath("/assets/entities/")))
        {
            if (Files.isDirectory(domainFolder))
            {
                for (file in Files.list(domainFolder))
                {
                    val resource = ResourceLocation(domainFolder.fileName.toString().removeSuffix("/"), file.fileName.toString().substringBeforeLast("."))
                    val json = JsonUtil.fromFile(file)
                    val renderer = EntityRendererDictionary.create(json.getJSONObject("renderer"))
                    entityTypes[resource] = EntityType(resource, renderer)
                }
            }
        }
    }

    fun getEntityTypes(): Map<ResourceLocation, EntityType>
    {
        return entityTypes.toMap()
    }

    @JvmStatic
    fun getEntityType(id: ResourceLocation): EntityType
    {
        var type = entityTypes[id]
        if (type == null)
        {
            type = EntityTypeMissing(id)
            entityTypes[id] = type
            logger.warn("Unregistered block type found: $id")
        }
        return type
    }

    @JvmStatic
    fun register(id: ResourceLocation, entityType: EntityType)
    {
        entityTypes[id] = entityType
    }
}