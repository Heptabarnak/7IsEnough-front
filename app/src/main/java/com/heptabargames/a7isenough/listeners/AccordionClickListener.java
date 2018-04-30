package com.heptabargames.a7isenough.listeners;

import android.view.View;
import android.widget.ImageButton;

import com.heptabargames.a7isenough.R;

public class AccordionClickListener implements View.OnClickListener {
    private ImageButton dropdown_icon;
    private View dropdown_view;

    public AccordionClickListener(ImageButton dropdown_icon, View dropdown_view) {
        this.dropdown_icon = dropdown_icon;
        this.dropdown_view = dropdown_view;
    }

    @Override
    public void onClick(View v) {
        if(dropdown_view.getVisibility() == View.VISIBLE){
            dropdown_icon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            dropdown_view.setVisibility(View.GONE);
        }else{
            dropdown_icon.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            dropdown_view.setVisibility(View.VISIBLE);
        }
    }
}
