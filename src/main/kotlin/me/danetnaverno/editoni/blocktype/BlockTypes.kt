package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.render.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.Texture

class BlockTypeError(id: String) : BlockType(id, BlockRendererCube(Texture["common:error"]), true, false)