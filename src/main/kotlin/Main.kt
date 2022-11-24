import java.io.File

fun main() {
    println("Program Started\n")
    val map = loadMap()

    val assembly = "assembly.txt"
    val machineBin = "binary.txt"
    val machineHex = "hex.txt"

    val assemblyFile = File(assembly)
    val binaryFile = File(machineBin)
    val hexFile = File(machineHex)

    val binCode = StringBuilder()
    val hexCode = StringBuilder()

    var instructionCount = 0
    if (assemblyFile.isFile) {
        println("Loading Assembly code...")
        assemblyFile.forEachLine {
            println(it)
        }
        println("\nGenerating Binary code...")
        assemblyFile.forEachLine { instruction ->
            var startPos = 0
            for (i in instruction.indices) {
                if (instruction[i] == ' ') {
                    val subStr = instruction.substring(startPos, i).deductComma()
                    startPos = i + 1
                    binCode.append(map[subStr] + " ")
                    hexCode.append(map[subStr]?.binToHex())
                }
                if (i == instruction.length - 1) {
                    val subStr = instruction.substring(startPos, i + 1)
                    try {
                        val num = subStr.toInt()
                        val bin = decimalTo4bitBinary(num)
                        binCode.append(bin + "\n")
                        hexCode.append(bin.binToHex() + "\n")
                    } catch (e: Exception) {
                        map[subStr]?.let { binCode.append(it + "\n") }
                        hexCode.append(map[subStr]?.binToHex() + "\n")
                    }
                }
            }
            if (instruction.trim().isNotEmpty()) instructionCount++
        }
        print(binCode)
        binaryFile.writeText(binCode.toString())
        println("Transferred Binary code to \"$machineBin\" file")
        println("\nGenerating Hex code...")
        print(hexCode)
        hexFile.writeText(hexCode.toString())
        println("Transferred Hex code to \"$machineHex\" file")

        println(
            "\n$instructionCount ${if (instructionCount < 2) "line" else "lines"} of machine instruction" +
                    " ${if (instructionCount < 2) "has" else "have"} been generated."
        )
    } else
        println("Input file $assembly dose not exist in current directory.")

}

fun String.deductComma(): String {
    return if (this.last() == ',')
        this.substring(0, this.length - 1)
    else
        this
}

fun decimalTo4bitBinary(d: Int): String {
    var decimal = d
    val b = StringBuilder()
    for (i in 0..3) {
        b.append(decimal % 2)
        decimal /= 2
    }
    return b.reverse().toString()
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
    "add" to "0110",
    "Sll" to "0111",
    "Beq" to "1001",
    "Slt" to "1011",
    "Slti" to "1100",
    "J" to "1101",
    "Sw" to "1110",
    "Lw" to "1111",
    //Register
    "\$zero" to "0000",
    "\$t0" to "0001",
    "\$t1" to "0010",
    "\$t2" to "0011",
    "\$s0" to "0100",
    "\$s1" to "0101",
    "\$s2" to "0110",
    "\$s3" to "0111",
)
