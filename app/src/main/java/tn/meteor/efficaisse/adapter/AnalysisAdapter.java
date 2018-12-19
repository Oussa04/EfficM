package tn.meteor.efficaisse.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Prediction;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> implements Filterable {


    private List<Prediction> predicitons;
    private List<Prediction> filteredPredictions;

    public AnalysisAdapter(List<Prediction> predictions) {
        this.predicitons = predictions;
        filteredPredictions = new ArrayList<>(predictions);
    }
    public void notifyData(){
        filteredPredictions = predicitons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prediction, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Prediction prediction = filteredPredictions.get(position);
        if (prediction.getCount() > 2) {
            String message = "Il est recommendé d'avoir <b><font color='#39c870'>" + prediction.getPrediction() + " "+prediction.getIngredient().getUnit() + " de "+prediction.getIngredient().getName()+
                    "</font></b> pour demain d'après <b><font color='#39c870'>"+prediction.getCriteria()+"</font></b>.";



            holder.text.setText(Html.fromHtml(message));
        } else {
            String message = "Il n'y a pas suffisamment de données pour donner une recommendation pour <b><font color='#39c870'>" +prediction.getIngredient().getName()+
                    "</font></b>.";

            holder.text.setText(Html.fromHtml(message));
        }

    }

    @Override
    public int getItemCount() {
        return filteredPredictions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Prediction> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = predicitons;
                } else {
                    for (Prediction prediction : predicitons) {
                        if (prediction.getIngredient().getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(prediction);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredPredictions = (ArrayList<Prediction>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;


        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);

        }


    }
}
