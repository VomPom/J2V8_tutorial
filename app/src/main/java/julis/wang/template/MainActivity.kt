package julis.wang.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
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
        // ensure that "complexFunc" can be called.
        if (funName == null) {
            return
        }
        result = J2V8Helper.runJS(moduleName, funName, params)
        Toast.makeText(this, "Result:$result", Toast.LENGTH_SHORT).show()
    }

    private fun complexFunc(v: View?, moduleName: String) {
        var funName = ""
        val params = mutableListOf<Any>()

        when (v?.id) {
            R.id.btn_promise -> {
                funName = "testPromise"
                J2V8Helper.runJSPromise(moduleName, funName, params, object : J2V8Helper.OnPromise {
                    override fun onResolve(result: Any) {
                        Toast.makeText(this@MainActivity, "Run [testPromise] Result:\n$result", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReject(result: Any) {
                    }
                })
            }
            R.id.btn_nested_promise -> {
                funName = "testNestedPromise"
                J2V8Helper.runJSPromise(moduleName, funName, params, object : J2V8Helper.OnPromise {
                    override fun onResolve(result: Any) {
                        Toast.makeText(this@MainActivity, "Run [testNestedPromise]:\n$result", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReject(result: Any) {
                    }
                })
            }
            R.id.btn_console -> {
                funName = "testConsoleLog"
                J2V8Helper.runJS(moduleName, funName, params)
                Toast.makeText(this@MainActivity, "See your Android Studio's Logcat, maybe printed sth.", Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.btn_set_time_out -> {
                funName = "testTimeOut"
                Toast.makeText(
                    this@MainActivity,
                    "[testTimeOut] has not been implemented yet.", Toast.LENGTH_LONG
                ).show()
                //TODO:: The implementation method is the same as ConsolePlugin.
            }
        }
    }
}