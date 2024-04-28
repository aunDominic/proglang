package proglang

import java.util.concurrent.locks.Lock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class ProgramExecutor(
    private val threadBody: Stmt,
    private val lock: Lock,
    private val pause: Long,
    private val store: MutableMap<String, Int>
    ): Runnable {
    override fun run() {
        Thread.sleep(pause)
        var curr: Stmt? = threadBody
        while (curr!= null){
            lock.withLock {
                curr = curr!!.step(store)
            }
        }

    }
}