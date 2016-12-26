package com.yalin.freevideo.explore;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.EpoxyAdapter;
import com.yalin.freevideo.R;
import com.yalin.freevideo.archframework.PresenterImpl;
import com.yalin.freevideo.archframework.UpdatableView;
import com.yalin.freevideo.explore.VideoExploreModel.VideoExploreQueryEnum;
import com.yalin.freevideo.explore.VideoExploreModel.VideoExploreUserActionEnum;
import com.yalin.freevideo.explore.data.MovieData;
import com.yalin.freevideo.explore.epoxy.EpoxyMovieModel_;
import com.yalin.freevideo.injection.ModelProvider;
import com.yalin.freevideo.ui.widget.recyclerview.ItemMarginDecoration;
import com.yalin.freevideo.util.ImageLoader;

import java.util.ArrayList;
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


    private static class VideoAdapter extends EpoxyAdapter {

        private final Activity mHost;

        private final ImageLoader mImageLoader;

        public VideoAdapter(@NonNull Activity activity,
                            @NonNull VideoExploreModel model,
                            @NonNull ImageLoader imageLoader) {
            enableDiffing();

            mHost = activity;
            mImageLoader = imageLoader;

            showModel(model);
        }

        public void update(@NonNull VideoExploreModel model) {
            models.clear();
            showModel(model);
        }

        private void showModel(@NonNull VideoExploreModel model) {
            List<EpoxyMovieModel_> models = new ArrayList<>(model.getMovies().size());
            for (MovieData movieData : model.getMovies()) {
                EpoxyMovieModel_ movieModel = new EpoxyMovieModel_();
                movieModel.clickListener(mCardClickListener)
                        .imageLoader(mImageLoader)
                        .movieData(movieData);
                models.add(movieModel);
            }
            addModels(models);
        }

        private View.OnClickListener mCardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() == null) {
                    return;
                }
                final MovieData movieData = (MovieData) v.getTag();
                if (mHost instanceof VideoExploreActivity) {
                    ((VideoExploreActivity) mHost).playVideo(movieData);
                }
            }
        };

    }
}
