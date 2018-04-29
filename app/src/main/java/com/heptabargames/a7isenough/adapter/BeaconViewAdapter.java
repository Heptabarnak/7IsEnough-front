package com.heptabargames.a7isenough.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Dimension;
import com.heptabargames.a7isenough.R;
import com.heptabargames.a7isenough.listeners.AccordionClickListener;
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
        final TextView monument_desc = holder.accordion_text;
        monument_desc.setText(beacons.get(position).getMonumentDescription());
        final ImageButton acc_button = holder.accordion_button;
        acc_button.setOnClickListener(new AccordionClickListener(acc_button, monument_desc));
        LinearLayout diff_layout = holder.difficultyLayout;
        int note = beacons.get(position).getDifficulty();
        ColorStateList color;
        if(note <3){
            color = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.EasyDifficulty));
        }else if (note <=4){
            color = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.MediumDifficulty));
        }else{
            color = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.HardDifficulty));
        }
        diff_layout.removeAllViews();
        for (int i = 0; i< note; i++){
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.ic_whatshot_black_24dp);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.beacon_difficulty_size), (int)context.getResources().getDimension(R.dimen.beacon_difficulty_size));
            imageView.setLayoutParams(parms);
            imageView.setImageTintList(color);
            diff_layout.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title;
        private TextView tv_desc;
        private ImageButton accordion_button;
        private TextView accordion_text;
        private LinearLayout difficultyLayout;
        private ImageView[] iv_difficulty = new ImageView[5];

        public BeaconViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.beacon_title);
            tv_desc = (TextView) itemView.findViewById(R.id.beacon_desc);
            accordion_button = (ImageButton) itemView.findViewById(R.id.beacon_monument_dropdown);
            accordion_text = (TextView) itemView.findViewById(R.id.beacon_monument_description);
            difficultyLayout = (LinearLayout) itemView.findViewById(R.id.beacon_difficulty_layout);
            iv_difficulty[0] = itemView.findViewById(R.id.beacon_difficulty_1);
            iv_difficulty[1] = itemView.findViewById(R.id.beacon_difficulty_2);
            iv_difficulty[2] = itemView.findViewById(R.id.beacon_difficulty_3);
            iv_difficulty[3] = itemView.findViewById(R.id.beacon_difficulty_4);
            iv_difficulty[4] = itemView.findViewById(R.id.beacon_difficulty_5);
        }
    }
}
