package mq

fun main() {
    println("Initializing Raindom Keygen Object")
    val myKeyObject = RainbowKeygen(save = "./")

    println("Generating Random Elements")
    for (i:Int in 0 until 10) {
        print("${myKeyObject.generateRandomElement()} ")
    }
}