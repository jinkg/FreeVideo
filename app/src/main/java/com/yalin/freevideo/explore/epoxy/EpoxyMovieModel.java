package com.yalin.freevideo.explore.epoxy;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.yalin.freevideo.R;
import com.yalin.freevideo.explore.data.MovieData;
import com.yalin.freevideo.util.ImageLoader;
import com.yalin.freevideo.util.TimeUtils;

/**
 * YaLin
 * 2016/12/26.
 */

public class EpoxyMovieModel extends EpoxyModelWithHolder<EpoxyMovieModel.MovieCardViewHolder> {

    @EpoxyAttribute
    MovieData movieData;
    @EpoxyAttribute(hash = false)
    ImageLoader imageLoader;
    @EpoxyAttribute(hash = false)
    View.OnClickListener clickListener;

    @Override
    protected MovieCardViewHolder createNewHolder() {
        return new MovieCardViewHolder();
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.video_explore_movie_card;
    }

    @Override
    public void bind(MovieCardViewHolder holder) {
        if (!TextUtils.isEmpty(movieData.getCoverUrl())) {
            imageLoader.loadImage(movieData.getCoverUrl(), holder.coverImage, true);
        }
        holder.timeLong.setText(TimeUtils.parseTimeMillisecond(movieData.getTimeLong()));
        holder.title.setText(movieData.getMovieName());

        holder.card.setTag(movieData);
        holder.card.setOnClickListener(clickListener);
    }

    @Override
    public void unbind(MovieCardViewHolder holder) {
        holder.card.setOnClickListener(null);
    }

    static class MovieCardViewHolder extends EpoxyHolder {
        CardView card;
        ImageView coverImage;
        TextView timeLong;
        TextView title;

        @Override
        protected void bindView(View itemView) {
            card = (CardView) itemView;
            coverImage = (ImageView) card.findViewById(R.id.movie_cover);
            timeLong = (TextView) card.findViewById(R.id.movie_time_long);
            title = (TextView) card.findViewById(R.id.movie_title);
        }
    }
}
