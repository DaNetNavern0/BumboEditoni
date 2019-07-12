package me.danetnaverno.editoni.common.blocktype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.TextureDictionary

class BlockTypeError(id: ResourceLocation)
    : BlockType(id, BlockRendererCube(TextureDictionary[ResourceLocation("common:error")]))