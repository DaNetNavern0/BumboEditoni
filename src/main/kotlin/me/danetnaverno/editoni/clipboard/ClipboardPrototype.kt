package me.danetnaverno.editoni.clipboard

import me.danetnaverno.editoni.world.Block
import java.util.*

object ClipboardPrototype
{
    private var clipboard: MutableList<Block> = ArrayList()
    fun set(blocks: MutableList<Block>)
    {
        clipboard = blocks
    }

    fun add(block: Block)
    {
        clipboard.add(block)
    }

    fun add(blocks: Collection<Block>?)
    {
        clipboard.addAll(blocks!!)
    }

    fun get(): List<Block>
    {
        return ArrayList(clipboard)
    }

    fun size(): Int
    {
        return clipboard.size
    }
}