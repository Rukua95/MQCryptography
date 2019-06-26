package mq

fun main(args: Array<String>) {
    println("hello world!!")
    var test = GF256
    var a: Int = test.add(255, 254)
    println("Mask value :${test.mask}")
    println("Addition of 255 and 254 :$a")
    println("Subtraction of 255 and 254 : ${test.subtract(255, 254)}")
    println("Multiplication of 255 and 68 : ${test.multiply(255, 68)}")
    println("Inverse of 1 and 255 are : ${test.getInverse(1)} and ${test.getInverse(255)}")
}

