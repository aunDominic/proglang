package proglang

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConcurrentProgram(
    private val threadBodies: List<Stmt>,
    private val pauses: List<Long>,
) {
    private val lock: Lock = ReentrantLock();
    init {
        if (threadBodies.size != pauses.size) throw IllegalArgumentException();
    }
    fun execute(intialStore: Map<String, Int>): Map<String, Int>{
        val workingStore = mutableMapOf<String,Int>()
        workingStore.putAll(intialStore)
        val n = threadBodies.size
        val runnables: List<ProgramExecutor> = List(n){ProgramExecutor(threadBodies[it], lock, pauses[it], workingStore)}
        val threads: List<Thread> = runnables.map{ Thread(it)}
        threads.map{it.start()}
        threads.map{it.join()}
        return workingStore
    }
}