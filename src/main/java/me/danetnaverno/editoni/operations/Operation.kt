package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.location.ChunkArea


abstract class Operation
{
    lateinit var operationList: OperationList
        internal set
    abstract val displayName: String
    abstract val alteredChunks: ChunkArea?

    /**
     * This method is used to apply the operation for the first time.
     * A good example of why there has to be a distinction can be seen at [SetBlocksOperation]
     */
    abstract fun initialApply()

    /**
     * This method is used to reapply the operation when scrolling through the operation history.
     * When the user jump several operations, each of them gets reapplied or rolled back sequentially.
     */
    abstract fun reapply()

    /**
     * This method is used to rollback the operation when scrolling through the operation history.
     * When the user jump several operations, each of them gets reapplied or rolled back sequentially.
     */
    abstract fun rollback()
}
