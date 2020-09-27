package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.render.WorldRenderer

object Settings
{
    val chunkCleanupPeriod = 5L
    var renderDistance = 10
        set(value)
        {
            require(value > 0)
            field = value
            chunkLoadDistance = value + 2
        }

    /**
     * We load 3 extra chunks around the render distance, in order to allow the outer ring of rendering chunks to check
     *   their surface visibility depending on blocks in neighboring chunks, including ones outside of the Render Distance.
     * todo can be cut down to 2 when we implement proper loading and rendering queue in [WorldRenderer]
     */
    var chunkLoadDistance = renderDistance + 3
        set(value)
        {
            require(value > 2)
            field = value
            renderDistance = value - 2
        }
    var hideObservingOperations = false
}