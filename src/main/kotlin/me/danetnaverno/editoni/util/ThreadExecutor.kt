package me.danetnaverno.editoni.util

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * todo I'm really not sure about this thing. It opens endless possibilities for multiple types of pasta
 */
class ThreadExecutor
{
    private val tasks = ConcurrentLinkedQueue<Runnable>()

    fun addTask(task: Runnable)
    {
        tasks.add(task)
    }

    fun fireTasks()
    {
        while (true)
        {
            val task = tasks.poll() ?: return
            task.run()
        }
    }
}