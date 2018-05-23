package hlsplayground.info.puzz.androidhlsexample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import hlsplayground.info.puzz.androidhlsexample.databinding.ActivityHlsBinding;

public class HLSActivity extends AppCompatActivity implements MediaSourceEventListener, BandwidthMeter.EventListener {

    private static final String TAG = HLSActivity.class.getSimpleName();

    public static final String HLS_URL = "https://s3-us-west-2.amazonaws.com/hls-playground/hls.m3u8";
    //public static final String HLS_URL = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";


    ActivityHlsBinding binding;
    private ArrayAdapter<HLSEvent> logsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For simplicity:
        MediaSourceEventListener eventListener = this;
        BandwidthMeter.EventListener bandwidthMeterEventListener = this;

        // ----------------------------------------------------------------------------------------------------

        String m3u8File = "hls.m3u8";
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            int type = activeNetwork.getType();
            int subType = activeNetwork.getSubtype();
            if (type == ConnectivityManager.TYPE_MOBILE && subType == TelephonyManager.NETWORK_TYPE_GPRS) {
                m3u8File = "hls_gprs.m3u8";
            }
        }
        String m3u8URL = "https://s3-us-west-2.amazonaws.com/hls-playground/" + m3u8File;

        // ----------------------------------------------------------------------------------------------------

        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder()
                .setEventListener(mainHandler, bandwidthMeterEventListener)
                .build();

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // ----------------------------------------------------------------------------------------------------

        binding = DataBindingUtil.setContentView(this, R.layout.activity_hls);
        binding.exoplayer.setPlayer(player);

        // ----------------------------------------------------------------------------------------------------

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "example-hls-app"), bandwidthMeter);

        // This is the MediaSource representing the media to be played.
        HlsMediaSource videoSource = new HlsMediaSource(Uri.parse(m3u8URL), dataSourceFactory, 5, mainHandler, eventListener);

        // Prepare the player with the source.
        player.prepare(videoSource);

        // ----------------------------------------------------------------------------------------------------

        logsAdapter = new LogAdapter(this, R.layout.log_entry, android.R.id.text1);
        binding.log.setAdapter(logsAdapter);

    }

    // ----------------------------------------------------------------------------------------------------
    // Methods implemented from MediaSourceEventListener
    // ----------------------------------------------------------------------------------------------------

    @Override
    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        log("Media period created", null, -1, -1, null, -1, null, -1, -1, -1);
    }

    @Override
    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        log("Media period released", null, -1, -1, null, -1, null, -1, -1, -1);
    }

    @Override
    public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        log("load started", loadEventInfo.dataSpec, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                loadEventInfo.elapsedRealtimeMs);
    }

    @Override
    public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        log("load completed", loadEventInfo.dataSpec, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                loadEventInfo.elapsedRealtimeMs);
    }

    @Override
    public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        log("load canceled", loadEventInfo.dataSpec, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                loadEventInfo.elapsedRealtimeMs);
    }

    @Override
    public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        log("load error", loadEventInfo.dataSpec, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                loadEventInfo.elapsedRealtimeMs);
    }

    @Override
    public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        log("reading started", null, -1, -1, null, -1, null, -1, -1, -1);
    }

    @Override
    public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        log("upstream discareded", null, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                -1);
    }

    @Override
    public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        log("downstream format changed", null, mediaLoadData.dataType, mediaLoadData.trackType, mediaLoadData.trackFormat,
                mediaLoadData.trackSelectionReason, mediaLoadData.trackSelectionData, mediaLoadData.mediaStartTimeMs, mediaLoadData.mediaEndTimeMs,
                -1);
    }

    // ----------------------------------------------------------------------------------------------------
    // Methods from BandwidthMeter.EventListener:
    // ----------------------------------------------------------------------------------------------------

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {
        HLSEvent hlsEvent = new HLSEvent(String.format("Bandwith sample elapsedMs=%dms, bytes=%d, bitrate=%d", elapsedMs, bytes, bitrate));
        logEvent(hlsEvent);
    }

    // ----------------------------------------------------------------------------------------------------

    /**
     * Show HLS events in the logs adapter.
     */
    private void log(String title, DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        HLSEvent event = new HLSEvent(title);
        event.dataSpec = dataSpec;
        event.dataType = dataType;
        event.trackType = trackType;
        event.trackFormat = trackFormat;
        event.trackSelectionReason = trackSelectionReason;
        event.trackSelectionData = trackSelectionData;
        event.mediaStartTimeMs = mediaStartTimeMs;
        event.mediaEndTimeMs = mediaEndTimeMs;
        event.elapsedRealtimeMs = elapsedRealtimeMs;
        logEvent(event);
    }

    private void logEvent(HLSEvent event) {
        logsAdapter.insert(event, 0);
        logsAdapter.notifyDataSetChanged();
    }

}
