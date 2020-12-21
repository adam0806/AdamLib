package com.adam.lib.safe

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adam.lib.R

/**
 * Created By Adam on 2020/8/24
 */
class ErrorActivity : AppCompatActivity() {
    lateinit var tv:TextView
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_error)
        tv = findViewById<TextView>(R.id.tv_error)
        var text = intent.getStringExtra("text");

        if(text != null && text.length>0) {
            tv.text = text
        }else{
            tv.text =  intent.extras!!.getString("text")
        }
        tv.setOnClickListener{
            var clipBoard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoard?.setPrimaryClip(ClipData.newPlainText("text", tv.text))
            Toast.makeText(this, "Copy Success!", Toast.LENGTH_SHORT).show();
        }
    }
}