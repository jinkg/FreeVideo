package com.yalin.freevideo.player;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.yalin.freevideo.AppApplication;
import com.yalin.freevideo.R;
import com.yalin.freevideo.explore.data.MovieData;
import com.yalin.freevideo.ui.widget.dragview.DraggableListener;
import com.yalin.freevideo.ui.widget.dragview.DraggableView;
import com.yalin.freevideo.util.EventLogger;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.UUID;

/**
 * YaLin
 * 2016/12/12.
 */

public class VideoPlayerFragment extends Fragment implements View.OnClickListener,
        ExoPlayer.EventListener,
        PlaybackControlView.VisibilityListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private DraggableView mDraggableView;

    private MovieData mCurrentPlayData;

    private Handler mMainHandler;
    private Timeline.Window mWindow;
    private EventLogger mEventLogger;
    private SimpleExoPlayerView mSimpleExoPlayerView;

    private DataSource.Factory mMediaDataSourceFactory;
    private SimpleExoPlayer mPlayer;
    private MappingTrackSelector mTrackSelector;
    private boolean mPlayerNeedsSource;

    private boolean mShouldAutoPlay;
    private boolean mIsTimelineStatic;
    private int mPlayerWindow;
    private long mPlayerPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);
        mDraggableView = (DraggableView) root.findViewById(R.id.draggable_view);
        mDraggableView.setDraggableListener(mDraggableListener);
        mDraggableView.setClickToMaximizeEnabled(true);
        root.findViewById(R.id.full_screen).setOnClickListener(this);
        initPlayerView(root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    public void playVideo(MovieData movieData) {
        if (!isVisible()) {
            getFragmentManager().beginTransaction()
                    .show(this)
                    .commit();
        }
        mDraggableView.maximize();
        if (mCurrentPlayData != null && mCurrentPlayData.equals(movieData)) {
            return;
        }
        mCurrentPlayData = movieData;
        releasePlayer();
        initializePlayer();
    }

    public boolean handleBackPress() {
        if (isVisible()) {
            hideSelf();
            return true;
        }
        return false;
    }

    private DraggableListener mDraggableListener = new DraggableListener() {
        @Override
        public void onMaximized() {
            mSimpleExoPlayerView.forceHideController(false);
        }

        @Override
        public void onMinimized() {
            mSimpleExoPlayerView.forceHideController(true);
        }

        @Override
        public void onClosedToLeft() {
            hideSelf();
        }

        @Override
        public void onClosedToRight() {
            hideSelf();
        }
    };

    private void hideSelf() {
        releasePlayer();
        getFragmentManager().beginTransaction()
                .hide(this)
                .commit();
    }

    private void initPlayerView(View root) {
        mShouldAutoPlay = true;
        mMediaDataSourceFactory = buildDataSourceFactory(true);
        mMainHandler = new Handler();
        mWindow = new Timeline.Window();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        mSimpleExoPlayerView = (SimpleExoPlayerView) root.findViewById(R.id.player_view);
        mSimpleExoPlayerView.setControllerVisibilityListener(this);
        mSimpleExoPlayerView.requestFocus();
    }

    private void initializePlayer() {
        if (mCurrentPlayData == null) {
            return;
        }
        if (mPlayer == null) {
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), mTrackSelector, new DefaultLoadControl(),
                    drmSessionManager);
            mPlayer.addListener(this);

            mEventLogger = new EventLogger(mTrackSelector);
            mPlayer.addListener(mEventLogger);

            mPlayer.setAudioDebugListener(mEventLogger);
            mPlayer.setVideoDebugListener(mEventLogger);
            mPlayer.setId3Output(mEventLogger);

            mSimpleExoPlayerView.setPlayer(mPlayer);
            if (mIsTimelineStatic) {
                if (mPlayerPosition == C.TIME_UNSET) {
                    mPlayer.seekToDefaultPosition(mPlayerWindow);
                } else {
                    mPlayer.seekTo(mPlayerWindow, mPlayerPosition);
                }
            }
            mPlayer.setPlayWhenReady(mShouldAutoPlay);
            mPlayerNeedsSource = true;
        }
        if (mPlayerNeedsSource) {
            Uri[] uris;
            String[] extensions;
            uris = new Uri[]{mCurrentPlayData.getVideoUri()};
            if (Util.maybeRequestReadExternalStoragePermission(getActivity(), uris)) {
                // The mPlayer will be reinitialized if the permission is granted.
                return;
            }
            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], null);
            }
            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);
            mPlayer.prepare(mediaSource, !mIsTimelineStatic, !mIsTimelineStatic);
            mPlayerNeedsSource = false;
            updateButtonVisibilities();
        }
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mMediaDataSourceFactory), mMainHandler, mEventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mMediaDataSourceFactory), mMainHandler, mEventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mMediaDataSourceFactory, mMainHandler, mEventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mMediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mMainHandler, mEventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid,
                                                                           String licenseUrl, Map<String, String> keyRequestProperties) throws UnsupportedDrmException {
        if (Util.SDK_INT < 18) {
            return null;
        }
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                buildHttpDataSourceFactory(false), keyRequestProperties);
        return new StreamingDrmSessionManager<>(uuid,
                FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mMainHandler, mEventLogger);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mShouldAutoPlay = mPlayer.getPlayWhenReady();
            mPlayerWindow = mPlayer.getCurrentWindowIndex();
            mPlayerPosition = C.TIME_UNSET;
            Timeline timeline = mPlayer.getCurrentTimeline();
            if (timeline != null && timeline.getWindow(mPlayerWindow, mWindow).isSeekable) {
                mPlayerPosition = mPlayer.getCurrentPosition();
            }
            mPlayer.release();
            mPlayer = null;
            mTrackSelector = null;
            mEventLogger = null;
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((AppApplication) getActivity().getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((AppApplication) getActivity().getApplication())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private void updateButtonVisibilities() {
        if (mPlayer == null) {
            return;
        }

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mTrackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                Button button = new Button(getActivity());
                int label;
                switch (mPlayer.getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        label = R.string.audio;
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        label = R.string.video;
                        break;
                    case C.TRACK_TYPE_TEXT:
                        label = R.string.text;
                        break;
                    default:
                        continue;
                }
                button.setText(label);
                button.setTag(i);
                button.setOnClickListener(this);
            }
        }
    }

    private void showControls() {
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_screen:
                if (mCurrentPlayData != null) {
                    startActivity(mCurrentPlayData.buildPlayIntent(getActivity()));
                }
                break;
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        updateButtonVisibilities();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        mIsTimelineStatic = timeline != null && timeline.getWindowCount() > 0
                && !timeline.getWindow(timeline.getWindowCount() - 1, mWindow).isDynamic;
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        updateButtonVisibilities();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mTrackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_video);
            }
            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_audio);
            }
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        mPlayerNeedsSource = true;
        updateButtonVisibilities();
        showControls();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

}
