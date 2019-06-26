package mq

import java.security.SecureRandom


class GF256 {

    companion object {
        var mask = 0xff
        /*
        * Obs: respecto a los arreglos exponents y logarithms, i = logarithms[exponents[i]], pero si i es
        *      distinto de una potencia de dos menor a 256, no se cual es la relacion de los numeros.
        * */
        private val exponents = intArrayOf(
            1, 2, 4, 8, 16, 32, 64, 128, 77, 154, 121, 242,
            169, 31, 62, 124, 248, 189, 55, 110, 220, 245, 167, 3, 6, 12, 24,
            48, 96, 192, 205, 215, 227, 139, 91, 182, 33, 66, 132, 69, 138, 89,
            178, 41, 82, 164, 5, 10, 20, 40, 80, 160, 13, 26, 52, 104, 208,
            237, 151, 99, 198, 193, 207, 211, 235, 155, 123, 246, 161, 15, 30,
            60, 120, 240, 173, 23, 46, 92, 184, 61, 122, 244, 165, 7, 14, 28,
            56, 112, 224, 141, 87, 174, 17, 34, 68, 136, 93, 186, 57, 114, 228,
            133, 71, 142, 81, 162, 9, 18, 36, 72, 144, 109, 218, 249, 191, 51,
            102, 204, 213, 231, 131, 75, 150, 97, 194, 201, 223, 243, 171, 27,
            54, 108, 216, 253, 183, 35, 70, 140, 85, 170, 25, 50, 100, 200,
            221, 247, 163, 11, 22, 44, 88, 176, 45, 90, 180, 37, 74, 148, 101,
            202, 217, 255, 179, 43, 86, 172, 21, 42, 84, 168, 29, 58, 116, 232,
            157, 119, 238, 145, 111, 222, 241, 175, 19, 38, 76, 152, 125, 250,
            185, 63, 126, 252, 181, 39, 78, 156, 117, 234, 153, 127, 254, 177,
            47, 94, 188, 53, 106, 212, 229, 135, 67, 134, 65, 130, 73, 146,
            105, 210, 233, 159, 115, 230, 129, 79, 158, 113, 226, 137, 95, 190,
            49, 98, 196, 197, 199, 195, 203, 219, 251, 187, 59, 118, 236, 149,
            103, 206, 209, 239, 147, 107, 214, 225, 143, 83, 166, 1
        )

        /*
        * Obs: logaritmos es un arreglo del logaritmo en base dos de cada elemento en
        *      el espacio de 256, hay que fijarce en las posiciones potencias de 2
        * */
        private val logarithms = intArrayOf(
            0, 0, 1, 23, 2, 46, 24, 83, 3, 106, 47, 147,
            25, 52, 84, 69, 4, 92, 107, 182, 48, 166, 148, 75, 26, 140, 53,
            129, 85, 170, 70, 13, 5, 36, 93, 135, 108, 155, 183, 193, 49, 43,
            167, 163, 149, 152, 76, 202, 27, 230, 141, 115, 54, 205, 130, 18,
            86, 98, 171, 240, 71, 79, 14, 189, 6, 212, 37, 210, 94, 39, 136,
            102, 109, 214, 156, 121, 184, 8, 194, 223, 50, 104, 44, 253, 168,
            138, 164, 90, 150, 41, 153, 34, 77, 96, 203, 228, 28, 123, 231, 59,
            142, 158, 116, 244, 55, 216, 206, 249, 131, 111, 19, 178, 87, 225,
            99, 220, 172, 196, 241, 175, 72, 10, 80, 66, 15, 186, 190, 199, 7,
            222, 213, 120, 38, 101, 211, 209, 95, 227, 40, 33, 137, 89, 103,
            252, 110, 177, 215, 248, 157, 243, 122, 58, 185, 198, 9, 65, 195,
            174, 224, 219, 51, 68, 105, 146, 45, 82, 254, 22, 169, 12, 139,
            128, 165, 74, 91, 181, 151, 201, 42, 162, 154, 192, 35, 134, 78,
            188, 97, 239, 204, 17, 229, 114, 29, 61, 124, 235, 232, 233, 60,
            234, 143, 125, 159, 236, 117, 30, 245, 62, 56, 246, 217, 63, 207,
            118, 250, 31, 132, 160, 112, 237, 20, 144, 179, 126, 88, 251, 226,
            32, 100, 208, 221, 119, 173, 218, 197, 64, 242, 57, 176, 247, 73,
            180, 11, 127, 81, 21, 67, 145, 16, 113, 187, 238, 191, 133, 200, 161
        )

        @JvmStatic
        fun get(): Int {
            return exponents[SecureRandom().nextInt(exponents.size)]
        }

        // Add two numbers in the finite field.
        @JvmStatic
        fun add(x: Int, y: Int): Int {
            if (x !in 0..255 || y !in 0..255) {
                throw Exception("Add Error : Values must be within finite field 256! x = $x, y = $y")
            }
            return x.xor(y)
        }

        // Substract two numbers in the finite field.
        @JvmStatic
        fun subtract(x: Int, y: Int): Int {
            if (x !in 0..255 || y !in 0..255) {
                throw Exception("Subtract Error : Values must be within finite field 256! x = $x, y = $y")
            }
            return x.xor(y)
        }

        // Multiply two numbers in the finite field.
        @JvmStatic
        fun multiply(x: Int, y: Int): Int {
            if (x !in 0..255 || y !in 0..255) {
                throw Exception("Multiply Error : Values must be within finite field 256! x = $x, y = $y")
            }
            if (x == 0 || y == 0)
                return 0
            else
                // Suponiendo que de verdad es un espacio finito en 256, esto deberia ser modulo 256??
                return exponents[(logarithms[x] + logarithms[y]) % 255]

        }

        // Find the inverse of a number in the finite field
        @JvmStatic
        fun getInverse(x: Int): Int {
            if (x !in 0..255) {
                throw Exception("Get Inverse Error : Values must be within finite field 256! x = $x")
            }
            if (x == 0)
                return 0
            else
                return exponents[255 - logarithms[x]]

        }

        // Change elements below diagonal to 0.
        // This method was created thinking to solve the problem of inverting a matrix or solving a linear equation
        // inverse == true  --> finding inverse of matrix
        // inverse == false --> solving linear equation
        @JvmStatic
        fun lowerZeroMatrix(mat: Array<Array<Int>>, inverse: Boolean): Array<Array<Int>> {
            val n: Int = mat.size
            val m: Int = mat[0].size

            val len: Int

            if(inverse)
                len = 2*n
            else
                len = n + 1

            for (k in 0 until n - 1) {
                for (i in k + 1 until n) {
                    val factor1 = mat[i][k]
                    val factor2 = getInverse(mat[k][k])

                    if (factor2 == 0)
                        throw Exception("Matrix not invertible")

                    for (j in k until len) {
                        var temp = multiply(mat[k][j], factor2)
                        temp = multiply(factor1, temp)
                        mat[i][j] = add(mat[i][j], temp)
                    }
                }
            }

            return mat

        }

        // Change elements below diagonal to 0
        // The matrix have the form n x 2n
        @JvmStatic
        fun upperZeroMatrix(mat: Array<Array<Int>>): Array<Array<Int>> {
            var temp: Int = 0
            val n: Int = mat.size
            val m: Int = mat[0].size

            for (k in n - 1 downTo 1) {
                for (i in k - 1 downTo 0) {
                    val factor1: Int = mat[i][k]
                    val factor2: Int = getInverse(mat[k][k])
                    if (factor2 == 0)
                        throw Exception("Matrix not invertible.")

                    for (j in k until 2 * n) {
                        // mat is a n x 2n matrix
                        temp = multiply(mat[k][j], factor2)
                        temp = multiply(factor1, temp)
                        mat[i][j] = add(mat[i][j], temp)
                    }
                }
            }

            return mat
        }

        @JvmStatic
        fun multiplyMatrices(M1: Array<Array<Int>>, M2: Array<Array<Int>>): Array<Array<Int>> {
            val n1: Int = M1.size
            val m1: Int = M1[0].size
            val n2: Int = M2.size
            val m2: Int = M2[0].size

            if (m1 != n2)
                throw Exception("Matrices need to have same dimensions: m1 = $m1, n2 = $n2")

            val ret: Array<Array<Int>> = Array(n1) { Array(m2) { 0 } }
            for (i in 0 until n1) {
                for (j in 0 until n2) {
                    for (k in 0 until m2) {
                        val temp: Int = multiply(M1[i][j], M2[j][k])
                        ret[i][k] = add(ret[i][k], temp)
                    }
                }
            }

            return ret

        }

        @JvmStatic
        fun findInverse(mat: Array<Array<Int>>): Array<Array<Int>> {
            try {
                val n: Int = mat.size
                val m: Int = mat[0].size

                var temp = Array(n) { Array(2 * n) { 0 } }
                if (n != m) {
                    throw Exception("Matrix is not invertible! $n v/s $m ")
                }
                for (i in 0 until n) {
                    for (j in 0 until n) {
                        temp[i][j] = mat[i][j]
                    }

                    for (j in n until 2 * n)
                        temp[i][j] = 0

                    temp[i][i + n] = 1
                }

                temp = lowerZeroMatrix(temp, true)
                val ntemp: Int = temp.size
                val mtemp: Int = temp[0].size

                for (i in 0 until ntemp) {
                    var factor = getInverse(temp[i][i])
                    for (j in i until 2 * ntemp) {
                        temp[i][j] = multiply(temp[i][j], factor)
                    }
                }

                temp = upperZeroMatrix(temp)
                val ret: Array<Array<Int>> = Array(ntemp) { Array(ntemp) { 0 } }

                for (i in 0 until ntemp) {
                    for (j in ntemp until 2 * ntemp) {
                        ret[i][j - ntemp] = temp[i][j]
                    }
                }

                return ret

            } catch (e: Exception) {
                println(e)
                throw Exception("MATRIX NOT INVERTIBLE!")
            }
        }

        // Multiply a matrix and a vector within the finite field.
        @JvmStatic
        fun multiplyMatrixVector(M: Array<Array<Int>>, v: Array<Int>): Array<Int> {
            val n1: Int = M.size
            val m1: Int = M[0].size
            val n: Int = v.size

            if (m1 != n)
                throw Exception("Cannot multiply")

            val ret: Array<Int> = Array(n1) { 0 }
            for (i in 0 until n1) {
                for (j in 0 until n) {
                    val temp: Int = multiply(M[i][j], v[j])
                    ret[i] = add(ret[i], temp)
                }
            }

            return ret

        }

        // Sum two vectors
        fun addVectors(v1: Array<Int>, v2: Array<Int>): Array<Int> {
            if (v1.size != v2.size)
                throw Exception("Cannot add vectors")

            val n = v1.size
            val ret = Array(n) { 0 }

            for (i in 0 until n)
                ret[i] = add(v1[i], v2[i])

            return ret

        }

        // Multiply a scalar and a vector
        @JvmStatic
        fun multiplyScalarVector(s: Int, v: Array<Int>): Array<Int> {
            val n = v.size
            val ret = Array(n) { 0 }

            for (i in 0 until n)
                ret[i] = multiply(s, v[i])

            return ret
        }

        // Multiply two vector.
        // this vector multiplication always return a n x n matrix
        @JvmStatic
        fun multiplyVectors(v1: Array<Int>, v2: Array<Int>): Array<Array<Int>> {
            if (v1.size != v2.size) {
                //raise GF256Errors ("Vectors must be of same length to multiply!")
            }

            val n = v1.size
            val ret = Array(n) { Array(n) { 0 } }

            for (i in 0 until n) {
                for (j in 0 until n) {
                    ret[i][j] = multiply(v1[i], v2[j])
                }
            }

            return ret
        }

        // Multiply a matrix with a scalar within the finite field.
        @JvmStatic
        fun multiplyMatrixScalar(m: Array<Array<Int>>, s: Int): Array<Array<Int>> {
            val n1: Int = m.size
            val n2: Int = m[0].size

            val ret = Array(n1) { Array(n2) { 0 } }
            for (i in 0 until n1)
                for (j in 0 until n2)
                    ret[i][j] = multiply(m[i][j], s)

            return ret

        }

        // Add two matrices given by m1 and m2
        @JvmStatic
        fun addMatrices(m1: Array<Array<Int>>, m2: Array<Array<Int>>): Array<Array<Int>> {
            if (m1.size != m2.size || m1[0].size != m2[0].size)
                throw Exception("Cannot add matrices")

            val n1: Int = m1.size
            val n2: Int = m1[0].size

            val ret: Array<Array<Int>> = Array(n1) { Array(n2) { 0 } }
            for (i in 0 until n1)
                for (j in 0 until n2)
                    ret[i][j] = add(m1[i][j], m2[i][j])

            return ret

        }

        // Backward substitution method to find x given m1 * x = v
        // In the code, this methods its aplied after lowerZeroMatrix
        // size of matrix M1 is of the form n x (n+1)
        @JvmStatic
        fun substitute(M1: Array<Array<Int>>, v: Array<Int>): Array<Int> {
            val n1: Int = M1.size
            val m1: Int = M1[0].size
            val n: Int = v.size

            var temp = getInverse(M1[n1 - 1][n1 - 1])
            if (temp == 0)
                throw Exception("Equations cannot be solved!")

            v[n1 - 1] = multiply(M1[n1 - 1][n1], temp)

            for (i in n - 2 downTo 0) {
                var aux: Int = M1[i][n1]

                for (j in n - 1 downTo i + 1) {
                    temp = multiply(M1[i][j], v[j])
                    aux = add(aux, temp)
                }

                temp = getInverse(M1[i][i])
                if (temp == 0)
                    throw Exception("Equations cannot be solved!")

                v[i] = multiply(aux, temp)

            }

            return v

        }

        // Solve a system of linear equation of the form : m1 * x = v
        @JvmStatic
        fun solveEquation(M: Array<Array<Int>>, v: Array<Int>): Array<Int> {
            val n1: Int = M.size
            val m1: Int = M[0].size
            val n: Int = v.size

            if (n1 != n)
                throw Exception("Matrices need to have the same dimensions! $n1 vs $n")

            var temp: Array<Array<Int>> = Array(n1) { Array(n1 + 1) { 0 } }
            var ret: Array<Int> = Array(n1) { 0 }

            for (i in 0 until n1) {
                for (j in 0 until m1) {
                    temp[i][j] = M[i][j]
                }
            }

            for (i in 0 until n)
                temp[i][n] = add(v[i], temp[i][n])

            temp = lowerZeroMatrix(temp, false)
            ret = substitute(temp, ret)

            return ret
        }
    }
}