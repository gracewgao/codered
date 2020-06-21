package codered.codered;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Generates the cards based on data from the Firebase (any requests within the last 30 minutes)

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();
    private List<Request> requestList;
    private Location location;
    public MainActivity main;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // views in card
        public TextView topTv, messageTv, productTv, timeTv;
        public ImageView iconImg;
        public android.widget.Button acceptButton;

        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            topTv = v.findViewById(R.id.card_distance);
            messageTv = v.findViewById(R.id.card_message);
            productTv = v.findViewById(R.id.card_product);
            iconImg = v.findViewById(R.id.product_icon);
            timeTv = v.findViewById(R.id.card_time);
            acceptButton = v.findViewById(R.id.accept_button);
        }
    }

    // constructor
    public ItemAdapter(List<Request> requests, Location l, MainActivity m) {
        requestList = requests;
        location = l;
        main = m;
    }

    // create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new card view
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_card, parent, false);
        MyViewHolder vh = new MyViewHolder(card);
        return vh;
    }

    // replaces the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Request r = requestList.get(position);

        long time = (long) r.getMeetTime();
        String message = "";
        Format format;
        Date recordedTime = new Date(time);
        if (recordedTime.before(new Date())){
            message = "ASAP";
        } else {
            format = new SimpleDateFormat("h:mm a");
            message = format.format(recordedTime);
        }

        holder.productTv.setText(Request.products[r.getProduct()] + "   |   " + message);
        holder.iconImg.setImageResource(Request.productIcons[r.getProduct()]);

        int secAgo = Request.secAgo((long)r.getTimestamp());
        String timeSent;
        if (secAgo < 60){
            timeSent =  "Posted " + secAgo + " sec ago";
        } else {
            timeSent = "Posted " + (secAgo / 60) + " min ago";
        }
        holder.timeTv.setText(timeSent);
        String distance = RequestFragment.findDistance(location, r.getLat(), r.getLng())+ " m away";
        holder.topTv.setText(distance);

        if (r.getMessage() != null){
            holder.messageTv.setText(r.getMessage());
        } else {
            holder.messageTv.setVisibility(View.GONE);
        }

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(main)
                        .setTitle("Accept Request")
                        .setMessage("Will you help out with this codeRED by lending a " + (Request.products[r.getProduct()]).toLowerCase() + "?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(main, RequestDetailActivity.class);
                                i.putExtra("RID", r.getId());
                                main.startActivity(i);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


    }

    // returns size of list
    @Override
    public int getItemCount() {
        return requestList.size();
    }



}