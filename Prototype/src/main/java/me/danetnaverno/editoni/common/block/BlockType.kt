package me.danetnaverno.editoni.common.block

import me.danetnaverno.editoni.common.render.BlockRenderer
import me.danetnaverno.editoni.common.render.BlockRendererCube
import me.danetnaverno.editoni.common.texture.Texture
import java.nio.file.Paths

open class BlockType(val id: String, val renderer: BlockRenderer)
{
}

class BlockTypeError(id: String)
    : BlockType(id, BlockRendererCube(
        Texture(Paths.get("data/textures/blocks/error.png")),
        Texture(Paths.get("data/textures/blocks/error.png")),
        Texture(Paths.get("data/textures/blocks/n.png")),
        Texture(Paths.get("data/textures/blocks/w.png")),
        Texture(Paths.get("data/textures/blocks/s.png")),
        Texture(Paths.get("data/textures/blocks/e.png"))
        ))