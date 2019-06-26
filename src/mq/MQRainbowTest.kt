package mq

fun main() {
    println("Initializing Raindom Keygen Object")
    val myKeyObject = RainbowKeygen(save = "./")

    println("Generating Random Elements")
    for (i:Int in 0 until 10) {
        print("${myKeyObject.generateRandomElement()} ")
    }
    print("\n")

    var sign = myKeyObject.sign(myKeyObject.privateKey, "C:\\Users\\thi-s\\Documents\\A6_S1\\Intro_Criptografia\\Proyecto\\MQCryptography\\src\\testSign.txt")

    var resultVerify = myKeyObject.verify(myKeyObject.publicKey, sign, "C:\\Users\\thi-s\\Documents\\A6_S1\\Intro_Criptografia\\Proyecto\\MQCryptography\\src\\testSign.txt")

    if(resultVerify)
        println("El mensaje esta correctamente autenticado")
    else
        println("El mensaje no esta autenticado")

    var forgeSign = sign
    forgeSign[0]++

    var resultVerifyForge = myKeyObject.verify(myKeyObject.publicKey, forgeSign, "C:\\Users\\thi-s\\Documents\\A6_S1\\Intro_Criptografia\\Proyecto\\MQCryptography\\src\\testSign.txt")

    if(resultVerifyForge)
        println("El mensaje esta correctamente autenticado")
    else
        println("El mensaje no esta autenticado")

}