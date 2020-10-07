package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.render.WorldRenderer

object Settings
{
    val chunkCleanupPeriod = 10L
    var renderDistance = 10
        set(value)
        {
            require(value > 0)
            field = value
            chunkLoadDistance = value + 3
        }

    /**
     * We load 3 extra chunks around the render distance, in order to allow the outer ring of rendering chunks to check
     *   their surface visibility depending on blocks in neighboring chunks, including ones outside of the Render Distance.
     * todo it's not 100% reliable, and we can be cut down it to 2 when we implement proper loading and rendering queue in [WorldRenderer]
     */
    var chunkLoadDistance = renderDistance + 3
        set(value)
        {
            require(value > 3)
            field = value
            renderDistance = value - 3
        }
    var hideObservingOperations = false
}