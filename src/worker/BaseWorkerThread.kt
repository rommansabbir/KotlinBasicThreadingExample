package worker

/**
 * Base Thread Class for this project.
 * Clients can extend from the class to consume all defined APIs in [BaseWorkerThread].
 *
 * [BaseWorkerThread] support error handling and multiple client access
 * based on provided configuration.
 *
 * @param body Body that will be executed.
 * @param enableSynchronized Enable multiple access or not.
 * @param catchException Handle [Exception] by default or not.
 * @param errorCallback Callback to get notified about [Exception] if [catchException] is enabled.
 * @param threadName Thread name for logging purpose.
 */
abstract class BaseWorkerThread(
    private val body: BaseWorkerThread.() -> Unit,
    private val enableSynchronized: Boolean = true,
    private val catchException: Boolean = true,
    private var errorCallback: (Exception) -> Unit = {},
    private val threadName: String? = null
) :
    Thread() {
    /*Store the thread name or use current class name*/
    private val tag = threadName ?: (this::class.java.canonicalName ?: "BaseWorkerThread")

    override fun run() {
        startWork()
    }

    /**
     * Stat the execution of the given [body]. Check if [catchException] is applicable,
     * if applicable [executeBody] under [executeBodyOrReturnNull] API else [executeBody].
     */
    private fun startWork() {
        if (catchException) executeBodyOrReturnNull { executeBody() } else executeBody()
    }

    /**
     * Execute the given body based on Configuration [enableSynchronized].
     * If [enableSynchronized] is applicable, disable multiple access from multiple clients.
     * Execute the given body one by one else allow multiple access.
     */
    private fun executeBody() {
        if (enableSynchronized) synchronized(tag) { invokeBody(body) } else invokeBody(body)
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
     * Notify regarding the [Exception] through [errorCallback].
     *
     * @param body Body that will be executed and return nullable object [T] from the body.
     */
    private inline fun <T> executeBodyOrReturnNull(crossinline body: () -> T): T? {
        return try {
            body.invoke()
        } catch (e: Exception) {
            errorCallback.invoke(e)
            null
        }
    }
}