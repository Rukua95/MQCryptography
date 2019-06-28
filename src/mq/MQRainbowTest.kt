package mq

fun main() {
    println("Initializing Rainbow Keygen Object")
    val myKeyObject = RainbowKeygen(save = "./")

    println("Testing verify and sign methods.")
    val testingFile = "C:\\Users\\thi-s\\Documents\\A6_S1\\Intro_Criptografia\\Proyecto\\MQCryptography\\src\\testSign.txt"
    val rexFile = "C:\\Users\\thi-s\\Documents\\A6_S1\\Intro_Criptografia\\Proyecto\\MQCryptography\\src\\rexFile.txt"

    var signTesting = myKeyObject.sign(myKeyObject.privateKey, testingFile)

    println("Testing correct verify.")
    var resultVerifyTesting = myKeyObject.verify(myKeyObject.publicKey, signTesting, testingFile)
    if(resultVerifyTesting)
        println("Results: Mensaje autentico.\n")
    else
        println("Results: Mensaje y firma no concuerdan.\n")

    var forgeTestingSign = signTesting
    forgeTestingSign[0]++

    println("Testing incorrect verify: change in sign.\n")
    var resultVerifyForge = myKeyObject.verify(myKeyObject.publicKey, forgeTestingSign, testingFile)
    if(resultVerifyForge)
        println("Results: Mensaje autentico")
    else
        println("Results: Mensaje y firma no concuerdan.\n")

    println("Testing incorrect verify: diferent message.")
    var resultsForgeRex = myKeyObject.verify(myKeyObject.publicKey, signTesting, rexFile)
    if(resultsForgeRex)
        println("Results: Mensaje autentico.\n")
    else
        println("Results: Mensaje y firma no concuerdan.\n")

}