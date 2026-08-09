package cn.enaium.waveformvisualizer

actual suspend fun pickPcmFileBytes(): ByteArray? {
    // FileKit dialogs are not published for Kotlin/Native Linux targets
    return null
}
