package mq

import java.lang.Exception
import java.nio.ByteBuffer
import java.security.SecureRandom


//Generate an invertible Affine function.
class Affine(var m: Int, var k: Int, var seed: Int, val verbosity: Boolean = false) {
    private val n:Int
    private var coroutineCount = 0


    init {
        val tmpSeed = seed
        seed = SecureRandom(ByteBuffer.allocate(Integer.SIZE/8).putInt(tmpSeed).array()).nextInt(tmpSeed)
        n = m
    }

    fun generate(): HashMap<String, Array<Array<Int>>> {
        var iterations = 0
        var l: Array<Array<Int>>
        var lInverse: Array<Array<Int>>
        while (true) {
            l =  Array(m){Array(n) {0}}
            for (i:Int in 0 until m) {
                for (j:Int in 0 until n) {
                    l[i][j] = GF256.get()
                }
            }

            try {
                lInverse = GF256.findInverse(l)
                break

            } catch (e:Exception) {
                iterations++
                if (iterations % 100000 == 0) {
                    println("$iterations done")
                }
            }
        }

        val b = Array(m) {0}
        for (i:Int in 0 until m)
            b[i] = GF256.get()
        val bb = Array(1){b}
        return hashMapOf(
            "l" to l,
            "lInverse" to lInverse,
            "b" to bb
        )
    }
}

