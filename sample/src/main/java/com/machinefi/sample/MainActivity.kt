package com.machinefi.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.machinefi.w3bstream.W3bStream
import com.machinefi.w3bstream.repository.network.HttpService
import com.machinefi.w3bstream.repository.network.request.Event
import com.machinefi.w3bstream.repository.network.request.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64

const val KEY_HISTORY = "key_history"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnUpload.setOnClickListener {
            upload()
        }

        mTvHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun upload() {
        val host = mEtHost.text.toString().trim()
        val projectName = mEtProjectName.text.toString().trim()
        val eventType = mEtType.text.toString().trim()
        val payload = mEtContent.text.toString()
        val publisherKey = mEtPublisherKey.text.toString()
        val publisherToken = mEtPublisherToken.text.toString()
        if (host.isBlank()) {
            Toast.makeText(this, "Host can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (projectName.isBlank()) {
            Toast.makeText(this, "Project name can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (payload.isBlank()) {
            Toast.makeText(this, "Data can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (eventType.isBlank()) {
            Toast.makeText(this, "Event type can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (publisherKey.isBlank()) {
            Toast.makeText(this, "PublisherKey can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (publisherToken.isBlank()) {
            Toast.makeText(this, "PublisherToken can not be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val loading = LoadingDialog(this@MainActivity).apply {
            show()
        }
        val w3bStream = W3bStream.build(HttpService(host, projectName))
        val errorHandler = CoroutineExceptionHandler{ _, e ->
            e.printStackTrace()
            loading.dismiss()
        }
        lifecycleScope.launch(errorHandler) {
            val eventId = System.currentTimeMillis().toString()
            val pubTime = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO) {
                val encodedPayload = Base64.getEncoder().encodeToString(payload.toByteArray())
                val event = Event(Header(eventId, eventType, publisherKey, pubTime, publisherToken), encodedPayload)
                w3bStream.publishEvents(listOf(event))
            }
            loading.dismiss()
            val json = Gson().toJson(response)
            val historyList = SPUtils.getInstance().getStringSet(KEY_HISTORY).toMutableList()
            historyList.add(json)
            SPUtils.getInstance().put(KEY_HISTORY, historyList.toSet())
            mJsonViewer.bindJson(json)
        }
    }
}