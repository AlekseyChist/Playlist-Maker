package com.example.playlistmaker.player.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
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

    // Drawable для состояний
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

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
                    playDrawable = ContextCompat.getDrawable(context, playDrawableId)
                }

                if (pauseDrawableId != 0) {
                    pauseDrawable = ContextCompat.getDrawable(context, pauseDrawableId)
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Выбираем drawable в зависимости от состояния
        val drawable = when (buttonState) {
            ButtonState.PLAY -> playDrawable
            ButtonState.PAUSE -> pauseDrawable
        }

        // Рисуем drawable
        drawable?.let {
            // Устанавливаем границы для drawable
            val centerX = width / 2
            val centerY = height / 2
            val size = minOf(width, height)

            it.setBounds(
                centerX - size / 2,
                centerY - size / 2,
                centerX + size / 2,
                centerY + size / 2
            )
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }
            MotionEvent.ACTION_UP -> {
                if (isClickInsideBounds(event.x, event.y)) {
                    performClick()
                    true
                } else {
                    super.onTouchEvent(event)
                }
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

    // Очистка слушателя нажатий - ДОБАВЛЕННАЯ ФУНКЦИЯ
    fun removeOnButtonClickListener() {
        this.onClickListener = null
    }
}