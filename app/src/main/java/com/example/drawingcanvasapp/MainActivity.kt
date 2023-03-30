package com.example.drawingcanvasapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.drawingcanvasapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ibCurrentPaint: ImageButton

    private lateinit var customDialog: Dialog

    //! opening gallery.
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
                    when (permissionName) {
                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            // do nothing
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            // do Nothing
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

        //! initializing custom Dialog
        customDialog = Dialog(this)
        customDialog.setContentView(R.layout.custom_progress_dialog)
        customDialog.setCancelable(false)

        // Brush size picker
        binding.ibBrushSize.setOnClickListener {
            showBrushSizeDialogue()
        }

        //! undo button
        binding.ibUndo.setOnClickListener {
            binding.drawingView.onClickUndo()
        }

        //! redo button
        binding.ibRedo.setOnClickListener {
            binding.drawingView.onClickRedo()
        }

        //! background image select button
        binding.ibGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showGalleryOpenDialog()
            } else {
                requestReadPermission()
            }
        }

        //! save drawing button
        binding.ibSave.setOnClickListener {
            val isReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val isWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            if (!isReadPermission && !isWritePermission) {
                requestStoragePermission2()
            } else {
                customDialog.show()
                lifecycleScope.launch {
                    val myDrawingBitmap = getBitmapFromView(binding.flDrawingContainer)
                    if (savePhotoToExternalStorage(myDrawingBitmap)) {
                        customDialog.dismiss()
                        Snackbar.make(it, "Drawing Saved Successfully", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //! eraser button
        binding.ibEraser.setOnClickListener{
            showEraserSizeDialogue()
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

    //! this function is called in eraser button to change eraser size and set eraser too!
    private fun showEraserSizeDialogue() {
        val brushDialogue = Dialog(this)
        brushDialogue.setContentView(R.layout.dialogue_eraser_size)
        brushDialogue.setTitle("Choose Eraser Size: ")
        brushDialogue.findViewById<ImageButton>(R.id.ibSmall).setOnClickListener {
            binding.drawingView.onEraserSelect(5.toFloat())
            brushDialogue.dismiss()
        }
        brushDialogue.findViewById<ImageButton>(R.id.ibMedium).setOnClickListener {
            binding.drawingView.onEraserSelect(15.toFloat())
            brushDialogue.dismiss()
        }
        brushDialogue.findViewById<ImageButton>(R.id.iblarge).setOnClickListener {
            binding.drawingView.onEraserSelect(25.toFloat())
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


    //! this function gets storage permission
    private fun requestReadPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog()
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
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

    private fun requestStoragePermission2() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog()
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    //! creating bitmap i.e., image of the drawing!
    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    //! save Image in Pictures folder
    private fun savePhotoToExternalStorage(bmp : Bitmap?) : Boolean{
        val imageCollection : Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, File.separator + "DrawingApp_" + System.currentTimeMillis() / 1000 + ".png")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            if (bmp != null){
                put(MediaStore.Images.Media.WIDTH,bmp.width)
                put(MediaStore.Images.Media.HEIGHT,bmp.height)
            }
        }

        return try{
            contentResolver.insert(imageCollection,contentValues)?.also {
                contentResolver.openOutputStream(it).use { outputStream ->
                    if (bmp != null){
                        if(!bmp.compress(Bitmap.CompressFormat.PNG,95,outputStream)){
                            throw IOException("Failed to save Bitmap")
                        }
                    }
                }
            } ?: throw IOException("Failed to create Media Store entry")
            true
        }catch (e: IOException){
            e.printStackTrace()
            false
        }
    }
}