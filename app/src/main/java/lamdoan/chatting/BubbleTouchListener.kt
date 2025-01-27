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
    private var isMoving = false // Cờ để kiểm tra trạng thái di chuyển

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Ghi nhận vị trí ban đầu
                initialX = layoutParams.x
                initialY = layoutParams.y
                touchX = event.rawX
                touchY = event.rawY
                isMoving = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Tính toán khoảng cách di chuyển
                val deltaX = event.rawX - touchX
                val deltaY = event.rawY - touchY

                // Cập nhật vị trí bong bóng
                layoutParams.x = initialX + deltaX.toInt()
                layoutParams.y = initialY + deltaY.toInt()
                windowManager.updateViewLayout(bubbleView, layoutParams)
                isMoving = true
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Kiểm tra nếu bong bóng được kéo xuống đáy màn hình
                if (layoutParams.y + bubbleView.height >= screenHeight) {
                    onBubbleRemoved() // Gọi callback xóa bong bóng
                } else if (!isMoving) {
                    // Nếu không di chuyển, xử lý sự kiện click (nếu cần)
                    v?.performClick()
                }
                return true
            }
        }
        return false
    }
}
