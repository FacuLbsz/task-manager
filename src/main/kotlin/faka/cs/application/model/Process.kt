package faka.cs.application.model

data class Process(val pid: String, val priority: Priority) {
    enum class Priority(val order: Int) {
        //I'm not using .ordinal as I prefer to explicit set the order of priorities
        LOW(0), MEDIUM(1), HIGH(2);
    }

    /**
     * Kill is not implemented as assignment does not require it to.
     * As kills is not implemented and process does not contain status of the process, it is not possible
     * to check if a process is killed when it is being removed from the task manager processes list.
     */
    fun kill() {
        //kills the inner process
    }
}
