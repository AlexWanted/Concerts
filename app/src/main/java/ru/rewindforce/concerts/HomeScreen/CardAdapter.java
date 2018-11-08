package ru.rewindforce.concerts.HomeScreen;

import androidx.cardview.widget.CardView;

public interface CardAdapter {
    CardView getCardViewAt(int position);
    int getCount();
}