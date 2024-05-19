package com.drpleaserespect.nyaamii;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class StoreItemsFragment extends Fragment {

    public static class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.ViewHolder> {

        public interface OnClickListener {
            void onClick(int position, String item);
        }

        private String[] localDataSet;
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

        public StoreItemsAdapter(String[] dataSet) {
            localDataSet = dataSet;
        }

        @Override
        public StoreItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_storeitem, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getItemNameView().setText(localDataSet[position]);
            viewHolder.getItemPriceView().setText(localDataSet[position]);
            Glide.with(viewHolder.itemView).load("https://drpleaserespect-pi.stargazer-puffin.ts.net/files/yukari.png").into(viewHolder.getItemImageView());

            viewHolder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(position, localDataSet[position]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return localDataSet.length;
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
        StoreItemsAdapter Shit = new StoreItemsAdapter(new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"});
        Shit.setOnClickListener((pos, item) -> {
            Log.d("Tag", String.format("Item Pos: %s   ItemData: %s", pos, item));
        });
        rec.setAdapter(Shit);
        Log.d("Fuck", "FUCK");
    }
}