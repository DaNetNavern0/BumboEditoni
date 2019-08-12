package me.danetnaverno.editoni.common.entitytype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.EntityRendererDictionary
import me.danetnaverno.editoni.util.JsonUtil
import org.apache.logging.log4j.LogManager
import java.io.File

object EntityDictionary
{
    private val logger = LogManager.getLogger("EntityDictionary")
    private val entityTypes = mutableMapOf<ResourceLocation, EntityType>()

    init
    {
        for (domainFolder in File("data/entities").listFiles())
        {
            if (domainFolder.isDirectory)
            {
                for (file in domainFolder.listFiles())
                {
                    val resource = ResourceLocation(domainFolder.name, file.nameWithoutExtension)
                    val json = JsonUtil.fromFile(file.toPath())
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