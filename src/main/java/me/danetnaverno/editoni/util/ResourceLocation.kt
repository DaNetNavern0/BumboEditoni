package me.danetnaverno.editoni.common

class ResourceLocation : Comparable<ResourceLocation>
{
    val domain: String
    val path: String

    constructor(string: String)
    {
        val lower = string.toLowerCase()
        val index = lower.indexOf(':')
        require(index != -1) { "Invalid ResourceLocation string: $string" }
        this.domain = lower.substring(0, index)
        this.path = lower.substring(index + 1)
    }

    constructor(domain: String, path: String)
    {
        //In pure Kotlin you can't pass nulls into domain or path because they're non-nullable,
        //but since it's a hybrid project, that can happen
        this.domain = domain.toLowerCase()
        this.path = path.toLowerCase()
    }

    fun toDottedString(): String
    {
        return "$domain.$path"
    }

    override fun toString(): String
    {
        return "$domain:$path"
    }

    override fun equals(other: Any?): Boolean
    {
        return other is ResourceLocation && other.toString() == toString()
    }

    override fun hashCode(): Int
    {
        return toString().hashCode()
    }

    override fun compareTo(other: ResourceLocation): Int
    {
        return this.toString().compareTo(other.toString())
    }
}