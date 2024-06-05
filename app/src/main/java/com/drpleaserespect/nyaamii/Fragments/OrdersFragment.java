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
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.Fragments.OrdersFragment.OrdersAdapter.OnClickListener;
import com.drpleaserespect.nyaamii.Fragments.OrdersFragment.OrdersAdapter.ViewHolder;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.LoaderViewModel;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrdersFragment extends Fragment {
    private static final String TAG = "OrdersFragment";
    boolean ListLoaded = false;
    private OrdersAdapter Adapter = null;

    private NyaamiDatabase DB_instance = null;

    private CompositeDisposable mDisposable = new CompositeDisposable();
    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrdersViewModel ordersViewModel = new ViewModelProvider(requireActivity()).get(OrdersViewModel.class);
        LoaderViewModel loaderViewModel = new ViewModelProvider(requireActivity()).get(LoaderViewModel.class);
        if (mDisposable.isDisposed()) {
            mDisposable = new CompositeDisposable();
        }
        DB_instance = NyaamiDatabase.getInstance(requireContext());


        RecyclerView rec = view.findViewById(id.OrdersRecyclerView);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposable.dispose();
    }

    private void InitAdapter(OrdersAdapter Adapter, OrdersViewModel viewModel) {
        Adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClickClose(ViewHolder viewHolder, OrderWithItem orderObject) {
                mDisposable.add(
                        DB_instance.userDao().deleteOrder(orderObject.order).subscribeOn(Schedulers.io())
                                .subscribe(() -> {}, throwable -> {
                                    Log.e(TAG, "onClickClose: " + throwable.getMessage());
                                })
                );




            }

            @Override
            public void onClickQuantityAdd(ViewHolder viewHolder, OrderWithItem orderObject) {


                UserDao userDao = DB_instance.userDao();
                orderObject.order.OrderQuantity += 1;
                mDisposable.add(
                        userDao.updateOrder(orderObject.order)
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> {}, throwable -> {
                            Log.e(TAG, "onClickQuantityAdd: " + throwable.getMessage());
                        }));

            }

            @Override
            public void onClickQuantityRemove(ViewHolder viewHolder, OrderWithItem orderObject) {

                UserDao userDao = DB_instance.userDao();
                orderObject.order.OrderQuantity -= 1;
                Completable completable_obj;
                if (orderObject.order.OrderQuantity <= 0) {
                    completable_obj = userDao.deleteOrder(orderObject.order);
                } else {
                    completable_obj = userDao.updateOrder(orderObject.order);
                }

                mDisposable.add(completable_obj
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> {}, throwable -> {
                    Log.e(TAG, "onClickQuantityRemove: " + throwable.getMessage());
                }));

            }

            @Override
            public void onEditQuantity(ViewHolder viewHolder, OrderWithItem orderObject, String quantity) {
                // Turn quantity into an integer
                int quantity_int;
                try {
                    quantity_int = Integer.parseInt(quantity);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "onEditQuantity: ", e);
                    quantity_int = 0;
                }
                Completable completable_obj;
                UserDao userDao = DB_instance.userDao();
                if (quantity_int <= 0) {
                    completable_obj = userDao.deleteOrder(orderObject.order);
                } else {
                    orderObject.order.OrderQuantity = quantity_int;
                    completable_obj = userDao.updateOrder(orderObject.order);
                }
                mDisposable.add(completable_obj
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> {}, throwable -> {
                    Log.e(TAG, "onEditQuantity: " + throwable.getMessage());
                }));
            }
        });
    }

    public static class OrdersAdapter extends ListAdapter<OrderWithItem, ViewHolder> {

        public static final ItemCallback<OrderWithItem> DIFF_CALLBACK = new ItemCallback<OrderWithItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull OrderWithItem oldItem, @NonNull OrderWithItem newItem) {
                //Log.d(TAG, "areItemsTheSame: " + oldItem.getItem().equalsID(newItem.getItem()));
                return oldItem.item.equalsID(newItem.item);
            }

            @Override
            public boolean areContentsTheSame(@NonNull OrderWithItem oldItem, @NonNull OrderWithItem newItem) {
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
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(layout.layout_storeitemcart, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            OrderWithItem orderObject = getItem(position);
            viewHolder.getItemNameView().setText(orderObject.item.getName());
            viewHolder.getItemPriceView().setText(orderObject.item.getPriceString());
            viewHolder.getItemQuantityView().setText(String.valueOf(orderObject.order.OrderQuantity));
            Glide.with(viewHolder.itemView)
                    .load(orderObject.item.getImageUrl())
                    .into(viewHolder.getItemImageView());

            if (!CartMode) {
                viewHolder.getCloseButton().setVisibility(View.GONE);
                viewHolder.getQuantityLayout().setVisibility(View.GONE);
            }

            viewHolder.getCloseButton().setOnClickListener(v -> {
                if (onClickListener != null) onClickListener.onClickClose(viewHolder, orderObject);
            });
            viewHolder.getQuantityAddButton().setOnClickListener(v -> {
                if (onClickListener != null)
                    onClickListener.onClickQuantityAdd(viewHolder, orderObject);
            });
            viewHolder.getQuantityRemoveButton().setOnClickListener(v -> {
                if (onClickListener != null)
                    onClickListener.onClickQuantityRemove(viewHolder, orderObject);
            });
            viewHolder.getItemQuantityView().setOnEditorActionListener((v, actionId, event) -> {
                Log.d(TAG, "onBindViewHolder: EditorActionListener: " + actionId);
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)
                    if (onClickListener != null) onClickListener.onEditQuantity(
                            viewHolder, orderObject,
                            v.getText().toString()
                    );
                return true;
            });
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public interface OnClickListener {
            void onClickClose(ViewHolder viewHolder, OrderWithItem orderObject);

            void onClickQuantityAdd(ViewHolder viewHolder, OrderWithItem orderObject);

            void onClickQuantityRemove(ViewHolder viewHolder, OrderWithItem orderObject);

            void onEditQuantity(ViewHolder viewHolder, OrderWithItem orderObject, String quantity);
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
                ItemImageView = view.findViewById(id.ItemImage);
                ItemNameView = view.findViewById(id.ItemName);
                ItemPriceView = view.findViewById(id.ItemPrice);
                ItemQuantityView = view.findViewById(id.QuantityEditText);
                CloseButton = view.findViewById(id.CloseButton);
                QuantityAddButton = view.findViewById(id.QuantityAddButton);
                QuantityRemoveButton = view.findViewById(id.QuantityRemoveButton);
                QuantityLayout = view.findViewById(id.QuantityCardView);
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