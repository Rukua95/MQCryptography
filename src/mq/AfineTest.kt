package mq

fun main() {
    val firstAffine = Affine(10, 10, 10)
    val result = firstAffine.generate()
    println("L matrix:")
    printMat(result["l"]!!)
    println("\nL inverse matrix:")
    printMat(result["lInverse"]!!)
    println("\nb vector:")
    printMat(result["b"]!!)
    println("\nL x L Inverse:")
    printMat(GF256.multiplyMatrices(result["l"]!!, result["lInverse"]!!))
}

fun printMat(mat:Array<Array<Int>>) {
    for (i:Int in 0 until mat.size) {
        for (j:Int in 0 until mat[0].size) {
            print(mat[i][j])
            print(" ")
        }
        print("\n")
    }
}