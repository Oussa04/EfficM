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
import tn.meteor.efficaisse.model.Event;


/**
 * Created by Oussa on 23-Apr-18.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder>  {

    private Context mContext;
    private int row_index = 0;
    private List<Event> eventsList;
    public EventAdapter.EventAdapterListener listener;
    int selected_position = 0;
    public class EventHolder extends RecyclerView.ViewHolder {

        //public ProgressBar mProgressBar;
        public ImageView image;
        public TextView eventName,awayTeam,homeTeam, separator, date;

        public EventHolder(View view) {
            super(view);
            //image =  view.findViewById(R.id.eventImage);
            eventName =  view.findViewById(R.id.eventName);
           // mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
            awayTeam = (TextView) view.findViewById(R.id.awayTeam);
            homeTeam = (TextView) view.findViewById(R.id.homeTeam);
            separator = (TextView) view.findViewById(R.id.separator);
            date = (TextView) view.findViewById(R.id.eventDate);

//            image.setOnClickListener(view1 -> listener.onEventSelected(eventsList.get(getAdapterPosition())));

        }
    }

    public EventAdapter(Context mContext, EventAdapter.EventAdapterListener listener, List<Event> eventsList) {
        this.mContext = mContext;
        this.listener = listener;
        this.eventsList = eventsList;
    }

    @Override
    public EventAdapter.EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_eventt, parent, false);

        return new EventHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventAdapter.EventHolder holder, int position) {



        Event event = eventsList.get(position);
        holder.awayTeam.setText(event.getAwayTeamName());
        holder.homeTeam.setText(event.getHomeTeamName());
        holder.date.setText(event.getDate());
        holder.eventName.setText(event.getEventName());


        holder.awayTeam.setOnClickListener(view -> {
            row_index=position;
            notifyDataSetChanged();
            listener.onEventSelected(eventsList.get(position));
        });


//        if (event.getEventName().equals("Champions League")) {
//            Picasso.with(mContext)
//                    .load(R.drawable.ic_check)
//                    .into(holder.image);
//        } else {
//
//            Picasso.with(mContext)
//                    .load(R.drawable.ic_bon)
//                    .into(holder.image);
//        }

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }


    public interface EventAdapterListener {
        void onEventSelected(Event event);

        void onEventLongClickSelected(Event event);
    }
}
