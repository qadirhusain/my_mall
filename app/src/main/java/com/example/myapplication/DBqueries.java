package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBqueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();
    public static List<String> wishlist = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();
    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();
    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();
    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static void loadCategories(RecyclerView categoryRecyclerView, final Context context) {
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                        }
                        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                        categoryRecyclerView.setAdapter(categoryAdapter);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadFragmentData(RecyclerView homePageRecyclerView, Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            if ((long) documentSnapshot.get("view_type") == 0) {
                                List<SliderModel> sliderModelList = new ArrayList<>();
                                long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                for (long x = 1; x < no_of_banners + 1; x++) {
                                    sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()
                                            , documentSnapshot.get("banner_" + x + "_background").toString()));
                                }
                                lists.get(index).add(new HomePageModel(0, sliderModelList));
                            } else if ((long) documentSnapshot.get("view_type") == 1) {
                                lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString()
                                        , documentSnapshot.get("background").toString()));
                            } else if ((long) documentSnapshot.get("view_type") == 2) {

                                List<WishlistModel> viewAllProductList = new ArrayList<>();
                                List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                long no_of_products = (long) documentSnapshot.get("no_of_products");
                                for (long x = 1; x < no_of_products + 1; x++) {
                                    horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                            , documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_title_" + x).toString()
                                            , documentSnapshot.get("product_subtitle_" + x).toString()
                                            , documentSnapshot.get("product_price_" + x).toString()));

                                    viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString()
                                            , documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_full_title_" + x).toString()
                                            , (long) documentSnapshot.get("free_coupons_" + x)
                                            , documentSnapshot.get("average_rating_" + x).toString()
                                            , (long) documentSnapshot.get("total_ratings_" + x)
                                            , documentSnapshot.get("product_price_" + x).toString()
                                            , documentSnapshot.get("cutted_price_" + x).toString()
                                            , (Boolean) documentSnapshot.get("COD_" + x)
                                            , (Boolean) documentSnapshot.get("in_stock_" + x)));
                                }
                                lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));


                            } else if ((long) documentSnapshot.get("view_type") == 3) {
                                List<HorizontalProductScrollModel> GridLayoutModelList = new ArrayList<>();
                                long no_of_products = (long) documentSnapshot.get("no_of_products");
                                for (long x = 1; x < no_of_products + 1; x++) {
                                    GridLayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString()
                                            , documentSnapshot.get("product_image_" + x).toString()
                                            , documentSnapshot.get("product_title_" + x).toString()
                                            , documentSnapshot.get("product_subtitle_" + x).toString()
                                            , documentSnapshot.get("product_price_" + x).toString()));
                                }
                                lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), GridLayoutModelList));
                            }
                        }
                        HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                        homePageRecyclerView.setAdapter(homePageAdapter);
                        homePageAdapter.notifyDataSetChanged();
                        HomeFragment.swipeRefreshLayout.setRefreshing(false);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadWishList(final Context context, Dialog dialog, final Boolean loadProductData) {
        wishlist.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                wishlist.add(task.getResult().get("product_ID_" + x).toString());

                                if (DBqueries.wishlist.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                                    if (ProductDetailsActivity.addToWishlistBtn != null) {
                                        ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.btnREDLight));
                                    }
                                } else {
                                    if (ProductDetailsActivity.addToWishlistBtn != null) {
                                        ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                    }
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                                }

                                if (loadProductData) {
                                    wishlistModelList.clear();
                                    String productID = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        wishlistModelList.add(new WishlistModel(productID
                                                                , task.getResult().get("product_image_1").toString()
                                                                , task.getResult().get("product_title").toString()
                                                                , (long) task.getResult().get("free_coupons")
                                                                , task.getResult().get("average_rating").toString()
                                                                , (long) task.getResult().get("total_ratings")
                                                                , task.getResult().get("product_price").toString()
                                                                , task.getResult().get("cutted_price").toString()
                                                                , (Boolean) task.getResult().get("COD")
                                                                , (Boolean) task.getResult().get("in_stock")));

                                                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromWishList(int index, Context context) {
        String removeProductID = wishlist.get(index);
        wishlist.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishlist.size(); x++) {
            updateWishlist.put("product_ID_" + x, wishlist.get(x));
        }
        updateWishlist.put("list_size", (long) wishlist.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (wishlistModelList.size() != 0) {
                            wishlistModelList.remove(index);
                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                        }
                        ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (ProductDetailsActivity.addToWishlistBtn != null) {
                            ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.btnREDLight));
                        }
                        wishlist.add(index, removeProductID);
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_wishlist_querry = false;
                });
    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishlist.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
    }

    public static void loadRatingList(Context context) {
        myRatedIds.clear();
        myRating.clear();
        if (!ProductDetailsActivity.running_rating_querry) {
            ProductDetailsActivity.running_rating_querry = true;
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {

                                myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                                myRating.add((long) task.getResult().get("rating_" + x));

                                if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                    if (ProductDetailsActivity.rateNowContainer != null) {
                                        ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                    }
                                }

                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_rating_querry = false;
                    });
        }
    }

    public static void loadCartList(final Context context, Dialog dialog, Boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                cartList.add(task.getResult().get("product_ID_" + x).toString());

                                if (DBqueries.cartList.contains(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                                } else {
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                                }

                                if (loadProductData) {
                                    cartItemModelList.clear();
                                    String productID = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        int index = 0;
                                                        if (cartList.size() >= 2) {
                                                            index = cartList.size() - 2;
                                                        }
                                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM, productID
                                                                , task.getResult().get("product_image_1").toString()
                                                                , task.getResult().get("product_title").toString()
                                                                , (long) task.getResult().get("free_coupons")
                                                                , task.getResult().get("product_price").toString()
                                                                , task.getResult().get("cutted_price").toString()
                                                                , (long) 1
                                                                , (long) 0
                                                                , (long) 0
                                                                , (boolean) task.getResult().get("in_stock")));

                                                        if (cartList.size() == 1) {
                                                            cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT));
                                                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                            parent.setVisibility(View.VISIBLE);
                                                        }
                                                        if (cartList.size() == 0) {
                                                            cartItemModelList.clear();
                                                        }

                                                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                            if (cartList.size() != 0) {
                                badgeCount.setVisibility(View.VISIBLE);
                            } else {
                                badgeCount.setVisibility(View.INVISIBLE);
                            }
                            if (DBqueries.cartList.size() < 99) {
                                badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                            } else {
                                badgeCount.setText("99");
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    public static void removeFromCart(int index, Context context, TextView cartTotalAmount) {
        String removeProductID = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartlist = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartlist.put("product_ID_" + x, cartList.get(x));
        }
        updateCartlist.put("list_size", (long) cartList.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartlist).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (cartItemModelList.size() != 0) {
                            cartItemModelList.remove(index);
                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                        }
                        if (cartList.size() == 0) {
                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                            parent.setVisibility(View.GONE);
                            cartItemModelList.clear();
                        }
                        Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        cartList.add(index, removeProductID);
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_cart_querry = false;
                });
    }

    public static void loadAddresses(Context context, Dialog loadingDialog) {

        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Intent deliveryIntent;

                            if ((Long) task.getResult().get("list_size") == 0) {
                                deliveryIntent = new Intent(context, AddAddressActivity.class);
                                deliveryIntent.putExtra("INTENT", "deliveryIntent");
                            } else {

                                for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                                    addressesModelList.add(new AddressesModel(task.getResult().get("fullname_" + x).toString()
                                            , task.getResult().get("address_" + x).toString()
                                            , task.getResult().get("pincode_" + x).toString()
                                            , (Boolean) task.getResult().get("selected_" + x)));

                                    if ((Boolean) task.getResult().get("selected_" + x)) {
                                        selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                                    }
                                }
                                deliveryIntent = new Intent(context, DeliveryActivity.class);
                            }
                            context.startActivity(deliveryIntent);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }
}
