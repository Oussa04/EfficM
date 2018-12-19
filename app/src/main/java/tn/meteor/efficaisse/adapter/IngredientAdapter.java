package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;


public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientHolder> {

    private Context context;
    private List<Ingredient> ingredientList;
    public GridAdapterListener listener;

    public IngredientAdapter(Context context, GridAdapterListener listener, List<Ingredient> ingredientList) {
        this.context = context;
        this.listener = listener;
        this.ingredientList = ingredientList;
    }


    @Override
    public IngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final IngredientHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.name.setText(ingredient.getName());
        holder.quantity.setText("Quantité disponible: " + String.format("%.2f",ingredient.getStockQuantity()) + " " + ingredient.getUnit());
        CacheStore cacheStore = CacheStore.getInstance();
        try{


        Picasso.with(context)
                .load(cacheStore.getFileUri(ingredient.getPhoto()))
                .into(holder.thumbnail);
        }catch(Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public class IngredientHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView quantity;
        public SquareImageView thumbnail;


        public IngredientHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.quantity);


            view.setOnClickListener(view1 -> listener.onIngredientSelected(ingredientList.get(getAdapterPosition())));
            view.setOnLongClickListener(v -> {
                listener.onIngredientLongClickSelected(ingredientList.get(getAdapterPosition()));
                return false;
            });
        }
    }


    public interface GridAdapterListener {
        void onIngredientSelected(Ingredient ingredient);

        void onIngredientLongClickSelected(Ingredient ingredient);
    }

}
