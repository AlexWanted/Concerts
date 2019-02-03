package ru.rewindforce.concerts.home;

import androidx.cardview.widget.CardView;

public interface CardAdapter {
    CardView getCardViewAt(int position);
    int getCount();
}