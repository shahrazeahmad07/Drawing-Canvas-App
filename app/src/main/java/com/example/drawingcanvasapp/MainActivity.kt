package com.example.drawingcanvasapp

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.drawingcanvasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ibCurrentPaint: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setting default brush size
        binding.drawingView.setSizeForBrush(10.toFloat())

        // Brush size picker
        binding.ibBrushSize.setOnClickListener {
            showBrushSizeDialogue()
        }

        val layout = binding.llColorPallet
        ibCurrentPaint = layout[1] as ImageButton
        ibCurrentPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.layout_pallet_pressed))
    }

    private fun showBrushSizeDialogue() {
        val brushDialogue = Dialog(this)
        brushDialogue.setContentView(R.layout.dialogue_brush_size)
        brushDialogue.setTitle("Choose Brush Size: ")
        brushDialogue.findViewById<ImageButton>(R.id.ibSmall).setOnClickListener {
            binding.drawingView.setSizeForBrush(5.toFloat())
            brushDialogue.dismiss()
        }
        brushDialogue.findViewById<ImageButton>(R.id.ibMedium).setOnClickListener {
            binding.drawingView.setSizeForBrush(10.toFloat())
            brushDialogue.dismiss()
        }
        brushDialogue.findViewById<ImageButton>(R.id.iblarge).setOnClickListener {
            binding.drawingView.setSizeForBrush(15.toFloat())
            brushDialogue.dismiss()
        }
        brushDialogue.show()
    }

    fun onColorSelect(view: View) {
        val ibSelectedColor = view as ImageButton
        val color = ibSelectedColor.tag.toString()
        binding.drawingView.setColor(color)
        ibSelectedColor.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.layout_pallet_pressed))
        ibCurrentPaint.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.layout_pallet_normal))
        ibCurrentPaint = view
    }
}