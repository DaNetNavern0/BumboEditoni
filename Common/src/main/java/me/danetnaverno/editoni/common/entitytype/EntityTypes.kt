package me.danetnaverno.editoni.common.entitytype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.EntityRendererDefault

class EntityTypeMissing(id: ResourceLocation) : EntityType(id, EntityRendererDefault())