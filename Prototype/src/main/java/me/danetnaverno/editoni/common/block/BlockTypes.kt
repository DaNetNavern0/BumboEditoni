package me.danetnaverno.editoni.common.block

import me.danetnaverno.editoni.engine.render.BlockRendererCube
import me.danetnaverno.editoni.engine.texture.Texture
import me.danetnaverno.editoni.engine.texture.TextureDictionary

class BlockTypeError(id: String)
    : BlockType(id, BlockRendererCube(
        TextureDictionary["error"],
        Texture["error"],
        Texture["blocks/n"],
        Texture["blocks/w"],
        Texture["blocks/s"],
        Texture["blocks/e"]
))