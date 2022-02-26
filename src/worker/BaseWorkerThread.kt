package worker

/**
 * Base Thread Class for this project.
 * Clients can extend from the class to consume all defined APIs in [BaseWorkerThread].
 *
 * [BaseWorkerThread] support error handling and multiple client access
 * based on provided configuration.
 *
 * @param builder [WorkerBuilder] to provide work configuration to the [BaseWorkerThread].
 */
abstract class BaseWorkerThread(
    private val builder: WorkerBuilder
) :
    Thread() {
    /*Store the thread name or use current class name*/
    private val tag = builder.threadName ?: (this::class.java.canonicalName ?: "BaseWorkerThread")

    override fun run() {
        startWork()
    }

    /**
     * Stat the execution of the given #builder.getBody. Check if #builder.catchException is applicable,
     * if applicable [executeBody] under [executeBodyOrReturnNull] API else [executeBody].
     */
    private fun startWork() {
        if (builder.catchException) executeBodyOrReturnNull { executeBody() } else executeBody()
    }

    /**
     * Execute the given body based on Configuration #builder.enableSynchronizedAccess.
     * If #builder.enableSynchronizedAccess is applicable, disable multiple access from multiple clients.
     * Execute the given body one by one else allow multiple access.
     */
    private fun executeBody() {
        if (builder.enableSynchronizedAccess) synchronized(tag) { invokeBody(builder.getBody) } else invokeBody(builder.getBody)
    }

    /**
     * Invoke the given body under this inline function
     * including a simple log.
     *
     * @param body Body that would be executed.
     */
    private inline fun invokeBody(crossinline body: BaseWorkerThread.() -> Unit) {
        logThis(tag, "Current Thread is : ${currentThread()}")
        body.invoke(this)
    }

    /**
     * Print a new log.
     *
     * @param tag Class name.
     * @param msg Message that would be printed.
     */
    private fun logThis(tag: String, msg: String) {
        println("$tag - $msg")
    }

    /**
     * A generic API to execute a given body and return nullable object
     * from the executed body. Also, this API catch any kind of [Exception]
     * during the execution time.
     *
     * Notify regarding the [Exception] through #builder.errorCallback.
     *
     * @param body Body that will be executed and return nullable object [T] from the body.
     */
    private inline fun <T> executeBodyOrReturnNull(crossinline body: () -> T): T? {
        return try {
            body.invoke()
        } catch (e: Exception) {
            builder.errorCallback.invoke(e)
            null
        }
    }
}