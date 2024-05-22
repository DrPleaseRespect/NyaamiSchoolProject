package com.drpleaserespect.nyaamii;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class StoreItemsFragment extends Fragment {

    public static class StoreItemsAdapter extends ListAdapter<StoreItem,StoreItemsAdapter.ViewHolder> {

        public interface OnClickListener {
            void onClick(int position, StoreItem item);
        }
        private OnClickListener onClickListener;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView ItemName;
            private final TextView Price;
            private final ImageView ItemImage;

            public ViewHolder(View view) {
                super(view);
                ItemName = view.findViewById(R.id.ItemName);
                Price = view.findViewById(R.id.ItemPrice);
                ItemImage = view.findViewById(R.id.imageView);
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
        }

        public StoreItemsAdapter() {
            super(DIFF_CALLBACK);
        }

        public static final DiffUtil.ItemCallback<StoreItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<StoreItem>() {
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
        public StoreItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_storeitem, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getItemNameView().setText(getItem(position).getName());
            viewHolder.getItemPriceView().setText(getItem(position).getPrice() + "PHP");
            Glide.with(viewHolder.itemView).load(getItem(position).getImageUrl()).into(viewHolder.getItemImageView());

            viewHolder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(position, getItem(position));
                }
            });
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
    }

    public StoreItemsFragment() {
        super(R.layout.fragment_store_items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_items, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rec = view.findViewById(R.id.storeItemsRecyclerView);
        rec.setLayoutManager(new GridLayoutManager(getContext(), 2));


        StoreItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(StoreItemViewModel.class);
        StoreItemsAdapter Adapter = new StoreItemsAdapter();
        rec.setAdapter(Adapter);


        viewModel.getStoreItems().observe(getViewLifecycleOwner(), storeItems1 -> {
            Adapter.submitList(storeItems1);
        });

        Adapter.setOnClickListener((pos, item) -> {
            Log.d("Tag", String.format("Item Pos: %s   ItemData: %s", pos, item));
        });
    }
}