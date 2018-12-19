package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.SquareImageView;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private Context mContext;
    private int row_index = 0;
    private List<Category> catgoriesList;
    public CategoryAdapterListener listener;
    int selected_position = 0;
    public class CategoryHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ProgressBar mProgressBar;
        public IconTextView description;
        public SquareImageView thumbnail;
        public ImageView gradient;


        public CategoryHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            mProgressBar = view.findViewById(R.id.mProgressBar);
            description = view.findViewById(R.id.descrption);
            gradient= view.findViewById(R.id.gradient);

            gradient.setOnClickListener(view1 -> listener.onCategorySelected(catgoriesList.get(getAdapterPosition())));

        }
    }

    public CategoryAdapter(Context mContext, CategoryAdapterListener listener, List<Category> categoriesList) {
        this.mContext = mContext;
        this.listener = listener;
        this.catgoriesList = categoriesList;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, int position) {



        Category category = catgoriesList.get(position);
        holder.description.setText(category.getName());
        holder.gradient.setOnClickListener(view -> {
            row_index=position;
            notifyDataSetChanged();
            listener.onCategorySelected(catgoriesList.get(position));
        });


        if(row_index==position){
            holder.gradient.setBackgroundResource(R.color.colorAccent);
            listener.onCategorySelected(catgoriesList.get(position));

        }
        else
        {
            holder.gradient.setBackgroundColor(Color.TRANSPARENT);
       }


        if (category.getName().equals("Favoris")) {
            Picasso.with(mContext)
                    .load(R.drawable.lace).fit()
                    .into(holder.thumbnail);
        } else {
            try {
                CacheStore cacheStore = CacheStore.getInstance();
                Picasso.with(mContext)
                        .load(cacheStore.getFileUri(category.getPhoto())).fit()
                        .into(holder.thumbnail);
            }catch(Exception e){

            }
        }

    }

    @Override
    public int getItemCount() {
        return catgoriesList.size();
    }


    public interface CategoryAdapterListener {
        void onCategorySelected(Category category);

        void onCategoryLongClickSelected(Category category);
    }

}
