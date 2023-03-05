package com.example.drawingcanvasapp

import android.app.Notification.Action
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet) : View(context, attr) {
    private lateinit var drawPaint: Paint
    private lateinit var drawPath: CustomPath
    private var color: Int = Color.BLACK
    private var brushSize: Float = 0.toFloat()
    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var canvasPaint: Paint

    private val paths = ArrayList<CustomPath>()
    private val undoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    //! bta rahe k style kya ho ga draw kerne ka bus with using drawPaint variable.
    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushSize)
        canvasPaint = Paint(Paint.DITHER_FLAG)
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
//        brushSize = 10.toFloat()
    }

    //! canvas set ker rahe bus bitmap create kerte apna or wo canvas main pas ker dete k ye hai hamara bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    //! ye method draw kerwata, us paint ko jo hum ne uper design btaya jis ka, us path per jo hum
    // touch event se la rahe onTouchEvent method main se
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)

        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas?.drawPath(path, drawPaint)
        }

        drawPaint.strokeWidth = drawPath.brushThickness
        drawPaint.color = drawPath.color
        canvas?.drawPath(drawPath, drawPaint)
    }

    //! identify ker raha k kis kis path per touch kya user nai or wo path ko mention ker raha bus
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushThickness = brushSize
                drawPath.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath)
                undoPaths.clear()
                drawPath = CustomPath(color, brushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    //! function that sets new size
    fun setSizeForBrush(newSize: Float) {
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
    }

    //! function that set new color
    fun setColor(newColor: String) {
        val color = Color.parseColor(newColor)
        this.color = color
    }

    //! undo ker raha last drawing
    fun onClickUndo() {
        if (paths.size > 0) {
            undoPaths.add(paths.removeAt(paths.size -1))
            //! this calls an internal method again internally
            invalidate()
        }
    }

    //! redo ker raha last undo kya wa
    fun onClickRedo() {
        if (undoPaths.size > 0) {
            paths.add(undoPaths.removeAt(undoPaths.size -1))
            invalidate()
        }
    }

    //! custom path class that has color and thickness properties which we are going to use to set
    // paint properties
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }

}