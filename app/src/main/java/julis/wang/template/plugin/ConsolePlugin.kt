package julis.wang.template.plugin

import android.util.Log
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Object

/**
 *
 *
 * Created by @juliswang on 2023/09/08 16:23
 *
 * @Description
 */
class ConsolePlugin {
    private val TAG = "ConsolePlugin"
    var v8Console: V8Object? = null

    fun log(message: Any) {
        Log.d(TAG, message.toString())
    }

    fun error(message: Any) {
        Log.d(TAG, message.toString())
    }

    fun warn(message: Any) {
        Log.d(TAG, message.toString())
    }

    fun register(jsRuntime: V8) {
        v8Console = V8Object(jsRuntime)
        v8Console?.let {
            it.registerJavaMethod(this, "log", "log", arrayOf<Class<*>>(Any::class.java))
            it.registerJavaMethod(this, "error", "error", arrayOf<Class<*>>(Any::class.java))
            it.registerJavaMethod(this, "warn", "warn", arrayOf<Class<*>>(Any::class.java))
        }
        v8Console?.setWeak()
        // if there registered name is not "console", eg. "test"
        // in js, call will be use "test.log" or "test.error".
        jsRuntime.add("console", v8Console)
    }

}