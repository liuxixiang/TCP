package com.fd.tcp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.dushu.tcp.app.TCPManager
import com.dushu.tcp.msg.MessageProcessor
import com.dushu.tcp.msg.TCPMessage
import com.dushu.tcp.msg.TCPMessageHead
import java.util.*

class MainActivity : AppCompatActivity() {
    var userId = "100002"
    var token = "token_$userId"
    var hosts = "[{\"host\":\"192.168.37.80\", \"port\":\"30000\"}]"
    var etContent: EditText? = null
    var mTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etContent = findViewById(R.id.etContent)
        mTextView = findViewById(R.id.tv_msg)
        TCPManager.INSTANCE.init(userId, token, hosts, 1)
        findViewById<Button>(R.id.send).setOnClickListener {
            sendMsg()
        }
    }

    fun sendMsg() {
        val header = TCPMessageHead()
        header.messageName = "_sendSms"
        header.seq = 1
        header.version = 1
        header.contentType = 0
        val message = TCPMessage(header)
        MessageProcessor.INSTANCE.sendMsg(message)
    }
}