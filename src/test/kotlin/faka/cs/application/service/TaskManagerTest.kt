package faka.cs.application.service

import faka.cs.application.TaskManagerFactory
import faka.cs.application.TaskManagerFactory.TaskManagerMode.FIFO
import faka.cs.application.TaskManagerFactory.TaskManagerMode.PRIORITY
import faka.cs.application.service.impl.TaskManagerCapacityReachedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import faka.cs.application.model.Process

class TaskManagerTest {

    @Test
    fun `a task manager lists the processes sorting by pid`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process1 = Process("1", Process.Priority.LOW)
        val process2 = Process("2", Process.Priority.LOW)

        taskManager.addProcess(process2)
        taskManager.addProcess(process1)

        //when/then
        assertEquals(
            listOf(
                process1,
                process2
            ),
            taskManager.listProcesses(TaskManager.SortedBy.PID)
        )
    }

    @Test
    fun `a task manager lists the processes sorting by priority`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process1 = Process("1", Process.Priority.MEDIUM)
        val process2 = Process("2", Process.Priority.LOW)

        taskManager.addProcess(process1)
        taskManager.addProcess(process2)

        //when/then
        assertEquals(
            listOf(
                process1,
                process2
            ),
            taskManager.listProcesses(TaskManager.SortedBy.PID)
        )
    }

    @Test
    fun `a task manager lists the processes sorting by creation time`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process2 = Process("2", Process.Priority.MEDIUM)
        val process1 = Process("1", Process.Priority.LOW)

        taskManager.addProcess(process2)
        taskManager.addProcess(process1)

        //when/then
        assertEquals(
            listOf(
                process2,
                process1
            ),
            taskManager.listProcesses(TaskManager.SortedBy.TIME)
        )
    }

    @Test
    fun `a default task manager only accepts a total capacity of processes`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process1 = Process("1", Process.Priority.LOW)
        val process2 = Process("2", Process.Priority.LOW)

        taskManager.addProcess(process1)
        taskManager.addProcess(process2)

        //when/then
        assertThrows<TaskManagerCapacityReachedException> {
            taskManager.addProcess(Process("3", Process.Priority.LOW))
        }
        assertEquals(
            listOf(
                process1,
                process2
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a fifo task manager accepts more processes than its total capacity removing the oldest one`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2, FIFO)

        val oldestProcess = Process("1", Process.Priority.LOW)
        val notThatOldProcess = Process("2", Process.Priority.LOW)
        val newestProcess = Process("3", Process.Priority.LOW)

        taskManager.addProcess(oldestProcess)
        taskManager.addProcess(notThatOldProcess)

        //when
        taskManager.addProcess(newestProcess)

        //then
        assertEquals(
            listOf(
                notThatOldProcess,
                newestProcess
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a priority tm accepts more processes than its total capacity removing the lowest priority and oldest one`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2, PRIORITY)

        val oldestLowProcess = Process("1", Process.Priority.LOW)
        val notThatOldLowProcess = Process("2", Process.Priority.LOW)
        val newestMediumProcess = Process("3", Process.Priority.MEDIUM)

        taskManager.addProcess(oldestLowProcess)
        taskManager.addProcess(notThatOldLowProcess)

        //when
        taskManager.addProcess(newestMediumProcess)

        //then
        assertEquals(
            listOf(
                notThatOldLowProcess,
                newestMediumProcess
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a priority task manager ignores more processes than its total capacity if a lowest priority is not found`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2, PRIORITY)

        val oldestMediumProcess = Process("1", Process.Priority.MEDIUM)
        val notThatOldMediumProcess = Process("2", Process.Priority.MEDIUM)
        val newestMediumProcess = Process("3", Process.Priority.MEDIUM)

        taskManager.addProcess(oldestMediumProcess)
        taskManager.addProcess(notThatOldMediumProcess)

        //when
        taskManager.addProcess(newestMediumProcess)

        //then
        assertEquals(
            listOf(
                oldestMediumProcess,
                notThatOldMediumProcess
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a task manager kills a process by pid successfully`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process1 = Process("1", Process.Priority.LOW)
        val process2 = Process("2", Process.Priority.LOW)

        taskManager.addProcess(process1)
        taskManager.addProcess(process2)

        //when
        taskManager.kill(process1.pid)

        //then
        assertEquals(
            listOf(
                process2
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a task manager kills many processes by group successfully`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(3)

        val process1 = Process("1", Process.Priority.LOW)
        val process2 = Process("2", Process.Priority.LOW)
        val process3 = Process("2", Process.Priority.MEDIUM)

        taskManager.addProcess(process1)
        taskManager.addProcess(process2)
        taskManager.addProcess(process3)

        //when
        taskManager.killGroup(Process.Priority.LOW)

        //then
        assertEquals(
            listOf(
                process3
            ),
            taskManager.listProcesses()
        )
    }

    @Test
    fun `a task manager kills all the processes successfully`() {
        //given
        val taskManager = TaskManagerFactory.aTaskManager(2)

        val process1 = Process("1", Process.Priority.LOW)
        val process2 = Process("2", Process.Priority.LOW)

        taskManager.addProcess(process1)
        taskManager.addProcess(process2)

        //when
        taskManager.killAll()

        //then
        assertEquals(
            0,
            taskManager.listProcesses().size
        )
    }
}





