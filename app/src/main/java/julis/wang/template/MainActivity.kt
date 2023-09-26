package julis.wang.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        J2V8Helper.init(this, "example.js", "libExample")
    }

    private fun initView() {
        findViewById<Button>(R.id.btn_string).setOnClickListener(this)
        findViewById<Button>(R.id.btn_2String).setOnClickListener(this)
        findViewById<Button>(R.id.btn_obj).setOnClickListener(this)
        findViewById<Button>(R.id.btn_promise).setOnClickListener(this)
        findViewById<Button>(R.id.btn_nested_promise).setOnClickListener(this)
        findViewById<Button>(R.id.btn_console).setOnClickListener(this)
        findViewById<Button>(R.id.btn_set_time_out).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        simpleFunc(v, "simpleFunc")
        complexFunc(v, "complexFunc")
    }

    private fun simpleFunc(v: View?, moduleName: String) {
        var funName: String? = null
        val result: Any?
        val params = mutableListOf<Any>()
        when (v?.id) {
            R.id.btn_string -> {
                funName = "testString"
                params.add("hello world")
            }
            R.id.btn_2String -> {
                funName = "test2String"
                params.add("hello")
                params.add("world")
            }
            R.id.btn_obj -> {
                funName = "testObj"
                val event = TestObj(
                    id = "id1234",
                    info = Info(code = 29, version = "1.0.0")
                )
                params.add(Gson().toJson(event))
            }
        }
        // ensure that "complexFunc" can be called...
        if (funName == null) {
            return
        }
        result = J2V8Helper.runJS(moduleName, funName, params)
        Toast.makeText(this, "Result:$result", Toast.LENGTH_SHORT).show()
    }

    private fun complexFunc(v: View?, moduleName: String) {
        val params = mutableListOf<Any>()

        when (v?.id) {
            R.id.btn_promise -> {
                J2V8Helper.runJSPromise(moduleName, "testPromise", params, object : J2V8Helper.OnPromise {
                    override fun onResolve(result: Any) {
                        Toast.makeText(this@MainActivity, "Run [testPromise] Result:\n$result", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReject(result: Any) {
                    }
                })
            }
            R.id.btn_nested_promise -> {
                J2V8Helper.runJSPromise(moduleName, "testNestedPromise", params, object : J2V8Helper.OnPromise {
                    override fun onResolve(result: Any) {
                        Toast.makeText(this@MainActivity, "Run [testNestedPromise]:\n$result", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReject(result: Any) {
                    }
                })
            }
            R.id.btn_console -> {
                J2V8Helper.runJS(moduleName, "testConsoleLog", params)
                Toast.makeText(this@MainActivity, "See your Android Studio's Logcat, maybe printed sth.", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.btn_set_time_out -> {
                Toast.makeText(
                    this@MainActivity,
                    "Wait for a moment...", Toast.LENGTH_SHORT
                ).show()
                J2V8Helper.runJSPromise(moduleName, "testTimeOut", params, object : J2V8Helper.OnPromise {
                    override fun onResolve(result: Any) {
                        Toast.makeText(this@MainActivity, "Run [testTimeOut]:\n$result", Toast.LENGTH_LONG).show()
                    }

                    override fun onReject(result: Any) {
                    }
                })
            }
        }
    }
}