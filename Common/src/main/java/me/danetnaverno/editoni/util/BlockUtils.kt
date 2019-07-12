package me.danetnaverno.editoni.util

import org.joml.Vector3d
import org.joml.Vector3i

fun globalToLocalPos(globalPos: Vector3i) : Vector3i
{
    val y = globalPos.y
    val x = globalPos.x - (globalPos.x shl 4)
    val z = globalPos.z - (globalPos.z shl 4)
    return Vector3i(x, y, z)
}

fun globalToLocalPos(globalPos: Vector3d) : Vector3d
{
    val y = globalPos.y
    val x = globalPos.x - (globalPos.x.toInt() shl 4)
    val z = globalPos.z - (globalPos.z.toInt() shl 4)
    return Vector3d(x, y, z)
}