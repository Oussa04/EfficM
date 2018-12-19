package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;
import tn.meteor.efficaisse.model.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private Context context;
    private List<Product> productsList;
    public GridAdapterListener listener;


    public ProductAdapter(Context context, GridAdapterListener listener, List<Product> productsList) {
        this.context = context;
        this.listener = listener;
        this.productsList = productsList;
    }



    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, int position) {
        Product product = productsList.get(position);
        holder.description.setText(product.getName() + " - " + product.getPrice() + " DT");
        try {
            CacheStore cacheStore = CacheStore.getInstance();
            Picasso.with(context)
                    .load(cacheStore.getFileUri(product.getPhoto())).fit()
                    .into(holder.thumbnail);
        }catch(Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


    public class ProductHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;
        public IconTextView description;
        public SquareImageView thumbnail;


        public ProductHolder(View view) {
            super(view);
            thumbnail =  view.findViewById(R.id.thumbnail);
            mProgressBar = view.findViewById(R.id.mProgressBar);
            description =  view.findViewById(R.id.descrption);

            view.setOnClickListener(view1 -> listener.onProductSelected(productsList.get(getAdapterPosition())));
            view.setOnLongClickListener(v -> {
                listener.onProductLongClickSelected(productsList.get(getAdapterPosition()));
                return false;
            });
        }
    }


    public interface GridAdapterListener {
        void onProductSelected(Product product);
        void onProductLongClickSelected(Product product);
    }

}
