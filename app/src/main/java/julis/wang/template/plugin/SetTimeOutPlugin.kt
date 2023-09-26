package julis.wang.template.plugin

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Function

/**
 * Created by @juliswang on 2023/09/26 10:13
 *
 * @Description
 */
class SetTimeOutPlugin {
    private val TAG = "SetTimeOutPlugin"

    fun register(jsRuntime: V8) {
        jsRuntime.registerJavaMethod({ receiver, args ->
            val function = args.get(0) as V8Function
            val delayTimeMs = args.get(1) as Int
            try {
                Thread.sleep(delayTimeMs.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            receiver.close()
            args.close()
            function.call(null, null)
            function.close()
        }, "setTimeout")

    }
}