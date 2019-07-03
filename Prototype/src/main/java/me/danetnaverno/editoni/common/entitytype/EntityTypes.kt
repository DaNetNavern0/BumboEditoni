package me.danetnaverno.editoni.common.entitytype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRendererCube
import me.danetnaverno.editoni.common.blockrender.EntityRendererDefault
import me.danetnaverno.editoni.engine.texture.Texture
import me.danetnaverno.editoni.engine.texture.TextureDictionary

class EntityTypeMissing(id: ResourceLocation)
    : EntityType(id, EntityRendererDefault())