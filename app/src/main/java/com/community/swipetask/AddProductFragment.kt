package com.community.swipetask

import ProductListFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.community.swipetask.api.viewmodel.AddProductViewModel
import com.community.swipetask.databinding.FragmentAddProductBinding
import com.community.swipetask.utils.AppUtils
import java.io.File

class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var viewModel: AddProductViewModel
    lateinit var typeadapter: ArrayAdapter<String>
    var typeId = ArrayList<String>()
    private val PICK_IMAGE_REQUEST = 1
    private var imageFile: File? = null
    var selectedItem = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)

        typeId.add("Select Type")

        typeadapter = ArrayAdapter(requireContext(), R.layout.spin_drop_down_view, typeId)
        typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.TypeSpinner.setAdapter(typeadapter)

        binding.TypeSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                selectedItem = adapterView?.getItemAtPosition(i) as String}

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        viewModel.products.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                val productTypes = mutableListOf<String>()

                for (product in response) {
                    val productType = product.productType
                    if (!productTypes.contains(productType)) {
                        productType?.let { productTypes.add(it) }
                    }
                }

                typeId.clear()
                typeId.add("Select Type")
                typeId.addAll(productTypes)
                typeadapter.notifyDataSetChanged()
            }
        })

        binding.txtChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.txtSubmit.setOnClickListener(View.OnClickListener {
            val productName = binding.edtName.text.toString()
            val price = binding.edtPrice.text.toString().toDoubleOrNull()
            val tax = binding.edtTax.text.toString().toDoubleOrNull()

            if (productName.isBlank()) {
                Toast.makeText(requireContext(), "Please enter product name", Toast.LENGTH_SHORT).show()
            } else if (price == null) {
                Toast.makeText(requireContext(), "Please enter a valid price in decimals", Toast.LENGTH_SHORT).show()
            } else if (tax == null) {
                Toast.makeText(requireContext(), "Please enter a valid tax in decimals", Toast.LENGTH_SHORT).show()
            } else if (selectedItem.equals("Select Type", ignoreCase = true)) {
                Toast.makeText(requireContext(), "Please select product type", Toast.LENGTH_SHORT).show()
            } else {
                if (imageFile != null) {
                    viewModel.addProduct(productName, selectedItem, price, tax, imageFile!!)
                } else {
                    viewModel.addProductWithoutFile(productName, selectedItem, price, tax, "")
                }
            }

            viewModel.addProductResponse.observe(viewLifecycleOwner, Observer { response ->
                if (response.success!!) {
                    Toast.makeText(requireContext(), "Product Added Successfully", Toast.LENGTH_SHORT).show()
                    context?.let { AppUtils.goNextFragmentReplace(it, ProductListFragment()) }
                } else {
                    Toast.makeText(requireContext(), "Product not added", Toast.LENGTH_SHORT).show()
                }
            })

        })

        binding.imgback.setOnClickListener(View.OnClickListener {
            context?.let { AppUtils.goNextFragmentReplace(it, ProductListFragment()) }
        })

        return binding.root
    }

    private fun convertUriToFile(uri: Uri?): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        uri?.let {
            val cursor = requireActivity().contentResolver.query(it, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val filePath = cursor?.getString(columnIndex!!)
            cursor?.close()
            return filePath?.let { File(it) }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            imageFile = convertUriToFile(selectedImageUri)

            val fileName = selectedImageUri?.let { getFileNameFromUri(it) }
            binding.txtFileChosen.text = fileName ?: "No File Chosen"
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }
}