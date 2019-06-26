package mq

fun main() {
    println("Testing GF256...")

    println("Random num: 1. ${GF256.get()} 2. ${GF256.get()} 3. ${GF256.get()} 4. ${GF256.get()} 5. ${GF256.get()}")

    val a = GF256.get()
    val b = GF256.get()

    var c = GF256.add(a, b)
    println("Sumando $a con $b, resultado $c")

    val d = GF256.multiply(a, b)
    println("Multiplicando $a con $b, resultado $d")

    val e = GF256.multiply(d, GF256.getInverse(a))
    println("Inverso de a es ${GF256.getInverse(a)}")
    println("y po eso e = d / a = $e y b = $b")

    val M = Array(5) {Array(6) {0}}
    val R = Array(5) {Array(10) {0}}

    println("M")
    for(i in 0 until 5) {
        for (j in 0 until 6) {
            M[i][j] = GF256.get()
            R[i][j] = GF256.get()
            print("${M[i][j]} ")
        }
        print("\n")
    }
    print("\n")

    println("R")
    for(i in 0 until 5) {
        for(j in 0 until 10) {
            print("${R[i][j]} ")
        }
        print("\n")
    }
    print("\n")

    println("lowerZeroMatrix(M, false)")
    val res1 = GF256.lowerZeroMatrix(M, false)

    println("lowerZeroMatrix(R, true)")
    val res2 = GF256.lowerZeroMatrix(R, true)

    println("M lower zero")
    for(i in 0 until 5) {
        for (j in 0 until 6) {
            print("${res1[i][j]} ")
        }
        print("\n")
    }
    print("\n")

    println("R lower zero")
    for(i in 0 until 5) {
        for(j in 0 until 10) {
            print("${res2[i][j]} ")
        }
        print("\n")
    }
    print("\n")

    println("R upper and lower zero")
    val res3 = GF256.upperZeroMatrix(res2)
    for(i in 0 until 5) {
        for(j in 0 until 10) {
            print("${res3[i][j]} ")
        }
        print("\n")
    }
    print("\n")

    println("Multiply matrix")
    val A = Array(2) {Array(3) {0}}
    val B = Array(3) {Array(2) {0}}
    val I2 = Array(2) {Array(2) {0}}
    for(i in 0 until 2) {
        I2[i][i] = 1
        for (j in 0 until 3) {
            A[i][j] = GF256.add(i, j+1)
        }
    }
    for(i in 0 until 3)
        for(j in 0 until 2)
            B[i][j] = GF256.add(i, j+1)
    print("\n")

    println("A")
    for(i in 0 until 2) {
        for (j in 0 until 3)
            print("${A[i][j]} ")
        print("\n")
    }
    println("B")
    for(i in 0 until 3) {
        for (j in 0 until 2)
            print("${B[i][j]} ")
        print("\n")
    }
    println("I2")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${I2[i][j]} ")
        print("\n")
    }

    val C = GF256.multiplyMatrices(A, B)
    println("C")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${C[i][j]} ")
        print("\n")
    }

    val D = GF256.multiplyMatrices(C, I2)
    println("D")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${D[i][j]} ")
        print("\n")
    }

    val E = GF256.findInverse(D)
    println("E")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${E[i][j]} ")
        print("\n")
    }

    val F = GF256.multiplyMatrices(D, E)
    println("F")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${F[i][j]} ")
        print("\n")
    }

    val G = GF256.addMatrices(F, E)
    println("G")
    for(i in 0 until 2) {
        for (j in 0 until 2)
            print("${G[i][j]} ")
        print("\n")
    }


}