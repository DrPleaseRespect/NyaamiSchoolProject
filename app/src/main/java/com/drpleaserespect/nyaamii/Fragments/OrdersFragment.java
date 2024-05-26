package com.drpleaserespect.nyaamii.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.drpleaserespect.nyaamii.DataObjects.OrderObject;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.ViewModels.LoaderViewModel;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private static final String TAG = "OrdersFragment";
    boolean ListLoaded = false;
    private OrdersAdapter Adapter = null;
    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrdersViewModel ordersViewModel = new ViewModelProvider(requireActivity()).get(OrdersViewModel.class);
        LoaderViewModel loaderViewModel = new ViewModelProvider(requireActivity()).get(LoaderViewModel.class);


        RecyclerView rec = view.findViewById(R.id.OrdersRecyclerView);
        rec.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false)
        );
        Adapter = new OrdersAdapter();
        rec.setAdapter(Adapter);

        ordersViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            Log.d(TAG, "Orders: " + orders);
            Adapter.submitList(new ArrayList<>(orders));
            if (!ListLoaded) {
                loaderViewModel.setStatus(true);
                ListLoaded = true;
            }
        });

        ordersViewModel.getCartMode().observe(getViewLifecycleOwner(), cartMode -> {
            Log.d(TAG, "CartMode: " + cartMode);
            Adapter = new OrdersAdapter(cartMode);
            InitAdapter(Adapter, ordersViewModel);
            rec.setAdapter(Adapter);
        });

        InitAdapter(Adapter, ordersViewModel);
    }

    private void InitAdapter(OrdersAdapter Adapter, OrdersViewModel viewModel) {
        Adapter.setOnClickListener(new OrdersAdapter.OnClickListener() {
            @Override
            public void onClickClose(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject) {
                List<OrderObject> List = viewModel.getOrders().getValue();
                try {
                    List.remove(orderObject);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "onClickClose: ", e);
                }
                Log.d(TAG, "onClickClose: " + List);
                viewModel.setOrders(List);
            }

            @Override
            public void onClickQuantityAdd(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject) {

                List<OrderObject> List = viewModel.getOrders().getValue();
                OrderObject new_order = new OrderObject(orderObject, orderObject.getQuantity() + 1);
                List.set(viewHolder.getBindingAdapterPosition(), new_order);
                Log.d(TAG, "onClickQuantityAdd: " + List);
                viewModel.setOrders(List);

            }

            @Override
            public void onClickQuantityRemove(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject) {

                List<OrderObject> list = viewModel.getOrders().getValue();
                OrderObject Order = orderObject;
                OrderObject new_order = new OrderObject(Order, Order.getQuantity() - 1);
                if (new_order.getQuantity() <= 0) {
                    list.remove(orderObject);
                } else {
                    list.set(viewHolder.getBindingAdapterPosition(), new_order);
                }
                Log.d(TAG, "onClickQuantityRemove: " + list);
                viewModel.setOrders(list);
            }

            @Override
            public void onEditQuantity(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject, String quantity) {
                // Turn quantity into an integer
                int quantity_int;
                try {
                    quantity_int = Integer.parseInt(quantity);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "onEditQuantity: ", e);
                    quantity_int = 0;
                }

                List<OrderObject> List = viewModel.getOrders().getValue();
                OrderObject Order = orderObject;
                OrderObject new_order = new OrderObject(Order, quantity_int);
                if (new_order.getQuantity() <= 0) {
                    List.remove(orderObject);
                } else {
                    List.set(viewHolder.getBindingAdapterPosition(), new_order);
                }
                Log.d(TAG, "onEditQuantity: " + List);
                viewModel.setOrders(List);
            }
        });
    }

    public static class OrdersAdapter extends ListAdapter<OrderObject, OrdersAdapter.ViewHolder> {

        public static final DiffUtil.ItemCallback<OrderObject> DIFF_CALLBACK = new DiffUtil.ItemCallback<OrderObject>() {
            @Override
            public boolean areItemsTheSame(@NonNull OrderObject oldItem, @NonNull OrderObject newItem) {
                //Log.d(TAG, "areItemsTheSame: " + oldItem.getItem().equalsID(newItem.getItem()));
                return oldItem.getItem().equalsID(newItem.getItem());
            }

            @Override
            public boolean areContentsTheSame(@NonNull OrderObject oldItem, @NonNull OrderObject newItem) {
                //Log.d(TAG, "areContentsTheSame: " + oldItem.equals(newItem));
                return oldItem.equals(newItem);
            }

        };
        private OnClickListener onClickListener;
        private boolean CartMode = true;

        public OrdersAdapter() {
            super(DIFF_CALLBACK);
        }

        public OrdersAdapter(boolean CartMode) {
            super(DIFF_CALLBACK);
            this.CartMode = CartMode;
        }

        @Override
        public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_storeitemcart, viewGroup, false);

            return new OrdersAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrdersAdapter.ViewHolder viewHolder, final int position) {
            OrderObject orderObject = getItem(position);
            viewHolder.getItemNameView().setText(orderObject.getItem().getName());
            viewHolder.getItemPriceView().setText(orderObject.getItem().getPriceString());
            viewHolder.getItemQuantityView().setText(String.valueOf(orderObject.getQuantity()));
            Glide.with(viewHolder.itemView)
                    .load(orderObject.getItem().getImageUrl())
                    .into(viewHolder.getItemImageView());

            if (!CartMode) {
                viewHolder.getCloseButton().setVisibility(View.GONE);
                viewHolder.getQuantityLayout().setVisibility(View.GONE);
            }

            viewHolder.getCloseButton().setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickClose(viewHolder, orderObject);

                }
            });
            viewHolder.getQuantityAddButton().setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickQuantityAdd(viewHolder, orderObject);
                }
            });
            viewHolder.getQuantityRemoveButton().setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickQuantityRemove(viewHolder, orderObject);
                }
            });
            viewHolder.getItemQuantityView().setOnEditorActionListener((v, actionId, event) -> {
                Log.d(TAG, "onBindViewHolder: EditorActionListener: " + actionId);
                if ((actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (onClickListener != null) {
                        onClickListener.onEditQuantity(
                                viewHolder, orderObject,
                                v.getText().toString()
                                );
                    }
                }
                return true;
            });
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public interface OnClickListener {
            void onClickClose(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject);

            void onClickQuantityAdd(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject);

            void onClickQuantityRemove(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject);

            void onEditQuantity(OrdersAdapter.ViewHolder viewHolder, OrderObject orderObject, String quantity);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ItemImageView;
            private final TextView ItemNameView;
            private final TextView ItemPriceView;
            private final EditText ItemQuantityView;
            private final ShapeableImageView CloseButton;
            private final ImageView QuantityAddButton;
            private final ImageView QuantityRemoveButton;
            private final MaterialCardView QuantityLayout;


            public ViewHolder(View view) {
                super(view);
                ItemImageView = view.findViewById(R.id.ItemImage);
                ItemNameView = view.findViewById(R.id.ItemName);
                ItemPriceView = view.findViewById(R.id.ItemPrice);
                ItemQuantityView = view.findViewById(R.id.QuantityEditText);
                CloseButton = view.findViewById(R.id.CloseButton);
                QuantityAddButton = view.findViewById(R.id.QuantityAddButton);
                QuantityRemoveButton = view.findViewById(R.id.QuantityRemoveButton);
                QuantityLayout = view.findViewById(R.id.QuantityCardView);
            }

            public ImageView getItemImageView() {
                return ItemImageView;
            }

            public TextView getItemNameView() {
                return ItemNameView;
            }

            public TextView getItemPriceView() {
                return ItemPriceView;
            }

            public EditText getItemQuantityView() {
                return ItemQuantityView;
            }

            public ShapeableImageView getCloseButton() {
                return CloseButton;
            }

            public ImageView getQuantityAddButton() {
                return QuantityAddButton;
            }

            public ImageView getQuantityRemoveButton() {
                return QuantityRemoveButton;
            }

            public MaterialCardView getQuantityLayout() {
                return QuantityLayout;
            }
        }

    }
}