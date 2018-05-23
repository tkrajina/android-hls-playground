package hlsplayground.info.puzz.androidhlsexample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import hlsplayground.info.puzz.androidhlsexample.databinding.LogEntryBinding;

public class LogAdapter extends ArrayAdapter<HLSEvent> {
    public LogAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId, new ArrayList<HLSEvent>());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LogEntryBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.log_entry, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setEvent(getItem(position));

        return binding.getRoot();
    }
}
