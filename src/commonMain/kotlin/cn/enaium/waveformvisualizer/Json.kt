package cn.enaium.waveformvisualizer

private const val HEX = "0123456789abcdef"

fun jsonString(value: String): String = buildString {
    append('"')
    for (c in value) {
        when {
            c == '"' -> append("\\\"")
            c == '\\' -> append("\\\\")
            c == '\n' -> append("\\n")
            c == '\r' -> append("\\r")
            c == '\t' -> append("\\t")
            c.code < 0x20 -> {
                append("\\u00")
                append(HEX[(c.code shr 4) and 0xF])
                append(HEX[c.code and 0xF])
            }

            else -> append(c)
        }
    }
    append('"')
}
