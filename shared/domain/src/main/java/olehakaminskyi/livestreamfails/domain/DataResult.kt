package olehakaminskyi.livestreamfails.domain

open class DataResult<T> constructor(val data: T? = null, val error: ResultError? = null) {

    val isSuccessful: Boolean
        get() = error == null

    fun success(successFunction: (T) -> Unit): DataResult<T> = apply {
        if (data != null) {
            successFunction(data)
        }
    }

    fun error(errorFunction: (ResultError) -> Unit): DataResult<T> = apply {
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
    object NoData : ErrorType()
}

data class ResultError(val type: ErrorType, val cause: Throwable? = null)