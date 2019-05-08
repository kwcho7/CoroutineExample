package com.cools.coroutine.cancellation

import kotlinx.coroutines.*
import org.junit.Test

class ScopeJobCancel {

    /**
     * Job 을 취소할 때 완료 상태로 되기 때문에 이미 완료된 범위에 대해서는 코루틴은 재실행 되지 않는다.
     * doWork1 > doWork2 > end...
     * doWork1 을 다시 호출하지만 실행되지 않는다.
     *
     * 이를 해결하기 위해서는 cancelChildren()을 사용해야한다.
     */
    @Test
    fun workManagerCancel() = runBlocking {
        val workManager = WorkManager()

        workManager.doWork1()
        workManager.doWork2()

        workManager.cancelAllWork()

        workManager.doWork1()

        delay(1000)
    }

    @Test
    fun workManagerCancelChildern() = runBlocking {
        val workManager = WorkManager()

        workManager.doWork1()
        workManager.doWork2()

        workManager.cancelAllChildern()

        workManager.doWork1()

        delay(1000)
    }


    class WorkManager{
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.Default + job)

        fun doWork1() {
            scope.launch {
                println("doWork1")
            }
        }

        fun doWork2() {
            scope.launch {
                println("doWork2")
            }
        }
        fun cancelAllChildern() {
            scope.coroutineContext.cancelChildren()
        }

        fun cancelAllWork() {
            job.cancel()
        }
    }
}