package me.danetnaverno.editoni.util

import me.danetnaverno.editoni.common.blockrender.BlockRendererAir
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.editor.Editor

fun Block.isInvisible(): Boolean
{
    return this.type.renderer is BlockRendererAir || Editor.getHiddenBlocks().contains(this)
}