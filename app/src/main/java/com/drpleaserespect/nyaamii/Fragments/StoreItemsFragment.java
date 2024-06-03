package com.drpleaserespect.nyaamii.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.drpleaserespect.nyaamii.Activities.ProductDetailActivity;
import com.drpleaserespect.nyaamii.Fragments.StoreItemsFragment.StoreItemsAdapter.ViewHolder;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.StoreItemViewModel;

public class StoreItemsFragment extends Fragment {

    public static class StoreItemsAdapter extends ListAdapter<StoreItem, ViewHolder> {

        public interface OnClickListener {
            void onClick(int position, StoreItem item);
        }
        private OnClickListener onClickListener;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView ItemName;
            private final TextView Price;
            private final ImageView ItemImage;
            private final ConstraintLayout LoadingLayout;

            public ViewHolder(View view) {
                super(view);
                ItemName = view.findViewById(id.ItemName);
                Price = view.findViewById(id.ItemPrice);
                ItemImage = view.findViewById(id.CartImage);
                LoadingLayout = view.findViewById(id.LoadingLayout);
            }

            public TextView getItemNameView() {
                return ItemName;
            }

            public TextView getItemPriceView() {
                return Price;
            }

            public ImageView getItemImageView() {
                return ItemImage;
            }

            public ConstraintLayout getLoadingLayout() { return LoadingLayout; }
        }

        public StoreItemsAdapter() {
            super(DIFF_CALLBACK);
        }

        public static final ItemCallback<StoreItem> DIFF_CALLBACK = new ItemCallback<StoreItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull StoreItem oldItem, @NonNull StoreItem newItem) {
                return oldItem.equalsID(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull StoreItem oldItem, @NonNull StoreItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(layout.layout_storeitem, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getItemNameView().setText(getItem(position).getName());
            viewHolder.getItemPriceView().setText(getItem(position).getPriceString());
            Glide.with(viewHolder.itemView).load(getItem(position).getImageUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                    viewHolder.getLoadingLayout().setVisibility(View.GONE);
                    return false;
                }
            }).into(viewHolder.getItemImageView());

            viewHolder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) onClickListener.onClick(position, getItem(position));
            });
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
    }

    public StoreItemsFragment() {
        super(layout.fragment_store_items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_store_items, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rec = view.findViewById(id.storeItemsRecyclerView);
        rec.setLayoutManager(new GridLayoutManager(getContext(), 2));


        StoreItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(StoreItemViewModel.class);
        StoreItemsAdapter Adapter = new StoreItemsAdapter();
        rec.setAdapter(Adapter);


        viewModel.getStoreItems().observe(getViewLifecycleOwner(), storeItems1 -> {
            Adapter.submitList(storeItems1);
        });

        Adapter.setOnClickListener((pos, item) -> {
            Log.d("Tag", String.format("Item Pos: %s   ItemData: %s", pos, item));

            // Start Intent to ProductDetailActivity
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("StoreItem", item);
            startActivity(intent);
        });
    }
}