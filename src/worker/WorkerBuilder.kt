package worker

/**
 * Builder class to provide configuration to [BaseWorkerThread].
 */
class WorkerBuilder {
    /*Store the body and limit field access to get() only*/
    private var mBody: BaseWorkerThread.() -> Unit = {}
    val getBody: BaseWorkerThread.() -> Unit
        get() = mBody

    /*Store the enableSynchronizedAccess and limit field access to get() only*/
    private var mEnableSynchronizedAccess: Boolean = true
    val enableSynchronizedAccess: Boolean
        get() = mEnableSynchronizedAccess

    /*Store the catchException and limit field access to get() only*/
    private var mCatchException: Boolean = false
    val catchException: Boolean
        get() = mCatchException

    /*Store the errorCallback and limit field access to get() only*/
    private var mErrorCallback: (Exception) -> Unit = {}
    val errorCallback: (Exception) -> Unit
        get() = mErrorCallback

    /*Store the threadName and limit field access to get() only*/
    private var mThreadName: String? = null
    val threadName: String?
        get() = mThreadName

    /**
     * Provide a body to be executed under [BaseWorkerThread].
     *
     * @param body Body that will be executed under [BaseWorkerThread].
     *
     * @return [WorkerBuilder].
     */
    fun body(body: BaseWorkerThread.() -> Unit): WorkerBuilder {
        this.mBody = body
        return this
    }

    /**
     * Enable multiple client access to the thread.
     * If enabled multiple client from multiple thread can't execute the body
     * at the same time, else multiple clients access from multiple thread.
     *
     * @param enableSynchronizedAccess Enable or disable multiple access from multiple clients.
     *
     * @return [WorkerBuilder].
     */
    fun enableSynchronizedAccess(enableSynchronizedAccess: Boolean): WorkerBuilder {
        this.mEnableSynchronizedAccess = enableSynchronizedAccess
        return this
    }

    /**
     * Enable or disable default error handling API.
     * If enabled, [BaseWorkerThread] will catch any kind of [Exception] during
     * the execution of the given body, else [BaseWorkerThread] will throw [Exception].
     *
     * @param catchException Enabled or disable error handling.
     *
     * @return [WorkerBuilder].
     */
    fun catchException(catchException: Boolean): WorkerBuilder {
        this.mCatchException = catchException
        return this
    }

    /**
     * Get notified about the [Exception] occurring the execution of the given body.
     *
     * Note: callback will be invoked if [catchException] is enabled.
     *
     * @param errorCallback Callback to get notified about [Exception] during execution.
     *
     * @return [WorkerBuilder].
     */
    fun errorCallback(errorCallback: (Exception) -> Unit): WorkerBuilder {
        this.mErrorCallback = errorCallback
        return this
    }

    /**
     * Provide the thread name, it can be nullable.
     * If [threadName] is null, [BaseWorkerThread] will use its own tag/thread name for logging.
     *
     * @param threadName Thread name, can be nullable.
     *
     * @return [WorkerBuilder].
     */
    fun threadName(threadName: String): WorkerBuilder {
        if (threadName.isEmpty()) {
            return this
        }
        this.mThreadName = threadName
        return this
    }
}