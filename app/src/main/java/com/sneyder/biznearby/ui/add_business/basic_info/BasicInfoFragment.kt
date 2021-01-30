package com.sneyder.biznearby.ui.add_business.basic_info

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.ui.add_business.AddBusinessActivity
import com.sneyder.biznearby.ui.add_business_image.AddBizImageActivity
import com.sneyder.biznearby.utils.*
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.dialogs.SelectImageDialog
import kotlinx.android.synthetic.main.fragment_add_business.*
import kotlinx.android.synthetic.main.fragment_basic_info.*
import kotlinx.android.synthetic.main.fragment_basic_info.addCategoryChip
import kotlinx.android.synthetic.main.fragment_basic_info.bannerImageView
import kotlinx.android.synthetic.main.fragment_basic_info.categoriesLinearLayout
import kotlinx.android.synthetic.main.fragment_basic_info.descriptionEditText
import kotlinx.android.synthetic.main.fragment_basic_info.nameEditText
import java.io.IOException

class BasicInfoFragment : DaggerFragment(), SelectImageDialog.SelectImageListener,
    AddBusinessActivity.InputsValidation {

    companion object {
        const val REQUEST_PICK_PHOTO = 100
        const val REQUEST_TAKE_PHOTO = 101
        const val REQUEST_UPLOAD_IMAGE = 102

        fun newInstance() = BasicInfoFragment()
    }

    private val currentBasicInfo by lazy {
        (activity as? AddBusinessActivity)?.currentBasicInfo
    }
    private var currentPhotoPath: String? = null
    private var currentBannerImgUrl: String? = null
        set(value) {
            field = value
            currentBasicInfo?.bannerUrl = value
            Glide.with(context ?: return).load(field).centerCrop().into(bannerImageView ?: return)
        }
    private var categories: ArrayList<Category>? = null
    private val categoriesSelected: ArrayList<Category> by lazy { ArrayList() }
    private val viewModel: BasicInfoViewModel by viewModels { viewModelFactory }

    override fun areAllInputsValid(): Boolean {
        val fieldsValidator = FieldsValidator()
        val name = InputValidator.Builder(nameEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        val description = InputValidator.Builder(descriptionEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        currentBasicInfo?.name = name
        currentBasicInfo?.description = description
        currentBasicInfo?.categories = categoriesSelected.map { it.code }
        return fieldsValidator.allInputsValid()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bannerImageView.setOnClickListener {
            showPickImageBottomSheet(currentBasicInfo?.bannerUrl?.isNotEmpty() == true)
        }
        addCategoryChip.setOnClickListener {
            showCategoriesDialog()
        }
        with(currentBasicInfo ?: return) {
            debug("currentBasicInfo = $this")
            nameEditText.setText(name)
            descriptionEditText.setText(description)
        }
        observeCategories()
        viewModel.fetchCategories()
    }

    private fun observeCategories() {
        viewModel.categories.observe(viewLifecycleOwner) { categories = it }
    }

    private fun showCategoriesDialog() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialog.setTitle("Seleccione categoria:")
        dialog.setItems(categories?.map { it.category }?.toTypedArray()) { d, position ->
            categories?.let { list ->
                val selected = list[position]
                if (!categoriesSelected.contains(selected)) {
                    categoriesSelected.add(selected)
                    val chip = View.inflate(
                        requireContext(),
                        R.layout.fragment_add_business_chip_item,
                        null
                    ) as Chip
                    chip.text = selected.category
                    chip.setOnCloseIconClickListener {
                        categoriesLinearLayout.removeView(chip)
                        categoriesSelected.remove(list[position])
                    }
                    categoriesLinearLayout.addView(chip, categoriesSelected.count() - 1)
                }
            }
            d.dismiss()
        }
        val alert: AlertDialog = dialog.create()
        alert.show()
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

    override fun onRemoveImage() {
        currentBannerImgUrl = null
        currentPhotoPath = null
    }

    private fun launchImageSelectorIntent() {
        ifHasPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PICK_PHOTO, {
                val intent = ImageSelectorUtils.getImageSelectionIntent()
                startActivityForResult(intent, REQUEST_PICK_PHOTO)
            })
    }

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
                            "Sucedio un error lanzando la app de camara",
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
        currentBannerImgUrl = imgUrl
    }

    private fun uploadBusinessImage(path: String) {
        startActivityForResult(
            AddBizImageActivity.starterIntent(requireContext(), path),
            REQUEST_UPLOAD_IMAGE
        )
    }
}