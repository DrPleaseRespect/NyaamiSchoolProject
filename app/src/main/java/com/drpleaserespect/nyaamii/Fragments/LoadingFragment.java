package com.drpleaserespect.nyaamii.Fragments;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.LoaderViewModel;

public class LoadingFragment extends Fragment {

    public final String TAG = "LoadingFragment";
    private final ObjectAnimator alpha_animator = null;
    public LoadingFragment() {
        super(layout.fragment_loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_loading, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Loading Fragment is ACTIVE");
        ConstraintLayout layout = view.findViewById(id.LoadingLayout);
        View background = view.findViewById(id.LoadingBackground);
        LoaderViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoaderViewModel.class);

        ObjectAnimator alpha_animator = ObjectAnimator.ofFloat(layout, "alpha", 1.0f, 0.0f);
        alpha_animator.setDuration(300);
        alpha_animator.setStartDelay(500);
        alpha_animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        viewModel.getLoadedObjects().observe(getViewLifecycleOwner(), loaded -> {
            Log.d(TAG, "onViewCreated: LOADED STATUS: " + loaded);
            if (loaded) alpha_animator.start();
        });

        viewModel.getStartDelay().observe(getViewLifecycleOwner(), startDelay -> {
            Log.d(TAG, "onViewCreated: START DELAY: " + startDelay);
            alpha_animator.setStartDelay(startDelay);
        });


    }
}