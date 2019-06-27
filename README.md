# MQ Cryptography

Implementación de esquema criptografico post-cuantico en base a polinomios cuadraticos 
multivariables denominado Rainbow.
Esta implementacion fue pensada como un trabajo sobre criptografia post-cuantica, 
por lo que fue diseñado con un enfoque educativo y no practico.

## Forma de uso

1. Crear las llaves.<br>
    
    Al momento de crear un objeto RainbowKeygen se generan las claves publica y 
    privada inmediatamente.
    
    ````kotlin
    val myKeyObject = RainbowKeygen(save = "./")
    ````

2. Firmar un mensaje.

    La firma se realiza utilizando la llave privada. Suponiendo que el documento se llama 
    `testSign.txt` un ejemplo de uso seria.
    
    ````kotlin
    val testingFile = "testSign.txt"

    var signTesting = myKeyObject.sign(myKeyObject.privateKey, testingFile)
    ````
    
3. Verificar una firma.

    La verificacion de una firma se realiza utilizando la llave publica. Suponiendo que el
     documento se llama `testSign.txt`, y que la firma es `signTesting`, un ejemplo de uso seria. 
     
     ````kotlin
     val testingFile = "testSign.txt"
  
     var resultVerifyTesting = myKeyObject.verify(myKeyObject.publicKey, signTesting, testingFile)
     ````
     
## Ejemplo

Un ejemplo de uso se encuentra en la carpeta `MQRainbowTesting`.