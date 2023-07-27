import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.community.swipetask.AddProductFragment
import com.community.swipetask.api.model.ProductListModel
import com.community.swipetask.databinding.FragmentProductListBinding
import com.community.swipetask.utils.AppUtils
import com.community.swipetask.utils.CustomProgressDialog

class ProductListFragment : Fragment() {

    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var binding: FragmentProductListBinding
    private lateinit var viewModel: ProdListViewModel
    private lateinit var adapter: ProdListAdapter
    private var productList: List<ProductListModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListBinding.inflate(inflater, container, false)

        progressDialog = CustomProgressDialog(requireContext())

        binding.txtAddProd.setOnClickListener(View.OnClickListener {
            context?.let { AppUtils.goNextFragmentReplace(it, AddProductFragment()) }
        })

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                filterProductList(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    private fun filterProductList(query: String) {
        val filteredList = productList.filter { product ->
            product.productName?.contains(query, ignoreCase = true) == true ||
                    product.productType?.contains(query, ignoreCase = true) == true
        }
        adapter.submitList(filteredList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(ProdListViewModel::class.java)

        viewModel.fetchProducts(progressDialog)
        adapter = ProdListAdapter()
        binding.recyProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyProducts.adapter = adapter

        viewModel.products.observe(viewLifecycleOwner, Observer { products ->
            productList = products
            adapter.submitList(productList)
        })
    }
}
