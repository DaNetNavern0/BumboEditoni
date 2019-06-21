package me.danetnaverno.editoni.common.block

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.render.BlockRendererCube
import me.danetnaverno.editoni.engine.texture.Texture
import me.danetnaverno.editoni.engine.texture.TextureDictionary

class BlockTypeError(id: ResourceLocation)
    : BlockType(id, BlockRendererCube(
        TextureDictionary["error"],
        Texture["error"],
        Texture["n"],
        Texture["w"],
        Texture["s"],
        Texture["e"]
))