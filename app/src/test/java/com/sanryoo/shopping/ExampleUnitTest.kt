package com.sanryoo.shopping

import okio.IOException
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertThrows(IOException::class.java) {
            throw IOException()
        }
    }

}