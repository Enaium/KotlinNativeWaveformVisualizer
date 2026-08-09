package cn.enaium.waveformvisualizer

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker

actual suspend fun pickPcmFileBytes(): ByteArray? {
    val file = FileKit.openFilePicker(type = FileKitType.File(setOf("pcm"))) ?: return null
    return file.readBytes()
}
