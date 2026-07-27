package ink.razure.azusa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import android.app.Activity
import org.json.JSONObject

class AzusaBridge(private val context: Context) {

    @JavascriptInterface
    fun postMessage(action: String, data: String) {
        when (action) {
            "navigate" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                context.startActivity(intent)
            }
            "toast" -> {
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
            }
            "finish" -> {
                if (context is Activity) context.finish()
            }
            "log" -> {
                Log.d("Azusa", data)
            }
        }
    }
}
