package ru.rewindforce.concerts.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import io.supercharge.shimmerlayout.ShimmerLayout;
import ru.rewindforce.concerts.R;
import ru.rewindforce.concerts.data.Band;

public class BandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int   VIEW_TYPE_LOADING = 0,
                        VIEW_TYPE_BAND = 1;

    private ArrayList<Band> bandsArrayList;
    private Context context;

    public BandAdapter(Context context, ArrayList<Band> bandsArrayList) {
        this.context = context;
        this.bandsArrayList = bandsArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_LOADING: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_band_loading, viewGroup, false);
                break;
            }
            case VIEW_TYPE_BAND: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_band, viewGroup, false);
                break;
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_band_loading, viewGroup, false);
                break;
            }
        }

        return viewType == VIEW_TYPE_LOADING ? new LoadingViewHolder(view) : new BandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_BAND) {
            BandViewHolder currentViewHolder = (BandViewHolder) viewHolder;
            Band band = bandsArrayList.get(position);

            currentViewHolder.band.setText(band.getBandName());
            String genreAndCountry = band.getBandCountry()+", "+band.getGenre();
            currentViewHolder.genreAndCountry.setText(genreAndCountry);
            RequestBuilder<Drawable> thumbnailRequest = Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_band));
            Glide.with(context).load(band.getAvatarURL())
                               .thumbnail(thumbnailRequest)
                               .apply(new RequestOptions().circleCrop())
                               .into(currentViewHolder.thumbnail);
        } else {
            LoadingViewHolder currentViewHolder = (LoadingViewHolder) viewHolder;
            currentViewHolder.shimmerView.startShimmerAnimation();
        }
    }

    @Override
    public int getItemViewType(int position) {
       return bandsArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_BAND;
    }

    @Override
    public int getItemCount() {
        return bandsArrayList.size();
    }

    class BandViewHolder extends RecyclerView.ViewHolder {

        TextView band, genreAndCountry;
        ImageView thumbnail;

        BandViewHolder(View view) {
            super(view);

            genreAndCountry = view.findViewById(R.id.band_country_and_genre);
            band = view.findViewById(R.id.band_name);
            thumbnail = view.findViewById(R.id.band_icon);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ShimmerLayout shimmerView;
        LoadingViewHolder(View view) {
            super(view);

            shimmerView = view.findViewById(R.id.info_shimmer);
        }
    }
}
