package zlc.season.downloadx.core

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import zlc.season.downloadx.utils.contentLength
import zlc.season.downloadx.utils.isSupportRange
import java.io.File
import java.util.concurrent.TimeUnit

interface HttpClientFactory {
    fun create(): OkHttpClient
}

object DefaultHttpClientFactory : HttpClientFactory {
    override fun create(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }
}

interface DownloadDispatcher {
    fun dispatch(downloadTask: DownloadTask, resp: Response<ResponseBody>): Downloader
}

object DefaultDownloadDispatcher : DownloadDispatcher {
    override fun dispatch(downloadTask: DownloadTask, resp: Response<ResponseBody>): Downloader {
        return if (resp.isSupportRange()) {
            RangeDownloader(downloadTask.coroutineScope)
        } else {
            NormalDownloader(downloadTask.coroutineScope)
        }
    }
}

interface FileValidator {
    fun validate(
        file: File,
        param: DownloadParam,
        resp: Response<ResponseBody>
    ): Boolean
}

object DefaultFileValidator : FileValidator {
    override fun validate(
        file: File,
        param: DownloadParam,
        resp: Response<ResponseBody>
    ): Boolean {
        return file.length() == resp.contentLength()
    }
}