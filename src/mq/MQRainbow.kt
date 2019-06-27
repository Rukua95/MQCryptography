package mq

import java.io.File
import java.io.InputStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.system.measureTimeMillis


class RainbowKeygen(var N: Int = 32, var U: Int = 5, var K: Int = 8, val save: String = "", private val debug:Boolean=true) {
    private var V: Array<Int> = generateVinegars()
    private var F_layers: Array<Array<HashMap<String, Array<Array<Int>>>>> = generateCoefficients()
    var L1Affine = Affine(N - V[0], K, 666).generate()
    var L2Affine = Affine(N, K, 666).generate()

    // Parameters for affine transformation and private key.
    private var L1: Array<Array<Int>> = L1Affine["l"]!!
    private var L1inv: Array<Array<Int>> = L1Affine["lInverse"]!!
    private var b1: Array<Int> = L1Affine["b"]!![0]
    private var L2: Array<Array<Int>> = L2Affine["l"]!!
    private var L2inv: Array<Array<Int>> = L2Affine["lInverse"]!!
    private var b2: Array<Int> = L2Affine["b"]!![0]

    var publicKey = PublicKeyClass(N, V[0], K)
    val privateKey = PrivateKeyClass()

    init {
        if (debug)
            println("->MQRainbow: Initialised with parameters n :$N (number of variables), u (number of layers) :$U")

        val generationRuntime = measureTimeMillis {
            generateKeys(save)
        }

        if (debug) {
            println("->MQRainbow: $generationRuntime millseconds used generating keys.\n")
        }

    }
/*
    fun showFLayers() {
        println("F_layers")
        for(layer in F_layers) {
            println("  Layer")

            for(hash in layer) {
                println("    Hash alpha")
                var h = hash["alphas"] ?: throw Exception("alpha")
                for(row in h) {
                    for(j in row) {
                        if(j > 255) {
                            throw Exception("alpha exed $j")
                        }
                    }
                }

                println("    Hash beta")
                h = hash["betas"] ?: throw Exception("betas")
                for(row in h) {
                    for(j in row) {
                        if(j > 255) {
                            throw Exception("betas exed $j")
                        }
                    }
                }

                println("    Hash gamma")
                h = hash["gammas"] ?: throw Exception("gammas")
                for(row in h) {
                    for(j in row) {
                        if(j > 255) {
                            throw Exception("gamma exed $j")
                        }
                    }
                }

                println("    Hash eta")
                h = hash["etas"] ?: throw Exception("etas")
                for(row in h) {
                    for(j in row) {
                        if(j > 255) {
                            throw Exception("etas exed $j")
                        }
                    }
                }

            }
        }
    }
*/
    class MyPolynomial(N: Int, V: Int) {
        var quadratic: Array<Array<Array<Int>>> = Array(N - V){Array(N) {Array(N) {0}}}
        var linear: Array<Array<Int>> = Array(N - V) {Array(N) {0}}
        var constant: Array<Int> = Array(N - V) {0}
    }

    class PublicKeyClass(val n: Int, val v0: Int, val k: Int) {
        var quads: Array<ArrayList<Int>> = Array(0) {ArrayList<Int>()}
        var linear: Array<Array<Int>> = Array(0) {Array(0) {0}}
        var constant: Array<Int> = Array(0) {0}

        fun addQuads(q: Array<ArrayList<Int>>) {quads = q}

        fun addLinear(l: Array<Array<Int>>) {linear = l}

        fun addConstant(c: Array<Int>) {constant = c}

    }

    class PrivateKeyClass {
        lateinit var l1: Array<Array<Int>>
        lateinit var l1inv: Array<Array<Int>>
        lateinit var b1: Array<Int>

        lateinit var l2: Array<Array<Int>>
        lateinit var l2inv: Array<Array<Int>>
        lateinit var b2: Array<Int>

        var privN: Int = -1
        var privK: Int = -1
        var privLayers: Int = -1

        lateinit var Flayer: Array<Array<HashMap<String, Array<Array<Int>>>>>

        fun setL1(L: Array<Array<Int>>, Linv: Array<Array<Int>>) {l1 = L; l1inv = Linv}

        fun setL2(L: Array<Array<Int>>, Linv: Array<Array<Int>>) {l2 = L; l2inv = Linv}

        fun setb1(b: Array<Int>) {b1 = b}

        fun setb2(b: Array<Int>) {b2 = b}

        fun setFLayer(F: Array<Array<HashMap<String, Array<Array<Int>>>>>) {Flayer = F}

