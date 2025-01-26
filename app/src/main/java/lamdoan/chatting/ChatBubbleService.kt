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
import com.bumptech.glide.Glide

class ChatBubbleService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private var params: WindowManager.LayoutParams? = null
    private var isBubbleAdded = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isBubbleAdded) {
            return START_STICKY
        }

        val avatarUrl = intent?.getStringExtra("avatarUrl")

        // Tạo layout bong bóng
        bubbleView = LayoutInflater.from(this).inflate(R.layout.chat_bubble, null)
        val avatarImageView = bubbleView.findViewById<ImageView>(R.id.avatarBubble)

        // Hiển thị avatar hoặc hình mặc định
        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .circleCrop() // Hiệu ứng hình tròn
                .into(avatarImageView)
        } else {
            avatarImageView.setImageResource(R.drawable.ic_placeholder)
        }

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

        // Khởi tạo WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels

        // Gắn BubbleTouchListener
        bubbleView.setOnTouchListener(
            BubbleTouchListener(windowManager, params!!, bubbleView, screenHeight) {
                stopSelf() // Xóa bong bóng khi kéo xuống đáy
            }
        )

        windowManager.addView(bubbleView, params)
        isBubbleAdded = true

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bubbleView.isInitialized) {
            windowManager.removeView(bubbleView)
        }
        isBubbleAdded = false
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
