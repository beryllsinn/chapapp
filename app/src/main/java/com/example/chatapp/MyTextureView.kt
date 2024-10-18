package com.example.chatapp

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.TextureView

class MyTextureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    init {
        // TextureView의 SurfaceTextureListener 설정
        surfaceTextureListener = this
    }

    // TextureView가 사용 가능할 때 호출됩니다.
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        // OpenGL 또는 Canvas 등을 이용해 그리기 작업을 수행합니다.
        val canvas = lockCanvas()
        if (canvas != null) {
            canvas.drawARGB(255, 100, 100, 100) // 회색 배경 그리기
            unlockCanvasAndPost(canvas)
        }
    }

    // TextureView의 크기가 변경될 때 호출됩니다.
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        // 화면 크기에 따라 그리기 내용을 변경할 필요가 있을 경우 처리합니다.
    }

    // TextureView가 파괴될 때 호출됩니다.
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        // 자원 해제 등 필요한 작업을 수행합니다.
        return true
    }

    // TextureView가 업데이트될 때 호출됩니다.
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // 필요 시 화면 갱신 처리를 수행합니다.
    }
}
