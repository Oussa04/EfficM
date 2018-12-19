package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.utils.HomeMenu;

/**
 * Created by lilk on 29/01/2018.
 */

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MenuItemsHolder> {


    private Context context;
    private List<HomeMenu> homeMenuList;
    public MenuItemListener listener;


    public MenuItemsAdapter(Context context, MenuItemListener listener, List<HomeMenu> homeMenuList) {
        this.context = context;
        this.listener = listener;
        this.homeMenuList = homeMenuList;
    }


    @Override
    public MenuItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);

        return new MenuItemsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuItemsHolder holder, int position) {

        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        HomeMenu homeMenu = homeMenuList.get(position);
        try {


            holder.imageView.setImageDrawable(context.getResources().getDrawable(homeMenu.getItemDrawble()));
        } catch (Exception e) {

        }
        holder.action.setText(homeMenu.getItemText());
        LayerDrawable ld = (LayerDrawable) context.getResources().getDrawable(R.drawable.shadow_card_home);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(homeMenu.getColor());
        gd.setCornerRadius(15);
        boolean testfactor = ld.setDrawableByLayerId(R.id.backgroundColor, gd);
        holder.constraintLayout.setBackground(ld);


    }


    @Override
    public int getItemCount() {
        return homeMenuList.size();
    }


    public class MenuItemsHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView action;
        public ConstraintLayout constraintLayout;

        public MenuItemsHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            action = (TextView) view.findViewById(R.id.action);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.la);


            view.setOnClickListener(view1 -> listener.onMenuItemClicked(homeMenuList.get(getAdapterPosition())));


            view.setOnLongClickListener(v -> {

                listener.onMenuItemClicked(homeMenuList.get(getAdapterPosition()));
                return false;
            });


        }
    }


    public interface MenuItemListener {
        void onMenuItemClicked(HomeMenu homeMenu);

        void onMenuItemLongClicked(HomeMenu homeMenu);


    }
}
