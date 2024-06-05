package com.drpleaserespect.nyaamii.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.drpleaserespect.nyaamii.Activities.ProductDetailActivity;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.StoreItemsCarouselViewModel;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class StoreItemsCarouselFragment extends Fragment {
    private final String TAG = "StoreItemsCarouselFragment";
    public StoreItemsCarouselFragment() {
        super(layout.fragment_store_items_carousel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageCarousel carousel = view.findViewById(id.carousel);
        carousel.registerLifecycle(getViewLifecycleOwner());
        StoreItemsCarouselViewModel viewModel = new ViewModelProvider(requireActivity()).get(StoreItemsCarouselViewModel.class);
        viewModel.getStoreItems().observe(getViewLifecycleOwner(), storeItems -> {
            List<CarouselItem> items = new ArrayList<>();
            for (StoreItem item : storeItems) {
                Glide.with(requireContext()).load(item.getImageUrl()).preload();
                if (item.FeaturedItem) {
                    items.add(new CarouselItem(item.getImageUrl(), item.getName()));
                }
            }
            Log.d(TAG, "RAW Items: " + storeItems);
            Log.d(TAG, "Items: " + items);
            carousel.setData(items);
        });

        // Carousel Listener
        carousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }
            @Override
            public void onClick(int position, CarouselItem carouselItem) {
                Log.d(TAG, "Clicked item name: " + viewModel.getStoreItems().getValue().get(position));
                // Start Intent to ProductDetailActivity
                StoreItem item = viewModel.getStoreItems().getValue().get(position);
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra("StoreItem", item);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position, CarouselItem carouselItem) {
            }
        });
    }
}