        fun setN(n: Int) {privN = n}

        fun setK(k: Int) {privK = k}

        fun setLayers(l: Int) {privLayers = l}

    }

    // Generate a cryptographically secure random number within the finite field..
    fun generateRandomElement(): Int {
        var num: Int = GF256.get()
        while(num == 0)
            num = GF256.get()

        return num

    }

    // Generate 2D matrix with random elements below k
    fun generateRandomMatrix(x: Int, y: Int, k: Int): Array<Array<Int>> {
        val mat: Array<Array<Int>> = Array(x) {Array(y) {0}}
        for(i in 0 until x) {
            for(j in 0 until y) {
                mat[i][j] = generateRandomElement()
            }
        }

        return mat
    }

    // Generate vinegar variables for 'u' layers where the last layer has 'n' vinegars
    fun generateVinegars(): Array<Int> {
        if (debug)
            println("->MQRainbow: Generating vinegars variables...")
        var rnum = generateRandomElement()
        while(rnum > N || (rnum - N) >= U) {
            rnum = generateRandomElement()
        }

        var cont: Int = 0
        val ret: Array<Int> = Array(U) {-1}

        while(true) {
            if(cont == U) {
                ret.sort()
                ret[U-1] = N
                break
            }

            rnum = generateRandomElement()

            if((rnum !in ret) && (rnum != N) && (rnum - N < U - cont)) {
                ret[cont] = rnum
                cont++
            }

        }

        if(debug)
            println("->MQRainbow: Done generating vinegar variable count for each layer")

        if (debug) {
            println("  ->Number of sets u = $U:")
            for (i in 0 until ret.size)
                println("    - v${i + 1} = ${ret[i]}")
            print("\n")
        }

        return ret

    }

    // Generate F - coefficients below 'k' for every polynomial in the keys
    fun generateCoefficients(): Array<Array<HashMap<String, Array<Array<Int> > > > > {
        if (debug) {
            println("->MQRainbow: Generating coefficients...")
        }
        val ret: Array<Array<HashMap<String, Array<Array<Int> > > > > = Array(U-1) {Array(0) { HashMap<String, Array<Array<Int>>>()}}

        for(_i in 0 until U-1) {
            if (debug)
                println("  ->Generating coefficients for layer ${_i+1}")

            val ol: Int = V[_i+1] - V[_i]
            if (debug)
                println("    ->Number of oil variables: $ol")

            val temp: Array<HashMap<String, Array<Array<Int> > > > = Array(ol) {HashMap<String, Array<Array<Int>>>()}

            for(i in 0 until ol) {
                val alphas = generateRandomMatrix(V[_i], V[_i], K)
                val betas = generateRandomMatrix(V[_i + 1] - V[_i], V[_i], K)
                val gammas = generateRandomMatrix(1, V[_i + 1], K)
                val etas = generateRandomMatrix(1, 1, K)

                var layer: HashMap<String, Array<Array<Int>>> = HashMap()
                layer["alphas"] = alphas
                layer["betas"] = betas
                layer["gammas"] = gammas
                layer["etas"] = etas

                temp[i] = layer
            }

            ret[_i] = temp
        }

        if(debug) {
            println("->MQRainbow: Done generating F map for each layer")
            println()
        }

        return ret
    }

