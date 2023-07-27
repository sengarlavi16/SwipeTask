import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.community.swipetask.api.ApiService
import com.community.swipetask.api.model.ProductListModel
import com.community.swipetask.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProdListViewModel : ViewModel() {

    private val apiBaseUrl = "https://app.getswipe.in/api/public/"
    private val _products = MutableLiveData<List<ProductListModel>>()
    val products: LiveData<List<ProductListModel>>
        get() = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun fetchProducts(progressDialog:CustomProgressDialog) {
        _isLoading.value = true
        progressDialog.showDialog()

        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val productService = retrofit.create(ApiService::class.java)
        val call = productService.getprodlist()

        call!!.enqueue(object : Callback<List<ProductListModel>> {
            override fun onResponse(
                call: Call<List<ProductListModel>>,
                response: Response<List<ProductListModel>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    _products.value = productList!!
                }
                _isLoading.value = false
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<List<ProductListModel>>, t: Throwable) {
                _isLoading.value = false
                progressDialog.dismiss()
            }
        })
    }
}
