package zlc.season.downloadx.utils

import zlc.season.downloadx.task.Task
import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

fun File.shadow(): File {
    val shadowPath = "$canonicalPath.download"
    return File(shadowPath)
}

fun File.tmp(): File {
    val tmpPath = "$canonicalPath.tmp"
    return File(tmpPath)
}

fun File.recreate(length: Long = 0L, block: () -> Unit = {}) {
    delete()
    val created = createNewFile()
    if (created) {
        setLength(length)
        block()
    } else {
        throw IllegalStateException("File create failed!")
    }
}

fun File.setLength(length: Long = 0L) {
    RandomAccessFile(this, "rw").setLength(length)
}

fun File.channel(): FileChannel {
    return RandomAccessFile(this, "rw").channel
}

fun File.mappedByteBuffer(position: Long, size: Long): MappedByteBuffer {
    val channel = channel()
    val map = channel.map(FileChannel.MapMode.READ_WRITE, position, size)
    channel.closeQuietly()
    return map
}

fun File.clear() {
    val shadow = shadow()
    val tmp = tmp()
    shadow.delete()
    tmp.delete()
    delete()
}

internal fun Task.getDir(): File {
    return File(savePath)
}

internal fun Task.getFile(): File {
    return File(savePath, saveName)
}
