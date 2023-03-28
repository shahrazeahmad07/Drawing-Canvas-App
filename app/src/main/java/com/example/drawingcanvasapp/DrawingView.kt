package com.example.drawingcanvasapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet) : View(context, attr) {

    private var drawPaint: Paint = Paint()
    private var currentBrushSize: Float = toPixel(10f)
    private var currentEraserSize: Float = toPixel(10f)

    private lateinit var backgroundBitmap : Bitmap
    private lateinit var viewBitmap : Bitmap
//    private val canvasBackgroundColor: Int = Color.WHITE
    private lateinit var canvas : Canvas

    private val drawPath: Path = Path()

    private val eachActionBitmapList: ArrayList<Bitmap> = ArrayList()
    private val undoActionBitmapList: ArrayList<Bitmap> = ArrayList()


    //! only set the paint type and style here
    init {
        drawPaint.color = Color.BLACK
        drawPaint.isAntiAlias = true
        drawPaint.isDither = true
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeCap = Paint.Cap.ROUND
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeWidth = currentBrushSize
    }

    //! creates bitmap and canvas according to screen size
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        viewBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(viewBitmap)
    }

    //! controls drawing
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawColor(canvasBackgroundColor)
        canvas?.drawBitmap(backgroundBitmap, 0f, 0f, null)
        canvas?.drawBitmap(viewBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchX != null && touchY != null) {
//                    drawPaint.strokeWidth = currentBrushSize
//                    drawPaint.color = currentColor
                    drawPath.moveTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
                canvas.drawPath(drawPath, drawPaint)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                eachActionBitmapList.add(getBitMap())
                undoActionBitmapList.clear()
                drawPath.reset()
            }
        }
        return true
    }

    private fun getBitMap(): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
//        this.isDrawingCacheEnabled = true
//        this.buildDrawingCache()
//        val bitmap: Bitmap = Bitmap.createBitmap(this.getDrawingCache())
//        this.isDrawingCacheEnabled = false
//        return bitmap
    }


    //! function that sets new size
    fun setSizeForBrush(newSize: Float) {
        disableEraser()
        currentBrushSize = toPixel(newSize)
        drawPaint.strokeWidth = currentBrushSize
    }

    //! function that set new color
    fun setColor(newColor: String) {
        disableEraser()
        drawPaint.strokeWidth = currentBrushSize
        drawPaint.color = Color.parseColor(newColor)
    }

    //! Eraser feature
    fun onEraserSelect(newSize: Float) {
        currentEraserSize = toPixel(newSize)
        drawPaint.strokeWidth = currentEraserSize
        drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    //! disables eraser and convert drawPaint into brush
    private fun disableEraser() {
        drawPaint.xfermode = null
        drawPaint.shader = null
        drawPaint.maskFilter = null
    }

    //! undo ker raha last drawing
    fun onClickUndo() {
        if (eachActionBitmapList.isNotEmpty()) {
            undoActionBitmapList.add(eachActionBitmapList.removeAt(eachActionBitmapList.lastIndex))
            viewBitmap = if (eachActionBitmapList.isNotEmpty()) {
                eachActionBitmapList[eachActionBitmapList.lastIndex]
            } else {
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }
            canvas = Canvas(viewBitmap)
            invalidate()
        }
    }

    //! redo ker raha last undo kya wa
    fun onClickRedo() {
        if (undoActionBitmapList.isNotEmpty()) {
            eachActionBitmapList.add(undoActionBitmapList.removeAt(undoActionBitmapList.lastIndex))
            viewBitmap = eachActionBitmapList[eachActionBitmapList.lastIndex]
            canvas = Canvas(viewBitmap)
            invalidate()
        }
    }

    //! converts float value to pixel value
    private fun toPixel(brushSize: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, brushSize, resources.displayMetrics)
    }

}

//! Old Paint Widget!


//package com.example.drawingcanvasapp
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Path
//import android.util.AttributeSet
//import android.util.TypedValue
//import android.view.MotionEvent
//import android.view.View
//
//class DrawingView(context: Context, attr: AttributeSet) : View(context, attr) {
//    private lateinit var drawPaint: Paint
//    private lateinit var drawPath: CustomPath
//    private var color: Int = Color.BLACK
//    private var brushSize: Float = 0.toFloat()
//    private lateinit var canvasBitmap: Bitmap
//    private lateinit var canvas: Canvas
//    private lateinit var canvasPaint: Paint
//
//    private val paths = ArrayList<CustomPath>()
//    private val undoPaths = ArrayList<CustomPath>()
//
//    init {
//        setUpDrawing()
//    }
//
//    //! bta rahe k style kya ho ga draw kerne ka bus with using drawPaint variable.
//    private fun setUpDrawing() {
//        drawPaint = Paint()
//        drawPath = CustomPath(color, brushSize)
//        canvasPaint = Paint(Paint.DITHER_FLAG)
//        drawPaint.style = Paint.Style.STROKE
//        drawPaint.strokeJoin = Paint.Join.ROUND
//        drawPaint.strokeCap = Paint.Cap.ROUND
////        brushSize = 10.toFloat()
//    }
//
//    //! canvas set ker rahe bus bitmap create kerte apna or wo canvas main pas ker dete k ye hai hamara bitmap
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
//        canvas = Canvas(canvasBitmap)
//    }
//
//    //! ye method draw kerwata, us paint ko jo hum ne uper design btaya jis ka, us path per jo hum
//    // touch event se la rahe onTouchEvent method main se
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        canvas?.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
//
//        for (path in paths) {
//            drawPaint.strokeWidth = path.brushThickness
//            drawPaint.color = path.color
//            canvas?.drawPath(path, drawPaint)
//        }
//
//        drawPaint.strokeWidth = drawPath.brushThickness
//        drawPaint.color = drawPath.color
//        canvas?.drawPath(drawPath, drawPaint)
//    }
//
//    //! identify ker raha k kis kis path per touch kya user nai or wo path ko mention ker raha bus
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val touchX = event?.x
//        val touchY = event?.y
//
//        when(event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                drawPath.color = color
//                drawPath.brushThickness = brushSize
//                drawPath.reset()
//                if (touchX != null) {
//                    if (touchY != null) {
//                        drawPath.moveTo(touchX, touchY)
//                    }
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (touchX != null) {
//                    if (touchY != null) {
//                        drawPath.lineTo(touchX, touchY)
//                    }
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                paths.add(drawPath)
//                undoPaths.clear()
//                drawPath = CustomPath(color, brushSize)
//            }
//            else -> return false
//        }
//        invalidate()
//        return true
//    }
//
//    //! function that sets new size
//    fun setSizeForBrush(newSize: Float) {
//        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
//    }
//
//    //! function that set new color
//    fun setColor(newColor: String) {
//        val color = Color.parseColor(newColor)
//        this.color = color
//    }
//
//    //! undo ker raha last drawing
//    fun onClickUndo() {
//        if (paths.size > 0) {
//            undoPaths.add(paths.removeAt(paths.size -1))
//            //! this calls an internal method again internally
//            invalidate()
//        }
//    }
//
//    //! redo ker raha last undo kya wa
//    fun onClickRedo() {
//        if (undoPaths.size > 0) {
//            paths.add(undoPaths.removeAt(undoPaths.size -1))
//            invalidate()
//        }
//    }
//
//    //! custom path class that has color and thickness properties which we are going to use to set
//    // paint properties
//    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {
//
//    }
//
//}