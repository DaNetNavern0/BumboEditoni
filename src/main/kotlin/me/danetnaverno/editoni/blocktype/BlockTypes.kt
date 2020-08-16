package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.render.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.TextureDictionary

class BlockTypeError(id: ResourceLocation)
    : BlockType(id, BlockRendererCube(TextureDictionary[ResourceLocation("common:error")]), true, false)