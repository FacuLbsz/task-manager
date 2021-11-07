package faka.cs.application

import faka.cs.application.service.impl.DefaultTaskManager
import faka.cs.application.service.impl.FifoTaskManager
import faka.cs.application.service.impl.PriorityBasedTaskManager

object TaskManagerFactory {
    enum class TaskManagerMode {
        DEFAULT, FIFO, PRIORITY
    }

    fun aTaskManager(capacity: Int, taskManagerMode: TaskManagerMode = TaskManagerMode.DEFAULT) =
        when (taskManagerMode) {
            TaskManagerMode.DEFAULT -> DefaultTaskManager(capacity)
            TaskManagerMode.FIFO -> FifoTaskManager(capacity)
            TaskManagerMode.PRIORITY -> PriorityBasedTaskManager(capacity)
        }
}
