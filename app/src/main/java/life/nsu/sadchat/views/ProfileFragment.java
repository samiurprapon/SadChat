package life.nsu.sadchat.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import life.nsu.sadchat.R;
import life.nsu.sadchat.utils.OnItemClickListener;

public class ProfileFragment extends Fragment {
    private static ProfileFragment fragment = null;

    static OnItemClickListener onItemClickListener;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(OnItemClickListener onItemClick) {
        if (fragment == null) {
            fragment = new ProfileFragment();
        }

        onItemClickListener = onItemClick;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}