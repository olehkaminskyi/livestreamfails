package olehakaminskyi.livestreamfails.domain

open class DataResult<T> private constructor(val data: T? = null, val error: ResultError?) {

    constructor(value: T) : this(value, null)
    constructor(error: ResultError) : this(null, error)

    val isSuccessful: Boolean
        get() = error == null

    fun success(successFunction: (T) -> Unit): DataResult<T> = this.apply {
        if (data != null) {
            successFunction(data)
        }
    }

    fun error(errorFunction: (ResultError) -> Unit): DataResult<T> = this.apply {
        if (error != null) {
            errorFunction(error)
        }
    }

    fun <R> map(mappingFunction: (T) -> R): DataResult<R> =
        DataResult(if (data != null) mappingFunction(data) else null, error)
}

sealed class ErrorType {
    object NoConnection : ErrorType()
    object Unknown : ErrorType()
}

data class ResultError(val error: ErrorType, val cause: Throwable? = null)