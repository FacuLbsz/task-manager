package faka.cs.application.service.impl

import faka.cs.application.model.Process
import faka.cs.application.service.TaskManager

class DefaultTaskManager(private val totalCapacity: Int) : TaskManager() {
    override val processes = mutableListOf<Process>()

    override fun addProcess(process: Process) {
        if (processes.size == totalCapacity) {
            throw TaskManagerCapacityReachedException()
        } else {
            processes.add(process)
        }
    }
}

class TaskManagerCapacityReachedException : Exception()
