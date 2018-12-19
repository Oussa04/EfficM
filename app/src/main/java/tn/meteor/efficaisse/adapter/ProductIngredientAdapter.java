package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;

/**
 * Created by lilk on 04/02/2018.
 */

public class ProductIngredientAdapter extends RecyclerView.Adapter<ProductIngredientAdapter.ProductIngredientHolder> {


    private Context context;
    private List<Product_Ingredient> product_ingredient_relationList;
    public ProductIngredientAdapter.GridAdapterListener listener;
    private boolean showAvailbleQuantity;
    private boolean isCashier;

    public ProductIngredientAdapter(Boolean isCashier, Context context, ProductIngredientAdapter.GridAdapterListener listener, List<Product_Ingredient> product_ingredient_relationList, boolean showAvailbleQuantity) {
        this.context = context;
        this.listener = listener;
        this.isCashier = isCashier;
        this.product_ingredient_relationList = product_ingredient_relationList;
        this.showAvailbleQuantity = showAvailbleQuantity;
    }

    public List<Product_Ingredient> getProductIngredientList() {
        return product_ingredient_relationList;
    }

    @Override
    public ProductIngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_ingredient, parent, false);

        return new ProductIngredientHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ProductIngredientHolder holder, int position) {
        Product_Ingredient productIngredient = product_ingredient_relationList.get(position);
        if (isCashier){

            holder.remove.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
        if (showAvailbleQuantity) {
            holder.quantity.setText("Quantité disponible: " + String.format("%.2f",productIngredient.getIngredient().getStockQuantity())+ " " + productIngredient.getIngredient().getUnit());
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onProductIngredientEditClicked(productIngredient);
                }
            });

            holder.remove.setOnClickListener(view -> listener.onProductIngredientRemoveClicked(productIngredient));
            holder.name.setText(productIngredient.getIngredient().getName());
            CacheStore cacheStore = CacheStore.getInstance();
            try {
                Picasso.with(context)
                        .load(cacheStore.getFileUri(productIngredient.getIngredient().getPhoto()))
                        .into(holder.thumbnail);
            } catch (Exception e) {
            }


        } else {
            holder.quantity.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
            holder.name.setText(productIngredient.getProduct().getName());
            CacheStore cacheStore = CacheStore.getInstance();
            try {
                Picasso.with(context)
                        .load(cacheStore.getFileUri(productIngredient.getProduct().getPhoto()))
                        .into(holder.thumbnail);
            } catch (Exception e) {
            }
        }
        holder.ingredient_product_quantity.setText(String.format("%.2f",productIngredient.getQuantity() )+ " " + productIngredient.getIngredient().getUnit());


    }

    @Override
    public int getItemCount() {
        return product_ingredient_relationList.size();
    }

    public class ProductIngredientHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView quantity;
        public CardView card_view;
        public SquareImageView thumbnail;
        public TextView ingredient_product_quantity;
        public ImageButton remove, edit;

        public ProductIngredientHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.quantity);
            card_view = view.findViewById(R.id.card_view);
            remove = view.findViewById(R.id.remove);
            edit = view.findViewById(R.id.edit);
            ingredient_product_quantity = view.findViewById(R.id.ingredient_product_quantity);

        }
    }


    public interface GridAdapterListener {
        void onProductIngredientRemoveClicked(Product_Ingredient productIngredientRelation);

        void onProductIngredientEditClicked(Product_Ingredient productIngredientRelation);


    }

}
