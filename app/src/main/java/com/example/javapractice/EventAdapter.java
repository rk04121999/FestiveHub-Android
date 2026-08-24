package com.example.javapractice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter
        extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.event_item, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EventViewHolder holder,
            int position) {

        Event event = eventList.get(position);

        holder.tvEventName.setText(event.getEventName());
        holder.tvEventLocation.setText(event.getEventPlace());


        @SuppressLint("DiscouragedApi") int imageResource = context.getResources()
                .getIdentifier(
                        event.getEventImage(),
                        "drawable",
                        context.getPackageName()
                );

        if (imageResource != 0) {
            holder.ivEventImage.setImageResource(imageResource);
        }else {
            holder.ivEventImage.setImageResource(R.drawable.aisummit);

        }


        holder.btnEventArrow.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    eventdetailactivity.class
            );

            intent.putExtra(
                    "eventName",
                    event.getEventName()
            );

            intent.putExtra(
                    "eventDate",
                    event.getEventDate()
            );

            intent.putExtra(
                    "eventTime",
                    event.getEventTime()
            );

            intent.putExtra(
                    "eventPlace",
                    event.getEventPlace()
            );

            intent.putExtra(
                    "eventAddress",
                    event.getEventAddress()
            );

            intent.putExtra(
                    "ticketPrice",
                    event.getTicketPrice()
            );

            intent.putExtra(
                    "eventDescription",
                    event.getEventDescription()
            );

            intent.putExtra(
                    "eventImage",
                    imageResource
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder
            extends RecyclerView.ViewHolder {

        ImageView ivEventImage;
        TextView tvEventName;
        TextView tvEventLocation;
        ImageButton btnEventArrow;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            ivEventImage =
                    itemView.findViewById(R.id.ivEventImage);

            tvEventName =
                    itemView.findViewById(R.id.tvEventName);

            tvEventLocation =
                    itemView.findViewById(R.id.tvEventLocation);

            btnEventArrow =
                    itemView.findViewById(R.id.btnEventArrow);
        }
    }
}