    // Generates polynomials for the Map F
    // (la generacion es capa por capa, generando ol polinomios por capa)
    // (mas especificamente, entrega inmediatamente en polynomial la composicion de F con L2)
    fun generatePolynomial(vl: Int, ol: Int, pcount: Int, coefficients: Array<HashMap<String, Array<Array<Int> > > >, polynomial: MyPolynomial): MyPolynomial {
        // Composition of F and L2.
        for(_i in 0 until ol) {

            // Multiply alphas.
            for(i in 0 until vl) {
                for(j in 0 until vl) {
                    val alphas: Array<Array<Int>> = coefficients[_i]["alphas"] ?: throw Exception("Generate polynomial: dictionary key doesn't exist [alphas].")

                    var temp1 = GF256.multiplyScalarVector(alphas[i][j], L2[i])
                    polynomial.quadratic[pcount + _i] = GF256.addMatrices(polynomial.quadratic[pcount + _i], GF256.multiplyVectors(temp1, L2[j]))
                    temp1 = GF256.multiplyScalarVector(b2[j], temp1)
                    polynomial.linear[pcount + _i] = GF256.addVectors(temp1, polynomial.linear[pcount + _i])
                    temp1 = GF256.multiplyScalarVector(alphas[i][j], L2[j])
                    temp1 = GF256.multiplyScalarVector(b2[i], temp1)
                    polynomial.linear[pcount + _i] = GF256.addVectors(temp1, polynomial.linear[pcount + _i])
                    val temp2: Int = GF256.multiply(alphas[i][j], b2[i])
                    polynomial.constant[pcount + _i] = GF256.add(polynomial.constant[pcount + _i], GF256.multiply(temp2, b2[j]))
                }
            }

            // Multiply betas.
            for(i in 0 until ol) {
                for(j in 0 until vl) {
                    val betas: Array<Array<Int>> = coefficients[_i]["betas"] ?: throw Exception("Generate polynomial: dictionary key doesn't exist [betas].")

                    var temp1 = GF256.multiplyScalarVector(betas[i][j], L2[i + vl])
                    polynomial.quadratic[pcount + _i] = GF256.addMatrices(polynomial.quadratic[pcount + _i], GF256.multiplyVectors(temp1, L2[j]))
                    temp1 = GF256.multiplyScalarVector(b2[j], temp1)
                    polynomial.linear[pcount + _i] = GF256.addVectors(temp1, polynomial.linear[pcount + _i])
                    temp1 = GF256.multiplyScalarVector(betas[i][j], L2[j])
                    temp1 = GF256.multiplyScalarVector(b2[i + vl], temp1)
                    polynomial.linear[pcount + _i] = GF256.addVectors(temp1, polynomial.linear[pcount + _i])
                    val temp2 = GF256.multiply(betas[i][j], b2[i + vl])
                    polynomial.constant[pcount + _i] = GF256.add(polynomial.constant[pcount + _i], GF256.multiply(temp2, b2[j]))

                }
            }

            // Multiply gamma
            for(i in 0 until vl+ol) {
                val gammas: Array<Array<Int>> = coefficients[_i]["gammas"] ?: throw Exception("Generate polynomial: dictionary key doesn't exist [gammas].")
                val temp = GF256.multiplyScalarVector(gammas[0][i], L2[i])
                polynomial.linear[pcount + _i] = GF256.addVectors(temp, polynomial.linear[pcount + _i])
                polynomial.constant[pcount + _i] = GF256.add(polynomial.constant[pcount + _i], GF256.multiply(gammas[0][i], b2[i]))
            }

            // Add Eta
            val etas: Array<Array<Int>> = coefficients[_i]["etas"] ?: throw Exception("Generate polynomial: dictionary key doesn't exist [etas].")
            polynomial.constant[pcount + _i] = GF256.add(polynomial.constant[pcount + _i], etas[0][0])

        }

        return polynomial
    }

    // Generates the public key.
    /*
    Parameters:
        save - the destination folder
     */
    fun generatePublicKey(save: String = "") {
        if(debug)
            println("->MQRainbow: Generating public key...")

        var polynomial: MyPolynomial = MyPolynomial(N, V[0])
        var olcount = 0
        var pcount = 0

        if(debug)
            println("  ->Generating polynomials...")

        for(_i in 0 until U - 1) {
            if(debug)
                print("    ->Layer ${_i+1} ")

            val vl = F_layers[_i][0]["alphas"]!![0].size
            val ol = F_layers[_i][0]["betas"]!!.size

            if(debug)
                println("with $ol polynomials")

            polynomial = generatePolynomial(vl, ol, pcount, F_layers[_i], polynomial)

            pcount += ol

        }
        if(debug)
            println("  ->Done generating polynomials")

        // Composition of L1 and F * L2
        if(debug)
            println("  ->Calculating composition of L1 and F * L2...")

        val tempQuadratic: Array<Array<Array<Int>>> = Array(N - V[0]) {Array(N) {Array(N) {0}}}
        val tempLinear: Array<Array<Int>> = Array(N - V[0]) {Array(N) {0}}
        val tempConstant: Array<Int> = Array(N - V[0]) {0}

        for(i in 0 until N - V[0]) {
            for(j in 0 until L1.size) {
                tempQuadratic[i] = GF256.addMatrices(
                    tempQuadratic[i],
                    GF256.multiplyMatrixScalar(
                        polynomial.quadratic[j],
                        L1[i][j]
                    )
                )

                tempLinear[i] = GF256.addVectors(
                    tempLinear[i],
                    GF256.multiplyScalarVector(
                        L1[i][j],
                        polynomial.linear[j]
                    )
                )
                tempConstant[i] = GF256.add(
                    tempConstant[i],
                    GF256.multiply(
                        L1[i][j],
                        polynomial.constant[j]
                    )
                )
            }

            tempConstant[i] = GF256.add(tempConstant[i], b1[i])
        }

        // Assign the computed values for L1 * F * L2
        polynomial.quadratic = tempQuadratic
        polynomial.linear = tempLinear
        polynomial.constant = tempConstant

        // Compactation
        if(debug)
            println("  ->Compactation quadratic coefficients...")

        val compactQuads = Array(polynomial.quadratic.size) {ArrayList<Int>()}
        for(i in 0 until compactQuads.size) {
            for(j in 0 until polynomial.quadratic[i].size) {
                for(k in j until polynomial.quadratic[i][j].size) {
                    if(j == k) {
                        compactQuads[i].add(polynomial.quadratic[i][j][k])
                    } else {
                        compactQuads[i].add(GF256.add(polynomial.quadratic[i][k][j], polynomial.quadratic[i][j][k]))
                    }
                }
            }
        }

        if(debug)
            println("  ->Assign the computed values for L1 * F * L2 to the public key...")

        publicKey.addQuads(compactQuads)
        publicKey.addLinear(polynomial.linear)
        publicKey.addConstant(polynomial.constant)

        /* TODO: save the public key
        if save != '':
            with open(save + 'cvPub.pub', 'wb') as pubFile:
                dill.dump(publicKey, pubFile)
         */

        if(debug)
            println("->MQRainbow: Done generating public key.\n")

    }

