package ca.hajjar.drivefree;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import ca.hajjar.drivefree.models.Config;
import ca.hajjar.drivefree.util.IabHelper;


/**
 * SettingsFragment for all setup related functionality for the App
 */
public class SettingsFragment extends Fragment {
    public static final String PREFS_SETTINGS = "SpeedCheckerPreferences";

    OnConfigurationChangeListener mCallback;
    private Config mConfig;
    IabHelper mHelper;


    public interface OnConfigurationChangeListener {
        public void onConfigurationUpdate(Config config);
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = Config.loadConfig(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch toggleSwitch = (Switch) view.findViewById(R.id.toggleFeatureSwitch);
        toggleSwitch.setChecked(mConfig.isEnabled());
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mConfig.setEnabled(isChecked);
                mCallback.onConfigurationUpdate(mConfig);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnConfigurationChangeListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnConfigurationChangeListener");
        }
    }


}
