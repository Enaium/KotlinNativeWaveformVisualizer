package cn.enaium.waveformvisualizer

import kotlin.math.roundToInt

private const val PCM_SAMPLE_RATE = 16000
private const val MAX_POINTS = 200_000

expect suspend fun pickPcmFileBytes(): ByteArray?

suspend fun loadPcmFile(): String {
    val bytes = pickPcmFileBytes() ?: return "null"
    val samples = parsePcm16BitLe(bytes)
    if (samples.isEmpty()) return "null"

    val step = (samples.size / MAX_POINTS + 1).coerceAtLeast(1)
    return buildString {
        append("{\"sampleRate\":")
        append(PCM_SAMPLE_RATE)
        append(",\"data\":[")
        var i = 0
        while (i < samples.size) {
            if (i > 0) append(',')
            append((samples[i] / 32768.0 * 100000.0).roundToInt() / 100000.0)
            i += step
        }
        append("]}")
    }
}

private fun parsePcm16BitLe(bytes: ByteArray): ShortArray {
    val count = bytes.size / 2
    val result = ShortArray(count)
    for (i in 0 until count) {
        val lo = bytes[i * 2].toInt() and 0xFF
        val hi = bytes[i * 2 + 1].toInt()
        result[i] = ((hi shl 8) or lo).toShort()
    }
    return result
}
