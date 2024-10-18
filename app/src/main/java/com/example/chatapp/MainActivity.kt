package com.example.chatapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechSettings
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import missing.namespace.R

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        startButton = findViewById(R.id.startButton)
        resultTextView = findViewById(R.id.resultTextView)

        // 권한 확인 및 요청
        checkAudioPermission()

        // 버튼 클릭 시 음성 인식 시작
        startButton.setOnClickListener {
            startSpeechRecognition()
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    private fun startSpeechRecognition() {
        // 음성 인식을 위한 코드 작성 (Google API와 연결)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // res/raw/credential.json 파일 사용
                val credentialsStream = resources.openRawResource(R.raw.credential)
                val credentials = GoogleCredentials.fromStream(credentialsStream)

                // SpeechSettings 사용하여 SpeechClient 생성
                val speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
                val speechClient = SpeechClient.create(speechSettings)

                // 오디오 파일을 텍스트로 변환
                val audioBytes = ByteString.readFrom(resources.openRawResource(R.raw.audio_sample))
                val audio = RecognitionAudio.newBuilder().setContent(audioBytes).build()
                val config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build()

                val response = speechClient.recognize(config, audio)
                for (result in response.resultsList) {
                    runOnUiThread {
                        resultTextView.text = result.alternativesList[0].transcript
                    }
                }

                speechClient.close()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    resultTextView.text = "오류 발생: ${e.message}"
                }
            }
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 승인됨
                startSpeechRecognition()
            } else {
                // 권한 거부됨
                resultTextView.text = "오디오 권한이 필요합니다."
            }
        }
    }
}
