package cn.enaium.waveformvisualizer

import cn.enaium.webview.Webview
import cn.enaium.webview.createWebview
import cn.enaium.waveformvisualizer.generated.getResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun main() {
    createWebview(debug = true).use { webview ->
        webview.setTitle("WaveformVisualizer")
        webview.setSize(1200, 800)

        webview.bind("openPcmFile") { id, _ ->
            // Return immediately: a long-running callback stalls the page's JS
            // thread and the resolve eval gets dropped by WKWebView, so the JS
            // promise never settles. Run the native dialog on the GUI thread via
            // dispatch, then resolve asynchronously.
            GlobalScope.launch(Dispatchers.Default) {
                val payload = runOnGuiThread(webview) {
                    runBlocking { loadPcmFile() }
                }
                webview.returnResult(id, 0, payload)
            }
        }

        webview.setHtml(getResource("www/index.html").asString())
        webview.run()
    }
}

private suspend fun <T> runOnGuiThread(webview: Webview, block: () -> T): T =
    suspendCancellableCoroutine { continuation ->
        webview.dispatch {
            continuation.resume(block())
        }
    }