    // Generates the private key.
    /*
    Parameters:
        save - the destination key.
     */

    fun generatePrivatekey(save: String = "") {
        if(debug)
            println("->MQRainbow: Generating Private key...")

        if(debug)
            println("  ->Saving L1 and L2 matrix (affine transformation parameters)...")
        privateKey.setL1(L1, L1inv)
        privateKey.setL2(L2, L2inv)

        if(debug)
            println("  ->Saving b1 and b2 vectors (affine transformation parameters)...")
        privateKey.setb1(b1)
        privateKey.setb2(b2)

        if(debug)
            println("  ->Saving coefficients from polynomials (oil and vinegar polynomials)")
        privateKey.setFLayer(F_layers)
        privateKey.setK(K)

        /*
        if save != '':
            with open(save + 'cvPriv.pem', 'wb') as privFile:
                dill.dump(privateKey, privFile)

        */

        if(debug)
            println("->MQRainbow: Done generating private key.\n")

    }

    // Generates Y or the set of targets from the hash of the message.
    fun generateTargets(n: Int, v0: Int, k: Int, message: String): ArrayList<Int> {
        if(debug)
            println("  ->MQRainbow: Generating targets...")

        // TODO: Reemplazar siguientes lineas.
        // h = hashlib.new('ripemd160')
        // h.update(bytes(message, encoding='utf-8'))
        // newMessage = h.hexdigest()
        // Las lineas anteriores hashean el mensaje, no se si es algo necesario, o si afecta el resultado final.

        //if(debug)
        //    println("->Hashed message of length ${message.length}")

        //message = newMessage

        val parts = n - v0
        if(debug) {
            println("    ->Message of size ${message.length}")
            println("    ->Splitting mesage into $parts")
        }

        val ret: ArrayList<Int> = ArrayList()
        val part = 1 + message.length / (parts + 1)

        //TODO: see how to avoid shadowing variables
        var k = 0
        for(i in 0 until parts) {
            var yPart = 0
            for(j in 0 until part) {
                if(k >= message.length)
                    break;
                yPart = yPart.or(message[k].toInt())
                k++
            }
            if(k >= message.length) {
                ret.add(yPart)
                break
            }

            ret.add(yPart)

        }

        while(ret.size < parts)
            ret.add(0)

        if(debug)
            println("  ->MQRainbow: Done generating targets.\n")

        return ret

    }

