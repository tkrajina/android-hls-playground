package hlsplayground.info.puzz.androidhlsexample;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSpec;

public class HLSEvent {

    public HLSEvent(String title) {
        this.title = title;
    }

    public String title;

    public DataSpec dataSpec;
    public int dataType;
    public int trackType;
    public Format trackFormat;
    public int trackSelectionReason;
    public Object trackSelectionData;
    public long mediaStartTimeMs;
    public long mediaEndTimeMs;
    public long elapsedRealtimeMs;
}
