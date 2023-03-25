package com.example.drawingcanvasapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.drawingcanvasapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ibCurrentPaint: ImageButton

    //! opening gallery.
    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            //! setting background image which the user selected
            binding.ivBackgroundImage.setImageURI(it.data?.data)
        }
    }

    //! permission request Launcher
    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.entries.forEach { permission ->
                val permissionName = permission.key
                val isGranted = permission.value
                if (isGranted) {
                    //! to be done after a certain permission access is granted
                    // when is checking which permission is granted
                    when(permissionName) {
                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            showGalleryOpenDialog()
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting default brush size
        binding.drawingView.setSizeForBrush(10.toFloat())

        //! setting default color pallet
        val layout = binding.llColorPallet
        ibCurrentPaint = layout[1] as ImageButton
        ibCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.layout_pallet_pressed
            )
        )

        // Brush size picker
        binding.ibBrushSize.setOnClickListener {
            showBrushSizeDialogue()
        }

        //! background image select button
        binding.ibGallery.setOnClickListener {
            requestStoragePermission()
        }

        //! undo button
        binding.ibUndo.setOnClickListener {
            binding.drawingView.onClickUndo()
        }

        //! redo button
        binding.ibRedo.setOnClickListener {
            binding.drawingView.onClickRedo()
        }
    }

    //! this dialog will appear when user click on background Image button
    private fun showGalleryOpenDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select Background Image from Gallery")
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Open") { _, _ ->
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            openGalleryLauncher.launch(intent)
        }
        builder.create().show()
    }

    //! this function gets storage permission
    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog()
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    //! this function is called in brush button to change brush size
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

    //! this function is called in color pallet views using onClick xml feature
    fun onColorSelect(view: View) {
        val ibSelectedColor = view as ImageButton
        val color = ibSelectedColor.tag.toString()
        binding.drawingView.setColor(color)
        ibSelectedColor.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.layout_pallet_pressed
            )
        )
        ibCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.layout_pallet_normal
            )
        )
        ibCurrentPaint = view
    }

    //! to show that read permission is required which the user has denied already
    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("External Storage Permission is Required.")
        builder.setMessage("Open Settings and allow storage permission for this app.")
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.create().show()

    }
}