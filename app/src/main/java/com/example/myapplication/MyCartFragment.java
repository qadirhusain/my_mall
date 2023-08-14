package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyCartFragment extends Fragment {

    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continueBtn;
    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private TextView totalAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        //////////   loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////   loading dialog

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(DBqueries.cartItemModelList, totalAmount, true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(v -> {

            DeliveryActivity.cartItemModelList = new ArrayList<>();
            DeliveryActivity.fromCart = true;

            for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
//                if (cartItemModel.getInStock()) {              // This line is crashing the app when user click on continue btn from cart fragment
                    DeliveryActivity.cartItemModelList.add(cartItemModel);
//                }                                            // This line is crashing the app when user click on continue btn from cart fragment
            }
            DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT));

            loadingDialog.show();
            if (DBqueries.addressesModelList.size() == 0) {
                DBqueries.loadAddresses(getContext(), loadingDialog);
            } else {
                loadingDialog.dismiss();
                Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                startActivity(deliveryIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (DBqueries.cartItemModelList.size() == 0) {
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        } else {
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size() - 1).getType() == CartItemModel.CART_TOTAL_AMOUNT) {
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }
    }
}