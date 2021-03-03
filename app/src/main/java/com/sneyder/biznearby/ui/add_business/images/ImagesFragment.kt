package com.sneyder.biznearby.ui.add_business.images

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.add_business.BusinessImageAdapter
import com.sneyder.biznearby.ui.add_business_image.AddBizImageActivity
import com.sneyder.biznearby.utils.*
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.dialogs.SelectImageDialog
import kotlinx.android.synthetic.main.fragment_images.*
import java.io.IOException

class ImagesFragment : DaggerFragment(), BusinessImageAdapter.BusinessImageListener,
    SelectImageDialog.SelectImageListener, AddBusinessActivity.InputsValidation {

    companion object {
        const val REQUEST_PICK_PHOTO = 100
        const val REQUEST_TAKE_PHOTO = 101
        const val REQUEST_UPLOAD_IMAGE = 102

        fun newInstance() = ImagesFragment()
    }

    private val imagesAdapter by lazy {
        BusinessImageAdapter(this)
    }
    private var currentPhotoPath: String? = null
    private val currentImages by lazy {
        (activity as? AddBusinessActivity)?.currentImages
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_images, container, false)
    }

    override fun areAllInputsValid(): Boolean {
        currentImages?.addAll(imagesAdapter.images)
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpImagesRecyclerView()
    }

    private fun setUpImagesRecyclerView() {
        with(imagesRecyclerView) {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(GridSpacingItemDecoration(3, 16.px, false))
            adapter = imagesAdapter
        }
    }

    override fun onImageRemoved(imageUrl: String) {
        imagesAdapter.images.remove(imageUrl)
        imagesAdapter.notifyDataSetChanged()
    }

    override fun onImageSelected() {
        showPickImageBottomSheet()
    }

    private fun showPickImageBottomSheet(includeRemove: Boolean = false) {
        val selectImageDialog = SelectImageDialog.newInstance(includeRemove)
        selectImageDialog.show(childFragmentManager, selectImageDialog.tag)
    }

    override fun onTakePicture() {
        launchTakePictureIntent()
    }

    override fun onPickImage() {
        launchImageSelectorIntent()
    }

    override fun onRemoveImage() {}

    private fun launchTakePictureIntent() {
        ifHasPermission(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            ), REQUEST_TAKE_PHOTO, {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(requireContext().packageManager) == null)
                        return@ifHasPermission
                    // Create the File where the photo should go
                    val photo = try {
                        requireContext().createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Toast.makeText(
                            requireContext(),
                            "Error occurred while launching the camera",
                            Toast.LENGTH_LONG
                        ).show()
                        return@ifHasPermission
                    } catch (ex: Exception) {
                        return@ifHasPermission
                    }
                    // Continue only if the File was successfully created
                    currentPhotoPath = photo.second
                    debug("photofile=${photo.first}")
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        "com.sneyder.biznearby.android.fileprovider",
                        photo.first
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_TAKE_PHOTO
                    )
                }
            })
    }

    private fun launchImageSelectorIntent() {
        ifHasPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PICK_PHOTO, {
                val intent = ImageSelectorUtils.getImageSelectionIntent()
                startActivityForResult(intent, REQUEST_PICK_PHOTO)
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("BasicInfoFragment onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            REQUEST_PICK_PHOTO -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val selectedImage: Uri? = data.data
                val path = ImageSelectorUtils.getFilePathFromUri(requireContext(), selectedImage)
                debug("REQUEST_PICK_PHOTO: $path")
                uploadBusinessImage(path)
            }
            REQUEST_TAKE_PHOTO -> {// a existing image has been selected
                if (resultCode != Activity.RESULT_OK) return
                uploadBusinessImage(currentPhotoPath ?: return)
            }
            REQUEST_UPLOAD_IMAGE -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val imgUrl = data.getStringExtra(AddBizImageActivity.EXTRA_IMAGE_URL)
                debug("REQUEST_UPLOAD_IMAGE $imgUrl")
                previewImgUploaded(imgUrl ?: return)
            }
        }
    }

    private fun previewImgUploaded(imgUrl: String) {
        imagesAdapter.images.add(imgUrl)
        imagesAdapter.notifyDataSetChanged()
    }

    private fun uploadBusinessImage(path: String) {
        startActivityForResult(
            AddBizImageActivity.starterIntent(requireContext(), path), REQUEST_UPLOAD_IMAGE
        )
    }

}