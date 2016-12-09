package com.yalin.freevideo.explore;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalin.freevideo.R;
import com.yalin.freevideo.archframework.PresenterImpl;
import com.yalin.freevideo.archframework.UpdatableView;
import com.yalin.freevideo.explore.VideoExploreModel.VideoExploreQueryEnum;
import com.yalin.freevideo.explore.VideoExploreModel.VideoExploreUserActionEnum;
import com.yalin.freevideo.explore.data.MovieData;
import com.yalin.freevideo.injection.ModelProvider;
import com.yalin.freevideo.ui.widget.recyclerview.ItemMarginDecoration;
import com.yalin.freevideo.ui.widget.recyclerview.UpdatableAdapter;
import com.yalin.freevideo.util.ImageLoader;
import com.yalin.freevideo.util.TimeUtils;

import java.util.List;

/**
 * YaLin
 * 2016/12/9.
 */

public class VideoExploreFragment extends Fragment
        implements UpdatableView<VideoExploreModel, VideoExploreQueryEnum, VideoExploreUserActionEnum> {

    private RecyclerView mCardList;

    private View mEmptyView;

    private ImageLoader mImageLoader;

    private VideoAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_explore, container, false);
        mCardList = (RecyclerView) root.findViewById(R.id.video_card_list);
        mCardList.setHasFixedSize(true);
        final int cardVerticalMargin = getResources().getDimensionPixelSize(R.dimen.spacing_normal);
        mCardList.addItemDecoration(new ItemMarginDecoration(0, cardVerticalMargin,
                0, cardVerticalMargin));
        mEmptyView = root.findViewById(android.R.id.empty);
        return root;
    }

    private void initPresenter() {
        ModelProvider.setStubModel(new StubVideoExploreModel());
        VideoExploreModel model = ModelProvider.provideVideoExploreModel(
                getDataUri(VideoExploreQueryEnum.VIDEOS),
                getContext(), getLoaderManager());

        PresenterImpl presenter = new PresenterImpl(model, this,
                VideoExploreUserActionEnum.values(), VideoExploreQueryEnum.values());
        presenter.loadInitialQueries();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImageLoader = new ImageLoader(getActivity(), R.drawable.io_logo);
        initPresenter();
    }

    @Override
    public void displayData(VideoExploreModel model, VideoExploreQueryEnum query) {
        if (model.getMovies() != null) {
            if (mAdapter == null) {
                mAdapter = new VideoAdapter(getActivity(), model, mImageLoader);
                mCardList.setAdapter(mAdapter);
            } else {
                mAdapter.update(model);
            }
            mEmptyView.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void displayErrorMessage(VideoExploreQueryEnum query) {

    }

    @Override
    public void displayUserActionResult(VideoExploreModel model,
                                        VideoExploreUserActionEnum userAction,
                                        boolean success) {

    }

    @Override
    public Uri getDataUri(VideoExploreQueryEnum query) {
        return null;
    }

    @Override
    public void addListener(UserActionListener listener) {

    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    private static class VideoAdapter
            extends UpdatableAdapter<VideoExploreModel, RecyclerView.ViewHolder> {

        private final Activity mHost;

        private final LayoutInflater mInflater;

        private final ImageLoader mImageLoader;

        private List mItems;

        public VideoAdapter(@NonNull Activity activity,
                            @NonNull VideoExploreModel model,
                            @NonNull ImageLoader imageLoader) {
            mHost = activity;
            mImageLoader = imageLoader;
            mInflater = LayoutInflater.from(activity);

            mItems = model.getMovies();
        }

        @Override
        public void update(@NonNull VideoExploreModel updatedData) {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return createMovieCardViewHolder(parent);
        }

        private MovieCardViewHolder createMovieCardViewHolder(final ViewGroup parent) {
            final MovieCardViewHolder holder = new MovieCardViewHolder(
                    mInflater.inflate(R.layout.video_explore_movie_card, parent, false));
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    final MovieData movieData = (MovieData) mItems.get(position);
                    mHost.startActivity(movieData.buildIntent(mHost));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            bindMovieCardViewHolder((MovieCardViewHolder) holder, (MovieData) mItems.get(position));
        }

        private void bindMovieCardViewHolder(MovieCardViewHolder holder, MovieData movieData) {
            if (!TextUtils.isEmpty(movieData.getCoverUrl())) {
                mImageLoader.loadImage(movieData.getCoverUrl(), holder.coverImage, true);
            }
            holder.timeLong.setText(TimeUtils.parseTimeMillisecond(movieData.getTimeLong()));
            holder.title.setText(movieData.getMovieName());
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }
    }

    private static class MovieCardViewHolder extends RecyclerView.ViewHolder {
        final CardView card;
        final ImageView coverImage;
        final TextView timeLong;
        final TextView title;

        public MovieCardViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;
            coverImage = (ImageView) card.findViewById(R.id.movie_cover);
            timeLong = (TextView) card.findViewById(R.id.movie_time_long);
            title = (TextView) card.findViewById(R.id.movie_title);
        }
    }
}
