package me.danetnaverno.editoni.util

import java.util.concurrent.ConcurrentLinkedQueue

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