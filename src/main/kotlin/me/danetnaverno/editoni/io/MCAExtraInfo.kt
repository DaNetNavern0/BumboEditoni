package me.danetnaverno.editoni.io

import net.querz.nbt.tag.CompoundTag

/**
 * This class is here to keep the unhandled NBT data from chunks.
 *
 *
 * May or may not be gone in a future in case if all chunk data will be handled.
 */
open class MCAExtraInfo(var data: CompoundTag)