package com.example.playlistmaker.player.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Состояния кнопки
    enum class ButtonState {
        PLAY,
        PAUSE
    }

    // Текущее состояние
    private var buttonState = ButtonState.PLAY

    // Изображения для состояний
    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    // Объекты для рисования
    private val paint = Paint()
    private val bitmapRect = RectF()

    // Слушатель нажатий
    private var onClickListener: (() -> Unit)? = null

    init {
        // Получаем атрибуты из XML
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            0, 0
        ).apply {
            try {
                val playDrawableId = getResourceId(R.styleable.PlaybackButtonView_playButtonDrawable, 0)
                val pauseDrawableId = getResourceId(R.styleable.PlaybackButtonView_pauseButtonDrawable, 0)

                if (playDrawableId != 0) {
                    val playDrawable = ContextCompat.getDrawable(context, playDrawableId)
                    playBitmap = playDrawable?.toBitmap()
                }

                if (pauseDrawableId != 0) {
                    val pauseDrawable = ContextCompat.getDrawable(context, pauseDrawableId)
                    pauseBitmap = pauseDrawable?.toBitmap()
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Вычисляем координаты для отрисовки изображения
        val centerX = w / 2f
        val centerY = h / 2f

        // Определяем размер изображения (используем минимальную сторону)
        val size = minOf(w, h).toFloat()

        bitmapRect.set(
            centerX - size / 2,
            centerY - size / 2,
            centerX + size / 2,
            centerY + size / 2
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Выбираем изображение в зависимости от состояния
        val bitmap = when (buttonState) {
            ButtonState.PLAY -> playBitmap
            ButtonState.PAUSE -> pauseBitmap
        }

        // Рисуем изображение
        bitmap?.let {
            canvas.drawBitmap(it, null, bitmapRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                if (isClickInsideBounds(event.x, event.y)) {
                    // Убираем toggleState() - состояние будет управляться извне
                    performClick()
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        onClickListener?.invoke()
        return true
    }

    // Проверка, что клик произошел внутри кнопки
    private fun isClickInsideBounds(x: Float, y: Float): Boolean {
        return x >= 0 && x <= width && y >= 0 && y <= height
    }

    // Переключение состояния
    private fun toggleState() {
        buttonState = when (buttonState) {
            ButtonState.PLAY -> ButtonState.PAUSE
            ButtonState.PAUSE -> ButtonState.PLAY
        }
        invalidate() // Перерисовываем View
    }

    // Публичный метод для установки состояния
    fun setState(state: ButtonState) {
        if (buttonState != state) {
            buttonState = state
            invalidate()
        }
    }

    // Публичный метод для получения текущего состояния
    fun getState(): ButtonState = buttonState

    // Установка слушателя нажатий
    fun setOnButtonClickListener(listener: () -> Unit) {
        this.onClickListener = listener
    }
}