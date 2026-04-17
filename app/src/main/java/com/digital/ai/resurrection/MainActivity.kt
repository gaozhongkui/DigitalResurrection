package com.digital.ai.resurrection

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.digital.ai.resurrection.ui.theme.DigitalResurrectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalResurrectionTheme {
                ArViewerScreen()
            }
        }
    }
}

@Composable
fun ArViewerScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            // 将 assets 目录映射到 https://appassets.androidplatform.net/assets/
            // 使 CDN 加载的 model-viewer 脚本可以无 CORS 问题地访问本地 GLB 文件
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()

            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false          // 使用 assetLoader 替代直接文件访问
                    mediaPlaybackRequiresUserGesture = false
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest
                    ): WebResourceResponse? {
                        return assetLoader.shouldInterceptRequest(request.url)
                    }
                }

                // 通过 HTTPS 伪地址加载，避免混合内容问题
                loadUrl("https://appassets.androidplatform.net/assets/ar_viewer.html")
            }
        }
    )
}
