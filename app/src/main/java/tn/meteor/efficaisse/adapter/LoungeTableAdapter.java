package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.utils.Common;

/**
 * Created by lilk on 29/01/2018.
 */

public class LoungeTableAdapter extends RecyclerView.Adapter<LoungeTableAdapter.LoungeTableHolder> {


    private Context context;
    private List<LoungeTable> loungeTableList;
    public LoungeTableListener listener;
    private Handler handler = new Handler();

   private Commande commande;

    public LoungeTableAdapter(Context context, LoungeTableListener listener, List<LoungeTable> loungeTableList) {
        this.context = context;
        this.listener = listener;
        this.loungeTableList = loungeTableList;
    }


    @Override
    public LoungeTableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table, parent, false);

        return new LoungeTableHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LoungeTableHolder holder, int position) {

        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        LoungeTable loungeTable = loungeTableList.get(position);

        holder.libelle.setText(loungeTable.getLibelle());
        holder.description.setText(loungeTable.getDescr());



        if (position!=0){
            holder.commande.setVisibility(View.VISIBLE);
            holder.elements.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.libelle.setTextColor(context.getResources().getColor(R.color.dark_gray));
            holder.description.setTextColor(context.getResources().getColor(R.color.dark_gray));
        commande = new Commande();
        if (loungeTable.getCommande_number() != null) {
            commande = detailCommandeRepository.find(loungeTable.getCommande_number());
            holder.commande.setText("Ticket n°" + commande.getCommandeNumber());
            holder.elements.setText(detailCommandeRepository.getCommandeQuantity(commande) + " éléments");
            holder.time.setText(Common.getDateDifference(commande.getDate()));
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.red));

        } else {
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            holder.commande.setVisibility(View.GONE);
            holder.elements.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);

        }
        }else {

            holder.commande.setVisibility(View.GONE);
            holder.elements.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.libelle.setTextColor(context.getResources().getColor(R.color.colorGreen));
            holder.description.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }



    }


    @Override
    public int getItemCount() {
        return loungeTableList.size();
    }


    public class LoungeTableHolder extends RecyclerView.ViewHolder {
        public TextView time, libelle, description, commande, elements;
        public LinearLayout status;

        public LoungeTableHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time);
            libelle = (TextView) view.findViewById(R.id.libelle);
            description = (TextView) view.findViewById(R.id.description);
            commande = (TextView) view.findViewById(R.id.commande);
            elements = (TextView) view.findViewById(R.id.elements);
            status = (LinearLayout) view.findViewById(R.id.status);

            view.setOnClickListener(view1 -> listener.onLoungeTableClicked(loungeTableList.get(getAdapterPosition())));


            view.setOnLongClickListener(v -> {

                listener.onLoungeTableClicked(loungeTableList.get(getAdapterPosition()));
                return false;
            });


        }
    }


    public interface LoungeTableListener {
        void onLoungeTableClicked(LoungeTable loungeTable);

        void onLoungeTableLongClicked(LoungeTable loungeTable);


    }
}
