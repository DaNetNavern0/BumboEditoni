package me.danetnaverno.editoni.common.block

object BlockDictionary
{
    private val blockTypes = mutableMapOf<String, BlockType>()

    init
    {
        blockTypes["minecraft:air"] = BlockType.AIR
        blockTypes["minecraft:stone"] = BlockType.STONE
    }

    fun getAllBlockTypes(): MutableMap<String, BlockType>
    {
        val map = mutableMapOf<String, BlockType>()
        map.putAll(blockTypes)
        return map
    }

    fun getBlockType(id: String) : BlockType
    {
        var type = blockTypes[id]
        if (type == null)
        {
            type = BlockTypeError(id)
            blockTypes[id] = type
        }
        return type
    }
}