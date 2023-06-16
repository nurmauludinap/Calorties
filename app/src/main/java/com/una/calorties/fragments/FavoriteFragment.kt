package com.una.calorties.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.una.calorties.databinding.FragmentFavoriteBinding
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val client = OkHttpClient()

    companion object {
        val IMAGE_REQUEST_CODE = 100
        val CAMERA_REQUEST_CODE = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        binding.cameraBtn.setOnClickListener {
            pickPhoto()
        }

        binding.galleryBtn.setOnClickListener {
            pickImageGallery()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val direction =
                FavoriteFragmentDirections.actionFavoriteFragmentToFoodDetailActivity(
                    data?.data.toString()
                )
            findNavController().navigate(direction)
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode ==
            RESULT_OK
        ) {
            val uri = context?.let {
                getImageUri(
                    it,
                    data?.extras?.get("data") as Bitmap
                )
            }
            val direction =
                FavoriteFragmentDirections.actionFavoriteFragmentToFoodDetailActivity(
                    uri.toString()
                )
            if (uri != null) {
                findNavController().navigate(direction)
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun pickPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, inImage, "Title", null
        )
        return Uri.parse(path)
    }

}