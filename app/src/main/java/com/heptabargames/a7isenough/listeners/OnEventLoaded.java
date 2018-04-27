package com.heptabargames.a7isenough.listeners;

import com.heptabargames.a7isenough.models.Event;

public interface OnEventLoaded {

    void onEvent(Event event);

    void onError(Exception e);
}
