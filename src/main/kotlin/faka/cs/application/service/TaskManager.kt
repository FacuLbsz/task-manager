package faka.cs.application.service

import faka.cs.application.model.Process

/**
 *
 * A few assumptions were taken to develop this code.
 * In a working environment I would try to reduce the ambiguity and assumptions to the minimum with the help of the client/my peers
 * As this is a take-home assignment, and I wouldn't like to take much from your time to clarify these assumptions through email
 * I will go further with these assumptions:
 *
 *  1. A process is removed from the taskManager when killed
 *  2. process.start() is out of scope
 *  3. taskManager.listProcesses() is parametrized to provide a sorted result
 *  4. No thread-safe need
 *  5. process.kill() implementation is out of scope
 *  6. process.pid is always unique and given (not given/assigned by the taskManager)
 *  7. taskManager.addProcess() is void -> if I would need to return a result of addition, I would return an object Result (removing the need of exceptions thrown)
 *
 *
 * TaskManager is not thread safe because:
 * 1. underlying collection is not thread-safe
 * 2. addition of a process while removing another one at the same time would cause an exception as kill/remove operation requires to iterate the collection
 *
 * A first approach to make this thread-safe it would be to make the methods synchronized and make the collection volatile.
 * A 2nd approach would be use Lock instead of synchronized to ensure fairness.
 */
@SuppressWarnings("MaxLineLength") //just for readability
abstract class TaskManager {
    protected abstract val processes: MutableCollection<Process>
    abstract fun addProcess(process: Process)

    enum class SortedBy {
        TIME, PRIORITY, PID
    }

    /**
     * "Sorting them by time of creation (implicitly we can consider it the time in which has been added to the TM), priority or id."
     *
     * For simplicity one option is only allowed, if combination of this option is required, I would build a comparator.
     *
     * ascending sort.
     */
    fun listProcesses(sortedBy: SortedBy = SortedBy.TIME): List<Process> =
        when (sortedBy) {
            SortedBy.TIME -> processes.toList()
            SortedBy.PRIORITY -> processes.sortedBy { it.priority.order }.toList()
            SortedBy.PID -> processes.sortedBy { it.pid }.toList()
        }

    /**
     * .kill/.killGroup/.killAll would require (at most) 1 full iteration through the content of processes collection
     * to identify the processes to kill.
     *
     * I couldn't find a collection that supports the following together:
     *
     *  1. fast get based in either 1 or 2 properties (pid/priority) -> map<string, process> would affect implementation of FifoTaskManager.add as it is not possible to poll() (linkedHashMap)
     *  2. fast removal based on hash
     *      -> set<process> doesn't respect point 1.
     *      -> map<string, process> would require having the pid up front which is not the case of killGroup(priority)
     *
     * -----
     * Iterator over stream -> It is not described how a process is removed from the task manager
     *      I assume that a process is removed when killed.
     *      Using an iterator im able to do both things, kill it and remove it from the underlying collection.
     *
     * If removal is not needed then I would use processes.firstOrNull { it.pid == pid }?.kill()
     */
    fun kill(pid: String) {
        val iterator = processes.iterator()
        while (iterator.hasNext()) {
            val process = iterator.next()
            if (process.pid == pid) {
                process.kill()
                iterator.remove()
                break
            }
        }
    }

    /**
     * Since it is not specified what process.kill() should do, I would assume that it is non-blocking, and I should not consider the result
     * in order to remove the process from the task manager.
     *
     * In case it is blocking, I would trigger them asynchronously and every time that one finishes I would then remove it from the task manager.
     *
     * If removal is not needed then I would use processes.filter { it.priority == priority }.forEach { it.kill() }
     */
    fun killGroup(priority: Process.Priority) {
        processes.filter { it.priority == priority }.forEach { it.kill() }
        val iterator = processes.iterator()
        while (iterator.hasNext()) {
            val process = iterator.next()
            if (process.priority == priority) {
                process.kill()
                iterator.remove()
            }
        }
    }

    /**
     * Same reasoning as .killGroup(...)
     */
    fun killAll() {
        val iterator = processes.iterator()
        while (iterator.hasNext()) {
            val process = iterator.next()
            process.kill()
            iterator.remove()
        }
    }
}
