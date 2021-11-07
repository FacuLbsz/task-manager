package faka.cs.application.service.impl

import faka.cs.application.model.Process
import faka.cs.application.service.TaskManager
import java.util.LinkedList

class FifoTaskManager(private val totalCapacity: Int) : TaskManager() {
    override val processes = LinkedList<Process>()

    override fun addProcess(process: Process) {
        if (processes.size == totalCapacity) {
            processes.poll().kill()
        }
        processes.add(process)
    }
}
