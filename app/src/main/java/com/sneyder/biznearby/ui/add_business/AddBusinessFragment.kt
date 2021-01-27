package com.sneyder.biznearby.ui.add_business

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.business.Address
import com.sneyder.biznearby.data.model.business.Category
import com.sneyder.biznearby.data.model.business.Hour
import com.sneyder.biznearby.ui.add_business_image.AddBizImageActivity
import com.sneyder.biznearby.ui.add_business_image.AddBizImageActivity.Companion.EXTRA_IMAGE_URL
import com.sneyder.biznearby.ui.pick_city.PickCityActivity
import com.sneyder.biznearby.ui.pick_location.PickLocationActivity
import com.sneyder.biznearby.utils.*
import com.sneyder.biznearby.utils.base.DaggerFragment
import com.sneyder.biznearby.utils.dialogs.SelectImageDialog
import kotlinx.android.synthetic.main.fragment_add_business.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddBusinessFragment : DaggerFragment(), OnMapReadyCallback,
    SelectImageDialog.SelectImageListener, BusinessImageAdapter.BusinessImageListener {

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        const val REQUEST_LOCATION_DETAILS = 10002
        const val REQUEST_CITY = 10003
        const val UNREACHABLE_GRADE = 500.0
        const val UNREACHABLE_CITY_CODE = -1
        const val REQUEST_PICK_PHOTO = 100
        const val REQUEST_TAKE_PHOTO = 101
        const val REQUEST_UPLOAD_IMAGE = 102

        fun newInstance() = AddBusinessFragment()
    }

    private val viewModel: AddBusinessViewModel by viewModels { viewModelFactory }
    private var googleMap: GoogleMap? = null
    private val imagesAdapter by lazy {
        BusinessImageAdapter(this)
    }

    override fun onImageRemoved(imageUrl: String) {
        imagesAdapter.images.remove(imageUrl)
        imagesAdapter.notifyDataSetChanged()
    }

    override fun onImageSelected() {
        uploadingBannerImg = false
        showPickImageBottomSheet()
    }

    private var locationSelected: LatLng? = null
        set(value) {
            field = value
            pickLocationTextView?.text =
                if (value != null) "Editar ubicación exacta" else "Seleccionar ubicacián exacta"
        }
    private var cityCodeSelected: Int? = null
    private var cityDescSelected: String? = null
        set(value) {
            field = value
            value?.let { cityEditText?.setText(it) }
        }
    private var categories: ArrayList<Category>? = null
    private val categoriesSelected: ArrayList<Category> by lazy { ArrayList() }
    private var uploadingBannerImg: Boolean = false
    private var currentBannerImgUrl: String? = null
        set(value) {
            field = value
            Glide.with(requireContext()).load(field).centerCrop().into(bannerImageView)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_business, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        setUpImagesRecyclerView()
        pickLocationTextView.setOnClickListener {
            startActivityForResult(
                PickLocationActivity.starterIntent(
                    requireContext(),
                    nameEditText.text.toString(),
                    locationSelected?.latitude, locationSelected?.longitude
                ),
                REQUEST_LOCATION_DETAILS
            )
        }
        cityEditText.setOnClickListener {
            startActivityForResult(PickCityActivity.starterIntent(requireContext()), REQUEST_CITY)
        }
        addCategoryChip.setOnClickListener {
            showCategoriesDialog()
        }
        bannerImageView.setOnClickListener {
            uploadingBannerImg = true
            showPickImageBottomSheet(true)
        }
        saveButton.setOnClickListener {
            debug("save clicked~")
            saveBusiness()
        }
        setUpListenersForScheduling()
        observeCategories()
        observeBusinessCreated()
        viewModel.fetchCategories()
    }

    private var progressDialog: ProgressDialog? = null

    private fun observeBusinessCreated() {
        viewModel.businessCreated.observe(viewLifecycleOwner) {
            when {
                it.isLoading -> {
                    progressDialog = ProgressDialog(context)
                    progressDialog?.setCancelable(false)
                    progressDialog?.setMessage("Subiendo imagen...")
                    progressDialog?.show()
                }
                it.success != null -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                }
                else -> {
                    if (progressDialog?.isShowing == true)
                        progressDialog?.dismiss()
                }
            }
        }
    }

    private fun saveBusiness() {
        val fieldsValidator = FieldsValidator()
        val name = InputValidator.Builder(nameEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        val description = InputValidator.Builder(descriptionEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        val addressLine = InputValidator.Builder(addressEditText)
            .required()
            .minLength(4)
            .build(fieldsValidator)
        if (!fieldsValidator.allInputsValid()) return
        debug("input validations passed $locationSelected $cityCodeSelected")
        val address = Address(
            id = UUID.randomUUID().toString(),
            address = addressLine,
            latitude = locationSelected?.latitude ?: return,
            longitude = locationSelected?.longitude ?: return,
            cityCode = cityCodeSelected ?: return
        )
        viewModel.addNewBusiness(
            name = name,
            description = description,
            address = address,
            bannerUrl = currentBannerImgUrl ?: "",
            businessId = UUID.randomUUID().toString(),
            categories = categoriesSelected.map { it.code },
            images = imagesAdapter.images
        )
    }

    private fun setUpImagesRecyclerView() {
        with(imagesList) {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(GridSpacingItemDecoration(3, 16.px, false))
            adapter = imagesAdapter
        }
    }

    private val chipsList by lazy {
        listOf(
            mondayChip,
            tuesdayChip,
            wednesdayChip,
            thursDayChip,
            fridayChip,
            saturdayChip,
            sundayChip
        )
    }

    private val hoursByDay by lazy { ArrayList<Hour>() }
    private var daySelected: Int? = null

    private fun setUpListenersForScheduling() {
        chipsList.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                daySelected = index
                cleanChipsBackground()
                chip.setChipBackgroundColorResource(R.color.lightBlue)
                displayHoursAtIndex(index)
            }
        }
        addHoursButton.setOnClickListener {
            val container = getHoursContainer()
            hoursLayout.addView(container)
            daySelected?.let {

            }
        }
    }

    private fun displayHoursAtIndex(index: Int) {
        val hours = hoursByDay.filter { it.day == index + 1 }
        hoursLayout.removeAllViews()
        hours.forEach { hour ->
            val container = getHoursContainer(hour)
            hoursLayout.addView(container)
        }
    }

    private fun getHoursContainer(hour: Hour? = null): View? {
        val container = View.inflate(
            requireContext(),
            R.layout.hourly_schedule_detail,
            null
        )
//        container.findViewById<View>(R.id.removeImageView).setOnClickListener {
//            hoursLayout.removeView(container)
//        }
        hour?.let {
            val openTime = container.findViewById<TextInputEditText>(R.id.openTime)
            val closeTime = container.findViewById<TextInputEditText>(R.id.closeTime)
            openTime.setText(hour.openTime)
            closeTime.setText(hour.closeTime)
        }
        return container
    }

    private fun cleanChipsBackground() {
        for (chip in chipsList) {
            chip.setChipBackgroundColorResource(R.color.lightGrey)
        }
    }

    override fun onTakePicture() {
        debug("onTakePicture")
        launchTakePictureIntent()
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
                    val photoFile: File = try {
                        createImageFile()
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
                    debug("photofile=$photoFile")
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        "com.sneyder.biznearby.android.fileprovider",
                        photoFile
                    )
//                    imageProfilePath = photoFile.path
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            })
    }

    override fun onPickImage() {
        debug("onPickImage")
        launchImageSelectorIntent()
    }

    override fun onRemoveImage() {
        currentBannerImgUrl = null
    }

    private fun launchImageSelectorIntent() {
        ifHasPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PICK_PHOTO, {
                val intent = ImageSelectorUtils.getImageSelectionIntent()
                startActivityForResult(intent, REQUEST_PICK_PHOTO)
            })
    }

    private fun showPickImageBottomSheet(includeRemove: Boolean = false) {
        val selectImageDialog = SelectImageDialog.newInstance(includeRemove)
        selectImageDialog.show(childFragmentManager, selectImageDialog.tag)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("AddByzFr onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            REQUEST_LOCATION_DETAILS -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val latitude =
                    data.getDoubleExtra(PickLocationActivity.EXTRA_LATITUDE, UNREACHABLE_GRADE)
                        .let { if (it != UNREACHABLE_GRADE) it else null }
                val longitude =
                    data.getDoubleExtra(PickLocationActivity.EXTRA_LONGITUDE, UNREACHABLE_GRADE)
                        .let { if (it != UNREACHABLE_GRADE) it else null }
                locationSelected = LatLng(latitude ?: return, longitude ?: return)
                debug("onActivityResult success $latitude $longitude")
                addMarkerToMap(locationSelected ?: return)
            }
            REQUEST_CITY -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                cityCodeSelected =
                    data.getIntExtra(PickCityActivity.EXTRA_CITY_CODE, UNREACHABLE_CITY_CODE)
                        .let { if (it != UNREACHABLE_CITY_CODE) it else null }
                cityDescSelected = data.getStringExtra(PickCityActivity.EXTRA_CITY_NAME)
            }
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
                val imgUrl = data.getStringExtra(EXTRA_IMAGE_URL)
                debug("REQUEST_UPLOAD_IMAGE $imgUrl")
                previewImgUploaded(imgUrl ?: return)
            }
        }
    }

    private fun previewImgUploaded(imgUrl: String) {
        if (uploadingBannerImg) {
            currentBannerImgUrl = imgUrl
        } else {
            imagesAdapter.images.add(imgUrl)
            imagesAdapter.notifyDataSetChanged()
        }
    }

    private fun uploadBusinessImage(path: String) {
        startActivityForResult(
            AddBizImageActivity.starterIntent(requireContext(), path),
            REQUEST_UPLOAD_IMAGE
        )
    }

    private fun addMarkerToMap(locationChosen: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(
            MarkerOptions()
                .position(locationChosen)
                .title("Ubicación de ${nameEditText.text}")
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(locationChosen))
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(10.08, -69.32)))
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}