package com.example.myapplication;

import static com.example.myapplication.DeliveryActivity.SELECT_ADDRESS;
import static com.example.myapplication.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.myapplication.MyAddressesActivity.refreshItem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {

    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;

    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String name = addressesModelList.get(position).getFullname();
        String address = addressesModelList.get(position).getAddress();
        String pincode = addressesModelList.get(position).getPincode();
        Boolean selected = addressesModelList.get(position).getSelected();
        holder.setData(name, address, pincode, selected, position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private TextView fullname;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionContainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.name_address);
            address = itemView.findViewById(R.id.address_3);
            pincode = itemView.findViewById(R.id.pincode_address);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String userName, String userAddress, String userPincode, Boolean selected, int position){
            fullname.setText(userName);
            address.setText(userAddress);
            pincode.setText(userPincode);

            if (MODE == SELECT_ADDRESS){
                icon.setImageResource(R.drawable.tick2);
                if (selected){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(v -> {
                    if (preSelectedPosition != position){
                        addressesModelList.get(position).setSelected(true);
                        addressesModelList.get(preSelectedPosition).setSelected(false);
                        refreshItem(preSelectedPosition, position);
                        preSelectedPosition = position;
                        DBqueries.selectedAddress = position;
                    }
                });
            } else if (MODE == MANAGE_ADDRESS) {
                  optionContainer.setVisibility(View.GONE);
                  icon.setImageResource(R.drawable.three_dots);
                  icon.setOnClickListener(v -> {
                      optionContainer.setVisibility(View.VISIBLE);
                      refreshItem(preSelectedPosition, preSelectedPosition);
                      preSelectedPosition = position;
                  });

                  itemView.setOnClickListener(v -> {
                      refreshItem(preSelectedPosition, preSelectedPosition);
                      preSelectedPosition = -1;
                  });
            }
        }
    }
}

