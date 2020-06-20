package codered.codered;

import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();
    private List<Request> requestList;
    private Location location;
    public MainActivity main;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // views in card
        public TextView topTv, messageTv, productTv;
        public ImageView iconImg;
        public android.widget.Button Button;

        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            topTv = v.findViewById(R.id.card_time);
            messageTv = v.findViewById(R.id.card_message);
            productTv = v.findViewById(R.id.card_product);
            iconImg = v.findViewById(R.id.product_icon);
            Button = v.findViewById(R.id.accept_button);
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
        holder.productTv.setText(Request.products[r.getProduct()]);
        holder.iconImg.setImageResource(Request.productIcons[r.getProduct()]);

        int secAgo = Request.secAgo((long)r.getTimestamp());
        String timeSent;
        if (secAgo < 60){
            timeSent =  secAgo + " sec ago";
        } else {
            timeSent = (secAgo / 60) + " min ago";
        }

        String distance = RequestFragment.findDistance(location, r.getLat(), r.getLng())+ " m away";
        holder.topTv.setText(timeSent);

        if (r.getMessage() != null){
            holder.messageTv.setText(r.getMessage());
        } else {
            holder.messageTv.setVisibility(View.GONE);
        }
        holder.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(main, RequestDetailActivity.class);
                i.putExtra("RID", r.getId());
                main.startActivity(i);
            }
        });


    }

    // returns size of list
    @Override
    public int getItemCount() {
        return requestList.size();
    }



}