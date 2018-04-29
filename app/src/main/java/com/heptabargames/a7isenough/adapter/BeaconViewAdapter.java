package com.heptabargames.a7isenough.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heptabargames.a7isenough.R;
import com.heptabargames.a7isenough.models.Beacon;

import java.util.List;

public class BeaconViewAdapter extends RecyclerView.Adapter<BeaconViewAdapter.BeaconViewHolder> {

    Context context;
    List<Beacon> beacons;

    public BeaconViewAdapter(Context context, List<Beacon> beacons) {
        this.context = context;
        this.beacons = beacons;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_beacon, parent, false);
        BeaconViewHolder beaconViewHolder = new BeaconViewHolder(view);
        return beaconViewHolder;
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        holder.tv_title.setText(beacons.get(position).getName());
        holder.tv_desc.setText(beacons.get(position).getDescription());
        int note = beacons.get(position).getDifficulty();
        for(int i = 0; i < 5; i++){
            if(note >= 1){
                holder.iv_difficulty[i].setImageResource(R.drawable.ic_star_black_24dp);
            }
            else if(note > 0){
                holder.iv_difficulty[i].setImageResource(R.drawable.ic_star_half_black_24dp);
            }
            else {
                holder.iv_difficulty[i].setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            note --;
        }
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title;
        private TextView tv_desc;
        private ImageView[] iv_difficulty = new ImageView[5];

        public BeaconViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.beacon_title);
            tv_desc = (TextView) itemView.findViewById(R.id.beacon_desc);
            iv_difficulty[0] = itemView.findViewById(R.id.beacon_difficulty_1);
            iv_difficulty[1] = itemView.findViewById(R.id.beacon_difficulty_2);
            iv_difficulty[2] = itemView.findViewById(R.id.beacon_difficulty_3);
            iv_difficulty[3] = itemView.findViewById(R.id.beacon_difficulty_4);
            iv_difficulty[4] = itemView.findViewById(R.id.beacon_difficulty_5);
        }
    }
}
