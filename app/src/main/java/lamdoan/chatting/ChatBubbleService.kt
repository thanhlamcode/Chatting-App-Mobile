package lamdoan.chatting

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import lamdoan.chatting.BubbleTouchListener
import lamdoan.chatting.R

class ChatBubbleService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private var params: WindowManager.LayoutParams? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val avatarUrl = intent?.getStringExtra("avatarUrl")
        val userName = intent?.getStringExtra("userName") ?: "Người dùng"

        // Tạo layout bong bóng
        bubbleView = LayoutInflater.from(this).inflate(R.layout.chat_bubble, null)
        val avatarImageView = bubbleView.findViewById<ImageView>(R.id.avatarBubble)

        Glide.with(this).load(avatarUrl).into(avatarImageView)

        // Cấu hình LayoutParams
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // Gắn BubbleTouchListener với callback xóa
        bubbleView.setOnTouchListener(BubbleTouchListener(windowManager, params!!, bubbleView, screenHeight) {
            // Xóa bong bóng khi kéo xuống đáy
            stopSelf()
        })

        windowManager.addView(bubbleView, params)
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(bubbleView)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
