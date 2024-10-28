package com.example.project2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.models.CategoryModel;

import java.util.List;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.ViewHolder> {

    private Context context;
    private List<CategoryModel> list;

    private OnCategoryItemClickListener itemClickListener;

    public CategoryAdaptor(Context context, List<CategoryModel> list, OnCategoryItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel categoryModel = list.get(position);
        holder.bind(categoryModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView catImg;
        TextView catName, price, type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImg = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);
            price = itemView.findViewById(R.id.textView3);



            itemView.setOnClickListener(this);
        }

        public void bind(CategoryModel categoryModel) {
            Glide.with(context).load(categoryModel.getImg_url()).into(catImg);
            catName.setText(categoryModel.getName());
            price.setText("$" + categoryModel.getPrice());

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(list.get(position));
                }
            }
        }
    }

    public interface OnCategoryItemClickListener {
        void onItemClick(CategoryModel categoryModel);
    }
}

