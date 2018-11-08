package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import jp.wasabeef.glide.transformations.BlurTransformation;
import ru.rewindforce.concerts.R;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ConcertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int VIEW_TYPE_CONCERT = 0,
                      VIEW_TYPE_HEADER = 1;

    private final int VIEW_TYPE_PAST = 0,
                      VIEW_TYPE_OVERVIEW = 1,
                      VIEW_TYPE_UPCOMING = 2;

    private int currentViewType;
    private List<Concert> concertArrayList;
    private Context context;
    private OnConcertClicked onConcertClickedListener;
    private long currentAbsoluteDateTime;

    ConcertAdapter(Context context, List<Concert> concertList, DateTime dateTime, int type) {
        this.context = context;
        concertArrayList = concertList;
        currentViewType = type;
        currentAbsoluteDateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(),
                                               dateTime.getDayOfMonth(), 0, 0).getMillis();
    }

    void setOnConcertClickedListener(OnConcertClicked onConcertClickedListener) {
        this.onConcertClickedListener = onConcertClickedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (currentViewType) {
            case VIEW_TYPE_OVERVIEW: {
                Log.e("VIEWTYPE", String.valueOf(viewType == VIEW_TYPE_HEADER));
                if (viewType == VIEW_TYPE_CONCERT)
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.concert_item_card, viewGroup, false);
                else
                    view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.concerts_header, viewGroup, false);
                break;
            }
            case VIEW_TYPE_UPCOMING: case VIEW_TYPE_PAST: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item_list, viewGroup, false);
                break;
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item_card, viewGroup, false);
                break;
            }
        }

        return viewType == VIEW_TYPE_HEADER ? new ConcertAdapter.HeaderViewHolder(view) : new ConcertAdapter.ConcertViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        switch (currentViewType) {
            case VIEW_TYPE_OVERVIEW:
                return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CONCERT;
            case VIEW_TYPE_UPCOMING: case VIEW_TYPE_PAST:
                return VIEW_TYPE_CONCERT;
            default:
                return VIEW_TYPE_CONCERT;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) != VIEW_TYPE_HEADER) {
            final ConcertViewHolder currentViewHolder = (ConcertViewHolder)viewHolder;
            final Concert concert = concertArrayList.get(position);
            DateTime datetime = new DateTime(concert.getDatetime());
            DateTime absoluteDatetime = new DateTime(datetime.getYear(), datetime.getMonthOfYear(),
                    datetime.getDayOfMonth(), 0, 0);
            String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                    + concert.getDatetime() + "_" + concert.getId() + ".webp";
            String date = datetime.toString(DateTimeFormat.forPattern("d")) + " " +
                    context.getResources().getStringArray(R.array.month)[datetime.getMonthOfYear() - 1] + " " +
                    datetime.toString(DateTimeFormat.forPattern("yyyy")) + " в " +
                    datetime.toString(DateTimeFormat.forPattern("HH:mm"));
            switch (currentViewType) {
                case VIEW_TYPE_OVERVIEW: {
                    Glide.with(context).load(imageURL).thumbnail(0.1f)
                            .apply(new RequestOptions().override(500)).into(currentViewHolder.thumbnail);
                    break;
                } case VIEW_TYPE_PAST: {
                    Interval interval = new Interval(absoluteDatetime.getMillis(), currentAbsoluteDateTime);
                    String days_word = context.getResources().getQuantityString(R.plurals.days_word,
                            (int)interval.toDuration().getStandardDays(), (int)interval.toDuration().getStandardDays());
                    currentViewHolder.days_number.setText(String.valueOf(interval.toDuration().getStandardDays()));
                    currentViewHolder.days_word.setText(days_word);
                    Glide.with(context).load(imageURL).thumbnail(0.1f)
                            .apply(new RequestOptions().override(200))
                            .apply(bitmapTransform(new BlurTransformation(1, 1))).into(currentViewHolder.thumbnail);
                    break;
                } case VIEW_TYPE_UPCOMING: {
                    Interval interval = new Interval(currentAbsoluteDateTime, absoluteDatetime.getMillis());
                    String days_word;
                    Glide.with(context).load(imageURL).thumbnail(0.1f)
                            .apply(new RequestOptions().override(200))
                            .apply(bitmapTransform(new BlurTransformation(1, 1))).into(currentViewHolder.thumbnail);
                    if ((int)interval.toDuration().getStandardDays() > 1) {
                        days_word = context.getResources().getQuantityString(R.plurals.days_word,
                                (int) interval.toDuration().getStandardDays(), (int) interval.toDuration().getStandardDays());
                        currentViewHolder.days_number.setVisibility(View.VISIBLE);
                        currentViewHolder.days_word.setVisibility(View.VISIBLE);
                        currentViewHolder.days_word.setText(days_word);
                        currentViewHolder.days_number.setText(String.valueOf(interval.toDuration().getStandardDays()));
                    } else if ((int)interval.toDuration().getStandardDays() == 1) {
                        days_word = "Завтра";
                        currentViewHolder.days_number.setVisibility(View.VISIBLE);
                        currentViewHolder.days_word.setVisibility(View.GONE);
                        currentViewHolder.days_number.setText(days_word);
                    } else {
                        days_word = "Сегодня";
                        currentViewHolder.days_number.setVisibility(View.VISIBLE);
                        currentViewHolder.days_word.setVisibility(View.GONE);
                        currentViewHolder.days_number.setText(days_word);
                    }
                    switch ((int)interval.toDuration().getStandardDays()) {
                        case 0: case 1: {
                            currentViewHolder.days_number.setTextColor(Color.parseColor("#f44336"));
                            currentViewHolder.days_word.setTextColor(Color.parseColor("#f44336"));
                            break;
                        } case 2: {
                            currentViewHolder.days_number.setTextColor(Color.parseColor("#ff9800"));
                            currentViewHolder.days_word.setTextColor(Color.parseColor("#ff9800"));
                            break;
                        } case 3:
                            currentViewHolder.days_number.setTextColor(Color.parseColor("#ffc107"));
                            currentViewHolder.days_word.setTextColor(Color.parseColor("#ffc107"));
                            break;
                        case 4: {
                            currentViewHolder.days_number.setTextColor(Color.parseColor("#ffeb3b"));
                            currentViewHolder.days_word.setTextColor(Color.parseColor("#ffeb3b"));
                            break;
                        } case 5: {
                            currentViewHolder.days_number.setTextColor(Color.parseColor("#fff59d"));
                            currentViewHolder.days_word.setTextColor(Color.parseColor("#fff59d"));
                            break;
                        }
                    }
                    break;
                }
            }

            currentViewHolder.club.setText(concert.getClub());
            currentViewHolder.date.setText(date);
            currentViewHolder.title.setText(concert.getBand());
            ViewCompat.setTransitionName(currentViewHolder.thumbnail, String.valueOf(concert.getId()));
            currentViewHolder.concertHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConcertAdapter.this.onConcertClickedListener != null)
                        onConcertClickedListener.onConcertClicked(concert);
                }
            });
        } else {
            HeaderViewHolder currentViewHolder = (HeaderViewHolder)viewHolder;
            final ViewPager viewPager = currentViewHolder.viewPager;
            PromotedConcertsPageAdapter cardAdapter  = new PromotedConcertsPageAdapter(context, onConcertClickedListener);
            if(concertArrayList.size() > 1) {

                cardAdapter.addCardItem(concertArrayList.get(3));
                cardAdapter.addCardItem(concertArrayList.get(4));

                cardAdapter.addCardItem(concertArrayList.get(1));
                cardAdapter.addCardItem(concertArrayList.get(2));
                cardAdapter.addCardItem(concertArrayList.get(3));
                cardAdapter.addCardItem(concertArrayList.get(4));

                cardAdapter.addCardItem(concertArrayList.get(1));
                cardAdapter.addCardItem(concertArrayList.get(2));
            }

            viewPager.setAdapter(cardAdapter);
            viewPager.setCurrentItem(2);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) { }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        int curr = viewPager.getCurrentItem();
                        int lastReal = viewPager.getAdapter().getCount() - 3;
                        if (curr <= 1) {
                            viewPager.setCurrentItem(lastReal, false);
                        } else if (curr > lastReal) {
                            viewPager.setCurrentItem(2, false);
                        }
                    }
                }
            });
            viewPager.setOffscreenPageLimit(3);
        }
    }

    @Override
    public int getItemCount() {
        return concertArrayList.size();
    }

    public interface OnConcertClicked {
        void onConcertClicked(Concert concert);
    }

    class ConcertViewHolder extends RecyclerView.ViewHolder {

        View concertHolder;
        TextView title, club, date, days_number, days_word;
        ImageView thumbnail;

        ConcertViewHolder(View view) {
            super(view);

            concertHolder = view.findViewById(R.id.concertHolder);
            title = view.findViewById(R.id.title);
            club = view.findViewById(R.id.club);
            if (currentViewType == VIEW_TYPE_PAST || currentViewType == VIEW_TYPE_UPCOMING) {
                days_number = view.findViewById(R.id.days_number);
                days_word = view.findViewById(R.id.days_word);
            }
            date = view.findViewById(R.id.date);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;

        HeaderViewHolder(View view) {
            super(view);

            viewPager = view.findViewById(R.id.viewPager);
        }
    }
}