    // Sign message at msgFile with private key at keyFile!
    fun sign(privKey: PrivateKeyClass, msgFile: String):Array<Int> {
        if(debug)
            println("->MQRainbow: Began signing...")

        val len = privKey.Flayer.size
        privKey.setN(privKey.Flayer[len-1][0]["alphas"]!![0].size + privKey.Flayer[len-1][0]["betas"]!!.size)
        privKey.setLayers(len)

        // Load message (as n dimensional vector)
        // readText no es recomendado para archivos grandes
        if(debug)
            println("  ->Reading message...")
        val message = File(msgFile).readText(Charsets.UTF_8)

        if(debug) {
            println("  ->Message to sign:")
            println("----------------------------------------------------")
            println(message)
            println("----------------------------------------------------")
            println("  ->Generating targets...\n")
        }

        val y = generateTargets(privKey.privN, privKey.Flayer[0][0]["alphas"]!![0].size, privKey.privK, message)
        val yArray: Array<Int> = Array(y.size) {0}
        for(i in 0 until y.size)
            yArray[i] = y[i]

        // Apply L1^(-1)
        if(debug)
            println("  ->Applying affine transformation: L1^-1")
        var ydash = GF256.addVectors(yArray, privKey.b1)
        ydash = GF256.multiplyMatrixVector(privKey.l1inv, ydash)

        val v0 = privKey.Flayer[0][0]["alphas"]!![0].size

        var signature:Array<Int>
        while(true) {
            try{
                var x = Array(privKey.Flayer[0][0]["alphas"]!![0].size) {0}
                for(i in 0 until x.size)
                    x[i] = generateRandomElement()

                if(debug)
                    println("  ->Calculating inverse of F...")
                for(layer in 0 until privKey.privLayers) {
                    var vl = privKey.Flayer[layer][0]["alphas"]!![0].size
                    val ol = privKey.Flayer[layer][0]["betas"]!!.size

                    val equations = Array(ol) {Array(ol+1) {0}}
                    val consts = Array(ol) {0}

                    if (debug) {
                        println("    ->Layer ${layer+1}: $ol oil variables and $vl vinegars variables.")
                    }

                    for (i:Int in 0 until ol) {
                        for (j:Int in 0 until vl) {
                            for (k:Int in 0 until vl) {
                                consts[i] = GF256.add(
                                    consts[i],
                                    GF256.multiply(
                                        GF256.multiply(
                                            privKey.Flayer[layer][i]["alphas"]!![j][k],
                                            x[j]
                                        ),
                                        x[k]
                                    )
                                )
                            }
                        }

                        for (j:Int in 0 until ol) {
                            for (k:Int in 0 until vl) {
                                equations[i][j] = GF256.add(
                                    equations[i][j],
                                    GF256.multiply(
                                        privKey.Flayer[layer][i]["betas"]!![j][k],
                                        x[k]
                                    )
                                )
                            }
                        }

                        for (j:Int in 0 until ol+vl) {
                            if (j < vl) {
                                consts[i] = GF256.add(
                                    consts[i],
                                    GF256.multiply(
                                        privKey.Flayer[layer][i]["gammas"]!![0][j],
                                        x[j]
                                    )
                                )
                            } else {
                                equations[i][j-vl] = GF256.add(
                                    equations[i][j-vl],
                                    privKey.Flayer[layer][i]["gammas"]!![0][j]
                                )
                            }
                        }

                        consts[i] = GF256.add(
                            consts[i],
                            // TODO Revisar
                            privKey.Flayer[layer][i]["etas"]!![0][0]
                        )
                    }

                    for (e:Int in 0 until ol) {
                        equations[e][ol] = consts[e]
                    }

                    val start = x.size - v0
                    val yDashSubset = Array(ol) {0}
                    for (j:Int in 0 until ol) {
                        yDashSubset[j] = ydash[start+j]
                    }

                    if (debug) {
                        println("    ->Number of fixed variables at the moment: $start")
                        println("    ->Number of equations to solve in layer: ${equations.size}")
                        println("      ->Solving equations...")
                    }

                    val solns = GF256.solveEquation(equations, yDashSubset)
                    val tempX = Array(x.size + solns.size) {0}
                    for (j:Int in 0 until x.size) {
                        tempX[j] = x[j]
                    }

                    for (s:Int in 0 until solns.size) {
                        tempX[x.size+s] = solns[s]
                    }

                    x = tempX
                }
                println("  ->Done calculating inverse of F")

                if(debug)
                    println("  ->Applying L2^-1")

                signature = GF256.addVectors(x, privKey.b2)
                signature = GF256.multiplyMatrixVector(privKey.l2inv, signature)
                if (debug) {
                    println("  ->Size of signature: ${signature.size}")
                }
                break
            } catch (e: Exception) {
                println("  ->ERROR DURING SIGNING.\n  ->REPEATING PROCESS.")
            }
        }

        if(debug)
            println("->MQRainbow: Done signing.\n")
        return signature
    }




