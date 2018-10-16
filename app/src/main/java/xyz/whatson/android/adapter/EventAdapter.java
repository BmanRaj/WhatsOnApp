package xyz.whatson.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import xyz.whatson.android.R;
import xyz.whatson.android.model.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context mContext;
    private List<Event> eventList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, startDate;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            startDate = (TextView) view.findViewById(R.id.date);
        }
    }


    public EventAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event_large, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.startDate.setText(new SimpleDateFormat("d\nMMM").format(event.getEventDate()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}