package tn.meteor.efficaisse.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.network.Statistics;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.analysis_ingredient.AnalysisIngredientFragment;

/**
 * Created by SKIIN on 20/03/2018.*/

public class StatisticsAdapter extends  RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private List<Statistics> statistics;
    private Context context;
    private Typeface tf;
    private ProductIngredientRepository productIngredientRepository;
    public StatisticsAdapter(Context context, List<Statistics> statistics) {
        this.context = context;
        this.statistics = statistics;
        this.tf = Typeface.createFromAsset(context.getAssets(),"Roboto-Light.ttf");
        productIngredientRepository = new ProductIngredientRepository();
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Statistics statistic = statistics.get(position);
        holder.libelle.setText(statistic.getProductName());
        holder.quantity.setText(String.valueOf(statistic.getQuantity()));
        holder.montant.setText(String.format("%.3f",statistic.getMontant()));
        holder.libelle.setTypeface(tf);
        holder.quantity.setTypeface(tf);
        holder.montant.setTypeface(tf);

        holder.libelle.setOnClickListener(v -> {
            Fragment analysisFragment = new AnalysisIngredientFragment();
            Bundle bundle = new Bundle();
            bundle.putString("libelle", holder.libelle.getText().toString());
            bundle.putString("prix", holder.montant.getText().toString());
            bundle.putInt("quantity", statistic.getQuantity());
            analysisFragment.setArguments(bundle);
            System.out.println(holder.libelle.getText().toString());
            Product ProductTarget = productIngredientRepository.
                    findByName(holder.libelle.getText().toString());

           if((ProductTarget.getCost() != null && ProductTarget.getStockQuantity() != null)){

                new MaterialDialog.Builder(context)
                        .title("Notice")
                        .content("Ce produit est une marchandise , pas d\'ingredients")
                        .positiveText("OK")
                        .show();

            }else {
               ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.content_main, analysisFragment).addToBackStack("main").commit();
           } });


    }


    @Override
    public int getItemCount() {
        return statistics.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView libelle,quantity,montant;

        private ItemClickListener itemClickListener;



        public ViewHolder(View itemView) {
            super(itemView);
            montant = itemView.findViewById(R.id.montant);
            quantity =  itemView.findViewById(R.id.quantity);
            libelle =  itemView.findViewById(R.id.libelle);
        }


        interface ItemClickListener{

            void onClick(View view, int position);

        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition());
        }


        public  void setItemClickListener(ItemClickListener itemClickListener){

            this.itemClickListener=itemClickListener;

        }


    }
}