    // Verify the signature.
    fun verify(keyFile: Any, signature:Array<Int>, msgFile: String): Boolean {
//        if isinstance(keyFile, pubKeyClass):
//          pubKey = keyFile
//          if args.v:
//              print("Loading public key from file...")
//        else:
//           with open(keyFile, 'rb') as kFile:
//              pubKey = dill.load(kFile)

        if(debug) {
            println("->MQRainbow: Begin to verify...")
            println("  ->Loading public key...")
        }
        when (keyFile) {
            is PublicKeyClass -> publicKey = keyFile
            is String -> {
                val inputStream:InputStream = File(keyFile).inputStream()
                val inputString = inputStream.bufferedReader().use { it.readText() }
                // TODO: see how to deserialize Kotlin class from file
                //publicKey =
            }
            else -> {
                println("    ->Error: keyFile should be PublicKeyClass or String")
                return false
            }
        }

        val quadratic: Array<Array<Array<Int>>> = Array(publicKey.quads.size) {Array(0) {Array(0) {0}}}

        if(debug)
            println("  ->Decompressing quadratics coefficients...")
        for(k in 0 until publicKey.quads.size) {
            val temp: Array<Array<Int>> = Array(publicKey.n) {Array(publicKey.n) {0}}
            for(i in 0 until publicKey.n) {
                for(j in 0 until publicKey.n) {
                    if(i < j)
                        temp[i][j] = 0
                    else
                        temp[i][j] = publicKey.quads[k][j+i]
                }
            }
            quadratic[k] = temp
        }

        if(debug)
            println("  ->Reading message...")

        val y:ArrayList<Int>
        var message = ""
        try {
            val inputStream:InputStream = File(msgFile).inputStream()
            message = inputStream.bufferedReader().use { it.readText() }
            if (debug) {
                println("----------------------------------------------------")
                println(message)
                println("----------------------------------------------------")
            }


        } catch (e:Exception) {
            println("    ->Error while reading message")
        }

        println("  ->Generating targets...")
        y = generateTargets(publicKey.n, publicKey.v0, publicKey.k, message)
        val ret =  Array(publicKey.quads.size) {0}

        for (p:Int in 0 until publicKey.quads.size) {
            var offset = 0
            for (q:Int in 0 until publicKey.n) {
                for (r:Int in q until publicKey.n) {
                    ret[p] = GF256.add(
                        GF256.multiply(
                            publicKey.quads[p][offset],
                            GF256.multiply(
                                signature[q],
                                signature[r]
                            )
                        ),
                        ret[p]
                    )
                    offset++
                }
                ret[p] = GF256.add(
                    ret[p],
                    GF256.multiply(
                        publicKey.linear[p][q],
                        signature[q]
                    )
                )
            }
            ret[p] = GF256.add(
                ret[p],
                publicKey.constant[p]
            )
        }

        val retRange = Array(ret.size) {0}
        for (i:Int in 0 until ret.size) {
            retRange[i] = i
        }


        /**
         * Returns a list of lists, each built from elements of all lists with the same indexes.
         * Output has length of shortest input list.
         */
        fun <T> zip(vararg lists: List<T>): List<List<T>> {
            /**
             * Returns a list of values built from elements of all lists with same indexes using provided [transform].
             * Output has length of shortest input list.
             */
            fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V> {
                val minSize = lists.map(List<T>::size).min() ?: return emptyList()
                val list = ArrayList<V>(minSize)

                val iterators = lists.map { it.iterator() }
                var i = 0
                while (i < minSize) {
                    list.add(transform(iterators.map { it.next() }))
                    i++
                }

                return list
            }
            return zip(*lists, transform = { it })
        }

        val verification = zip(retRange.toList(), ret.toList(), y.toList())

        println("  ->Comparing evaluation of polynomials and sign...")
        var results = true
        for (tuple in verification) {
            val i = tuple[0]
            val a = tuple[1]
            val b = tuple[2]
            if (debug) {
                val cond = a==b
                println("    ->Polinomial ${i+1} => evaluation of sign: $a, message representation: $b => $cond")
            }

            if (a != b) {
                results = false
                break
            }
        }
        if(debug)
            println("->MQRainbow: End of verifycation.\n")

        return results
    }

    // Generates both the private and public keys.
    fun generateKeys(save: String = "") {
        if(debug)
            println("->MQRainbow: Begin generation of keys.")

        generatePublicKey("")
        generatePrivatekey("")
    }

    // TODO: Faltan estas weas, pero se necesita saber como serializar objetos de Kotlin en archivos
    fun saveSignature() {}

    fun loadSignature() {}

}