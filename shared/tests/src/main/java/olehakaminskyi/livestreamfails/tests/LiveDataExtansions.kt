package olehakaminskyi.livestreamfails.tests

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.junit.Assert

fun <T> LiveData<T>.assertThat(condition: (T) -> Boolean) {
    TestObserver<T> { Assert.assertTrue(condition(it)) }
        .also {
            observeForever(it)
            Assert.assertTrue(it.invoked)
            removeObserver(it)
        }
}

fun <T> LiveData<T>.assertValue(value: T) {
    TestObserver<T> { Assert.assertEquals(value, it) }
        .also {
            observeForever(it)
            Assert.assertTrue(it.invoked)
            removeObserver(it)
        }
}

fun <T> LiveData<T>.assertEmpty() {
    TestObserver<T> { }
        .also {
            observeForever(it)
            Assert.assertFalse(it.invoked)
            removeObserver(it)
        }
}

fun <T> LiveData<T>.assertNotEmpty() {
    TestObserver<T> { }
        .also {
            observeForever(it)
            Assert.assertTrue(it.invoked)
            removeObserver(it)
        }
}

fun <T> LiveData<T>.valuesVerifier(vararg values: T) = Verifier(values = *values, liveData = this)

class Verifier<T> (vararg values: T, private val liveData: LiveData<T>) {
    private val _values = values
    private val _emitted = mutableListOf<T>()
    private val _observer = Observer<T> { _emitted.add(it) }.also { liveData.observeForever(it) }

    fun verify() {
        liveData.removeObserver(_observer)
        Assert.assertEquals(_values.size, _emitted.size)
        (0.._values.lastIndex).forEach { Assert.assertEquals(_emitted[it], _values[it]) }
    }
}

internal class TestObserver<T>(val onChangedFunc: (T) -> Unit) : Observer<T> {
    var invoked = false
    override fun onChanged(t: T) {
        onChangedFunc(t)
        invoked = true
    }
}
