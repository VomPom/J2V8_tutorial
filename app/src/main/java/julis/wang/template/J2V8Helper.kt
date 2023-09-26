package julis.wang.template

import android.content.Context
import android.os.Looper
import android.util.Log
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import julis.wang.template.plugin.ConsolePlugin
import julis.wang.template.plugin.SetTimeOutPlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch

/**
 * Created by @juliswang on 2023/09/25 19:38
 *
 * @Description j2v8 helper.
 */
object J2V8Helper {
    private const val TAG = "J2V8Helper"
    var taskLock: CountDownLatch? = null
    private var jsRuntime = V8.createV8Runtime()
    private var jsContentBuffer: String = ""
    private var jsLibName: String = ""

    /**
     * init J2V8 engine and loads the required javascript code.
     *
     * @param context    Activity context.
     * @param fileName   target javascript file name in assets dir.
     * @param libName    webpack packed library's name
     */
    fun init(context: Context, fileName: String, libName: String) {
        val assetManager = context.assets
        val inputStream = assetManager.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        jsContentBuffer = reader.readText()
        this.jsLibName = libName
    }

    fun runJS(moduleName: String, functionName: String, args: List<Any>): Any? {
        val jsLibrary = createJSModule(moduleName)
        val parameters = createV8Params(args)
        var result: Any? = null
        try {
            result = jsLibrary?.executeFunction(functionName, parameters)
        } catch (e: Exception) {
            Log.e(TAG, "js run exception,error, functionName:$functionName\n stack:${e.stackTraceToString()}")
        }
        if ((result is V8Object) && !result.isUndefined && result.constructorName.equals("Promise")) {
            result.close()
            throw RuntimeException(
                "The function [$functionName] in $moduleName's return type is Promise, please use [runJSPromise] instead of [runJs]."
            )
        }
        jsLibrary?.close()
        parameters.close()
        jsRuntime.close()
        return result
    }

    fun runJSPromise(moduleName: String, functionName: String, args: List<Any>, listener: OnPromise) {
        Thread {
            taskLock = CountDownLatch(1)
            val jsLibrary = createJSModule(moduleName)

            val v8Array = createV8Params(args)
            val jsPromise = jsLibrary?.executeFunction(functionName, v8Array) as V8Object
            jsLibrary.close()
            v8Array.close()

            val onResolve = V8Function(jsRuntime) { receiver, parameters ->
                //FIXME:: simple use index [0], need complete...
                val result = parameters[0].toString()
                android.os.Handler(Looper.getMainLooper()).post {
                    listener.onResolve(result)
                }
                parameters.close()
                receiver.close()
                taskLock?.countDown()
            }
            val onReject = V8Function(jsRuntime) { receiver, parameters ->
                //FIXME:: simple use index [0], can complete...
                val result = parameters[0].toString()
                android.os.Handler(Looper.getMainLooper()).post {
                    listener.onReject(result)
                }
                parameters.close()
                receiver.close()
                taskLock?.countDown()
            }

            jsPromise.apply {
                val onResolveParameter = V8Array(jsRuntime).push(onResolve)
                val onRejectParameter = V8Array(jsRuntime).push(onResolve)
                executeVoidFunction("then", onResolveParameter)
                executeVoidFunction("catch", onRejectParameter)
                onRejectParameter.close()
                onResolveParameter.close()
                close()
            }
            taskLock?.await()
            onResolve.close()
            onReject.close()
            jsRuntime.close()
        }.start()
    }


    /**
     * Create module which can execute js method.
     *
     * @param moduleName which module exports' name
     * @return  return lib can run js' method.
     */
    private fun createJSModule(moduleName: String): V8Object? {
        var jsModule: V8Object? = null
        try {
            jsRuntime = V8.createV8Runtime()
            jsRuntime.executeVoidScript(jsContentBuffer)
            val rootLib = jsRuntime.getObject(this.jsLibName);
            jsModule = rootLib.getObject(moduleName)
            simpleNativePlugin()
            rootLib.close()
        } catch (e: Exception) {
            Log.e(TAG, "Create js runtime error:" + e.stackTraceToString())
        }
        return jsModule
    }

    /**
     * must be ensured that the same runtime is used when executing the method and initializing the parameters.
     *
     * @param args
     * @return
     */
    private fun createV8Params(args: List<Any>): V8Array {
        val v8Array = V8Array(jsRuntime)
        args.forEach { v8Array.push(it) }
        return v8Array
    }

    /**
     * sample register "console.log" native plugin.
     */
    private fun simpleNativePlugin() {
        ConsolePlugin().register(jsRuntime)
        SetTimeOutPlugin().register(jsRuntime)
        // u can add more native plugins at there.
    }

    interface OnPromise {
        fun onResolve(result: Any)

        fun onReject(result: Any)
    }
}