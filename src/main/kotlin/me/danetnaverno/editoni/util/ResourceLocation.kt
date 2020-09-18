package me.danetnaverno.editoni.util

//todo inlining is still not enough - gonna just use regular strings instead :(
inline class ResourceLocation(val name: String)
{
    companion object
    {
        fun fromTwo(domain: String, path: String): ResourceLocation
        {
            return ResourceLocation("$domain:$path")
        }
    }
}