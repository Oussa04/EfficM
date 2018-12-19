package tn.meteor.efficaisse.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Analysis;

public class AnalysisIngredientAdapter extends RecyclerView.Adapter<AnalysisIngredientAdapter.AnalysisHolder>{


    private Context context;
    private List<Analysis> analyses;
    private AnalysisAdapterListener analysisAdapterListener;

    public AnalysisIngredientAdapter(Context context, AnalysisAdapterListener analysisAdapterListener, List<Analysis> analyses) {
        this.context = context;
        this.analyses = analyses;
        this.analysisAdapterListener = analysisAdapterListener;
    }


    @NonNull
    @Override
    public AnalysisIngredientAdapter.AnalysisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analysis, parent, false);
          return new AnalysisIngredientAdapter.AnalysisHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisHolder holder, int position) {


        Analysis analysis = analyses.get(position);
        holder.indication.setText(analysis.getIndication());
        holder.libelle.setText(analysis.getIngredientName());
        holder.quantity.setText(String.format("%.2f",analysis.getQuantity()));
        holder.unit.setText(analysis.getUnit());
        analysisAdapterListener.onAnalysisSelected(analyses.get(position));

    }


    @Override
    public int getItemCount() {

        return analyses.size();
    }

    public class AnalysisHolder extends RecyclerView.ViewHolder {

        private TextView indication,quantity,libelle,unit;

        public AnalysisHolder(View view) {
            super(view);
            indication = itemView.findViewById(R.id.indication);
            quantity =  itemView.findViewById(R.id.quantity);
            libelle =  itemView.findViewById(R.id.libelle);
            unit =  itemView.findViewById(R.id.unit);

        }
    }

    public interface AnalysisAdapterListener {

        void onAnalysisSelected(Analysis analysis);
        void onAnalysisLongClickSelected(Analysis analysis);
    }

}





