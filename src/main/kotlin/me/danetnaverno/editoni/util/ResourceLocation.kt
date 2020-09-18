package me.danetnaverno.editoni.util

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