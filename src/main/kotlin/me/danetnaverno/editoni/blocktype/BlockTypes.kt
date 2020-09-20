package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.render.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.TextureDictionary

class BlockTypeError(id: String)
    : BlockType(id, BlockRendererCube(TextureDictionary["common:error"]), true, false)