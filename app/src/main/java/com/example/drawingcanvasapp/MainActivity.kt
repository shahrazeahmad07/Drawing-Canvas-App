package com.example.drawingcanvasapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.drawingcanvasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.setSizeForBrush(10.toFloat())

        binding.ibBrushSize.setOnClickListener {
            showBrushSizeDialogue()
        }
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
}