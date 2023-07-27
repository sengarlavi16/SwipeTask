import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.community.swipetask.api.model.ProductListModel
import com.community.swipetask.databinding.ChildProductsBinding

class ProdListAdapter : ListAdapter<ProductListModel, ProdListAdapter.MyViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChildProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class MyViewHolder(private val binding: ChildProductsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductListModel) {
            binding.txtProdName.text = item.productName
            binding.txtProdType.text = item.productType
            val priceFormatted = String.format("₹%.2f", item.price)
            binding.txtPrice.text = priceFormatted

            val taxFormatted = String.format("%.2f%% Tax", item.tax)
            binding.txtProdTax.text = taxFormatted
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductListModel>() {
        override fun areItemsTheSame(oldItem: ProductListModel, newItem: ProductListModel): Boolean {
            return oldItem.productName == newItem.productName
        }

        override fun areContentsTheSame(oldItem: ProductListModel, newItem: ProductListModel): Boolean {
            return oldItem == newItem
        }
    }
}
