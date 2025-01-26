package lamdoan.chatting

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class BubbleTouchListener(
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val bubbleView: View,
    private val screenHeight: Int, // Chiều cao màn hình
    private val onBubbleRemoved: () -> Unit // Hàm callback khi bong bóng bị xóa
) : View.OnTouchListener {
    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = layoutParams.x
                initialY = layoutParams.y
                touchX = event.rawX
                touchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                layoutParams.x = initialX + (event.rawX - touchX).toInt()
                layoutParams.y = initialY + (event.rawY - touchY).toInt()
                windowManager.updateViewLayout(bubbleView, layoutParams)
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Kiểm tra nếu bong bóng chạm đáy màn hình
                if (layoutParams.y + bubbleView.height >= screenHeight) {
                    onBubbleRemoved()
                }
                return true
            }
        }
        return false
    }
}
