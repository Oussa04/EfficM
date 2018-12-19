package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.CredentialsRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private Context context;
    private List<Cashier> usersList;
    public GridAdapterListener listener;
    private PreferencesHelper prefs;


    public UserAdapter(Context context, GridAdapterListener listener, List<Cashier> usersList) {
        this.context = context;
        this.listener = listener;
        this.usersList = usersList;
        prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
    }


    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        Cashier user = usersList.get(position);
        holder.name.setText(user.getName());


        CredentialsRepository credentialsRepository = new CredentialsRepository();
        if (credentialsRepository.find(user.getUsername()) == null) {
            holder.userPic.setImageDrawable(context.getResources().getDrawable(R.drawable.cashier));

        }
        else
            holder.userPic.setImageDrawable(context.getResources().getDrawable(R.drawable.manager));

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class UserHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView userPic;

        public UserHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);

            userPic = view.findViewById(R.id.user_pic);
            view.setOnClickListener(view1 -> listener.onUserSelected(usersList.get(getAdapterPosition())));
            view.setOnLongClickListener(v -> {
                listener.onUserLongClicked(usersList.get(getAdapterPosition()));
                return false;
            });
        }
    }


    public interface GridAdapterListener {
        void onUserSelected(Cashier user);

        void onUserLongClicked(Cashier user);
    }

}
