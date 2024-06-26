package com.example.newhope

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    lateinit var  choose_img: Button
    lateinit var  upload_img : Button
    lateinit var retrieve_img: Button
    lateinit var image_view: ImageView
    var fileUri : Uri? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        choose_img = findViewById(R.id.choose_image)
        upload_img = findViewById(R.id.upload_image)
        retrieve_img = findViewById(R.id.retrive_image)

        image_view = findViewById(R.id.image_view)

        choose_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose clothing to Upload"), 0
            )
        }

        upload_img.setOnClickListener {
            if (fileUri != null){
                uploadImage()
            }else{
                Toast.makeText(applicationContext, "Please Select A Dress to Upload", Toast.LENGTH_LONG).show()
            }
        }
        retrieve_img.setOnClickListener {
            retrive_image()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null){
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                image_view.setImageBitmap(bitmap)
            } catch (e: Exception){
                Log.e("Exception", "Error: $e")
            }
        }
    }
    fun uploadImage(){
        if(fileUri != null){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading Image...")
            progressDialog.setMessage("Processing...")
            progressDialog.show()

            val storage = FirebaseStorage.getInstance("gs://dresses-29e6f.appspot.com")
            val ref: StorageReference = storage.getReference().child("images/" + UUID.randomUUID().toString())
            ref.putFile(fileUri!!).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "file Uploaded Successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                Toast.makeText(applicationContext, "file failed to Uploaded", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun retrive_image() {
        val storage = FirebaseStorage.getInstance("gs://dresses-29e6f.appspot.com")
        val storageReference: StorageReference = storage.reference
        val image_refrence: StorageReference = storageReference.child("images")

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Retriving Image...")
        progressDialog.setMessage("Processing...")
        progressDialog.show()

        image_refrence.downloadUrl.addOnSuccessListener { uri: Uri ->

            Glide.with(this@MainActivity)
                .load(uri)
                .into(image_view)

            progressDialog.dismiss()
            Toast.makeText(this,"Image Retrived Successfull",Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this,"Image Retrived Failed: "+exception.message,Toast.LENGTH_LONG).show()

            }
    }

}

