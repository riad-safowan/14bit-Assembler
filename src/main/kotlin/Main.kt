import java.io.File
import java.util.Scanner

val map = loadMap()
val binCode = StringBuilder()
val hexCode = StringBuilder()
val jumpLocations = mutableMapOf<String, Int>()

fun main() {
    println("Program Started\n")

    val assembly = "assembly.txt"
    val machineBin = "binary.txt"
    val machineHex = "hex.txt"

    val assemblyFile = File(assembly)
    val binaryFile = File(machineBin)
    val hexFile = File(machineHex)


    var instructionCount = 0

    if (assemblyFile.isFile) {
//        TimeUnit.MILLISECONDS.sleep(500L)
        println("Loading Assembly code...")
//        TimeUnit.MILLISECONDS.sleep(1500L)
        var line = 0
        assemblyFile.forEachLine { instruction ->
            println(instruction)
            for (i in instruction.indices) {
                if (instruction[i] == ' ') {
                    val subStr = instruction.substring(0, i).deductCommaOrColon()
                    if (instructionFormat(subStr) == 'L') {
                        jumpLocations[subStr.deductCommaOrColon()] = line
                    }
                    break
                }
            }
            line++
        }

//        TimeUnit.MILLISECONDS.sleep(500L)
        println("\nGenerating Binary code...")
//        TimeUnit.MILLISECONDS.sleep(1500L)
        assemblyFile.forEachLine { instruction ->
            when (getFormat(instruction)) {
                'R', 'I' -> {
                    generateBinSeq(instruction)
                }
                'L' -> {
                    for (i in instruction.indices) {
                        if (instruction[i] == ' ') {
                            generateBinSeq(instruction.substring(i + 1))
                            break
                        }
                    }
                }
            }
            if (instruction.trim().isNotEmpty()) instructionCount++
        }
        print(binCode)
        binaryFile.writeText(binCode.toString())
//        TimeUnit.MILLISECONDS.sleep(500L)
        println("Transferred Binary code to \"$machineBin\" file")
//        TimeUnit.MILLISECONDS.sleep(2000L)
        println("\nGenerating Hex code...")
        generateHexCode(binCode.toString())
//        TimeUnit.MILLISECONDS.sleep(1500L)
        print(hexCode)
        hexFile.writeText(hexCode.toString())
//        TimeUnit.MILLISECONDS.sleep(500L)
        println("Transferred Hex code to \"$machineHex\" file")

//        TimeUnit.MILLISECONDS.sleep(2000L)
        println(
            "\n$instructionCount ${if (instructionCount < 2) "line" else "lines"} of machine instruction" +
                    " ${if (instructionCount < 2) "has" else "have"} been generated."
        )
    } else
        println("Input file $assembly dose not exist in current directory.")

    val reader = Scanner(System.`in`)
    println("Press Enter to Exit")
    reader.nextLine()
}

fun generateHexCode(bin: String) {
    for (i in 0..bin.length - 14 step 15) {
        hexCode.append("00${bin.substring(i + 0, i + 2)}".binToHex())
        hexCode.append(bin.substring(i + 2, i + 6).binToHex())
        hexCode.append(bin.substring(i + 6, i + 10).binToHex())
        hexCode.append(bin.substring(i + 10, i + 14).binToHex() + "\n")
    }
}

fun generateBinSeq(instruction: String) {
    var startPos = 0
    for (i in instruction.indices) {
        if (instruction[i] == ' ') {
            val subStr = instruction.substring(startPos, i).deductCommaOrColon()
            startPos = i + 1
            binCode.append(map[subStr])
        }
        if (i == instruction.length - 1) {
            val subStr = instruction.substring(startPos, i + 1)
            try {
                val num = subStr.toInt()
                val bin = decimalTo4bitBinary(num)
                binCode.append(bin + "\n")
            } catch (e: Exception) {
                if (!map[subStr].isNullOrEmpty()) {
                    binCode.append(map[subStr] + "0\n")
                } else {
                    binCode.append(jumpLocations[subStr]?.let { decimalTo4bitBinary(it) + "\n" })
                }
            }
        }
    }
}

fun getFormat(s: String): Char {
    var firstWord = ""
    for (i in s.indices) {
        if (s[i] == ' ') {
            firstWord = s.substring(0, i).deductCommaOrColon()
            break
        }
    }
    return instructionFormat(firstWord)
}

fun String.deductCommaOrColon() = if (this.last() == ',' || this.last() == ':')
    this.substring(0, this.length - 1)
else this

fun decimalTo4bitBinary(d: Int): String {
    var decimal = d
    val b = StringBuilder()
    for (i in 0..3) {
        b.append(decimal % 2)
        decimal /= 2
    }
    return b.reverse().toString()
}

fun instructionFormat(s: String) = when (s) {
    "Add", "Sub", "AND", "Sll", "Slt" -> 'R'
    "Addi", "Beq", "Slti", "J", "Sw", "Lw" -> 'I'
    else -> 'L'
}


fun String.binToHex(): String {
    return when (this) {
        "0000" -> "0"
        "0001" -> "1"
        "0010" -> "2"
        "0011" -> "3"
        "0100" -> "4"
        "0101" -> "5"
        "0110" -> "6"
        "0111" -> "7"
        "1000" -> "8"
        "1001" -> "9"
        "1010" -> "a"
        "1011" -> "b"
        "1100" -> "c"
        "1101" -> "d"
        "1110" -> "e"
        "1111" -> "f"
        else -> "0"
    }
}

fun loadMap() = mutableMapOf(
    //Opcode
    "Add" to "0000",
    "Sub" to "0001",
    "Addi" to "0010",
    "AND" to "0011",
    "Sll" to "0111",
    "Beq" to "1001",
    "Slt" to "1011",
    "Slti" to "1100",
    "J" to "1101",
    "Sw" to "1110",
    "Lw" to "1111",
    //Register
    "\$zero" to "000",
    "\$t0" to "001",
    "\$t1" to "010",
    "\$t2" to "011",
    "\$s0" to "100",
    "\$s1" to "101",
    "\$s2" to "110",
    "\$s3" to "111",
)
