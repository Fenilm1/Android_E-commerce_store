package com.example.project2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.project2.R;
import com.example.project2.activities.CartActivity;
import com.example.project2.activities.DetailedActivity;
import com.example.project2.adapters.CategoryAdaptor;
import com.example.project2.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdaptor.OnCategoryItemClickListener {

    RecyclerView catRecyclerView,newProductRecyclerView;


    CategoryAdaptor categoryAdaptor;
    List<CategoryModel> categoryModelList;

    FirebaseFirestore db;
    Button cartButton;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_home, container, false);
        catRecyclerView = root.findViewById(R.id.rec_category);


        db = FirebaseFirestore.getInstance();

        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, "Find Massive Discounts when you shop Online", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2, "Get Offers on our new series", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3, "Holiday Sale Coming Soon", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);



        catRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categoryModelList = new ArrayList<>();
        categoryAdaptor = new CategoryAdaptor(getActivity(), categoryModelList, this);
        catRecyclerView.setAdapter(categoryAdaptor);



        db.collection("Category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CategoryModel categoryModel = document.toObject(CategoryModel.class);
                            categoryModelList.add(categoryModel);
                        }
                        categoryAdaptor.notifyDataSetChanged();
                    }
                });

        return root;
    }

    @Override
    public void onItemClick(CategoryModel categoryModel) {

        Intent intent = new Intent(getActivity(), DetailedActivity.class);
        intent.putExtra("categoryModel", categoryModel);
        startActivity(intent);
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}