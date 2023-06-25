package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MyRewardsFragment extends Fragment {

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    private RecyclerView rewardsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        rewardsRecyclerView = view.findViewById(R.id.my_rewards_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rewardsRecyclerView.setLayoutManager(layoutManager);

        List<RewardModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new RewardModel("Cashback", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));
        rewardModelList.add(new RewardModel("Cashback", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));
        rewardModelList.add(new RewardModel("Discount", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));
        rewardModelList.add(new RewardModel("Buy 1 get 1 free", "till 2nd June, 2016", "GET 20% CASHBACK on any product above Rs. 200/- and below Rs. 30000/-"));

        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(rewardModelList, false);
        rewardsRecyclerView.setAdapter(myRewardsAdapter);
        myRewardsAdapter.notifyDataSetChanged();

        return view;
    }
}