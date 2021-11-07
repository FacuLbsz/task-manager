package faka.cs.application.service.impl

import faka.cs.application.model.Process
import faka.cs.application.service.TaskManager

class PriorityBasedTaskManager(private val totalCapacity: Int) : TaskManager() {
    override val processes = LinkedHashSet<Process>()

    override fun addProcess(process: Process) {
        if (processes.size == totalCapacity) {
            processes.findLowestAndOldestProcess(process)
                ?.let {
                    it.kill()
                    processes.remove(it)
                }?.also {
                    processes.add(process)
                }
        } else {
            processes.add(process)
        }
    }

    private fun LinkedHashSet<Process>.findLowestAndOldestProcess(process: Process) =
        filter { it.priority.order < process.priority.order }
            .minByOrNull { it.priority.order }
}
