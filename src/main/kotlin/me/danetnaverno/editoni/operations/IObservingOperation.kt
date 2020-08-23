package me.danetnaverno.editoni.operations

/**
 * We have a setting that makes "observing operations" (like "select") not being considered as an actual operation:
 *   it's not features in the operation list, it doesn't generate a chunk loading ticket etc
 */
interface IObservingOperation