package ink.razure.azusa

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        val bridge = AzusaBridge(this)
        webView.addJavascriptInterface(bridge, "Azusa")

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = false
            allowContentAccess = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectHijackScript(view)
            }
        }

        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    private fun injectHijackScript(view: WebView) {
        val script = """
            (function() {
              if (window.__azusa_hijacked__) return;
              window.__azusa_hijacked__ = true;
              document.addEventListener('click', function(e) {
                var el = e.target;
                var action, data;
                while (el && el !== document) {
                  if (el.hasAttribute('data-azusa-action')) {
                    action = el.getAttribute('data-azusa-action');
                    data = el.getAttribute('data-azusa-data') || '';
                    break;
                  }
                  el = el.parentElement;
                }
                if (!action) {
                  var tag = e.target.tagName;
                  if (tag === 'BUTTON' || tag === 'A') {
                    action = 'navigate';
                    data = e.target.href || e.target.innerText || '';
                  }
                }
                if (action) {
                  e.preventDefault();
                  e.stopPropagation();
                  Azusa.postMessage(action, data);
                }
              }, true);
            })();
        """.trimIndent()
        view.evaluateJavascript(script, null)
    }
}
