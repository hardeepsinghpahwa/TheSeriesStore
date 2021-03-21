package com.example.login.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.login.Cart;
import com.example.login.Home;
import com.example.login.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class SubCategories extends Fragment {

    RecyclerView recyclerView, recyclerView2;
    ArrayList<String> its;
    FirebaseRecyclerAdapter<itemdetails, ItemViewHolder> firebaseRecyclerAdapter;
    ProgressBar progressBar;

    public SubCategories() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_categories, container, false);


        recyclerView = view.findViewById(R.id.listrecyview);
        recyclerView2 = view.findViewById(R.id.productsrecyview);
        progressBar=view.findViewById(R.id.progressbar);

        Query query = FirebaseDatabase.getInstance().getReference().child("Items");

        view.findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Cart.class));
                customType(getActivity(),"fadein-to-fadeout");
            }
        });


        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull itemdetails model) {

//                holder.category.setText(model.getCategory());
                holder.name.setText(model.getName());
                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                holder.price.setText(format.format(new BigDecimal(model.getPrice())));
                //    holder.rating.setText(model.getRating());
                Glide.with(getActivity()).load(model.getImage()).into(holder.image);
            }

            @Override
            public void onViewAttachedToWindow(@NonNull ItemViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layoutanim);
                recyclerView2.setLayoutAnimation(animation);
                progressBar.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.productitem, parent, false);
                return new ItemViewHolder(view1);
            }
        };

        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                its = new ArrayList<>();
                its.add("All");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!its.contains(dataSnapshot.child("subcategory").getValue(String.class))) {
                        its.add(dataSnapshot.child("subcategory").getValue(String.class));
                    }
                }

                recyclerView.setAdapter(new SubAdapter(its));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView2.setAdapter(firebaseRecyclerAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.listitem);
        }
    }

    public class SubAdapter extends RecyclerView.Adapter<SubCategoryViewHolder> {
        ArrayList<String> items;

        public SubAdapter(ArrayList<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
            return new SubCategoryViewHolder(view1);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

            holder.name.setText(items.get(position));

            Home home=(Home)getActivity();
            Log.i("ct",home.cat);

            if(home.cat.equals(items.get(position)))
            {
                holder.name.setTextSize(18);
                holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
                holder.name.setTextColor(Color.BLACK);
                home.cat="";



                Query query = FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("subcategory").equalTo(holder.name.getText().toString());


                FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                        .setQuery(query, new SnapshotParser<itemdetails>() {
                            @NonNull
                            @Override
                            public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                            }
                        }).build();

                firebaseRecyclerAdapter.updateOptions(options);
            }

            if (position == 0 && home.cat.equals("")) {
                holder.name.setTextSize(18);
                holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
                holder.name.setTextColor(Color.BLACK);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);

                    for (int i = 0; i < items.size(); i++) {
                        if (position == i) {
                            TextView textView =null;

                            View view=recyclerView.getLayoutManager().findViewByPosition(i);

                            if(view!=null)
                                textView=view.findViewById(R.id.listitem);
                            if (textView != null) {
                                textView.setTextSize(18);
                                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

                                if (textView.getText().toString().equals("All")) {

                                    Query query = FirebaseDatabase.getInstance().getReference().child("Items");


                                    FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                                            .setQuery(query, new SnapshotParser<itemdetails>() {
                                                @NonNull
                                                @Override
                                                public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                                    return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                                                }
                                            }).build();

                                    firebaseRecyclerAdapter.updateOptions(options);
                                } else {

                                    Query query = FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("subcategory").equalTo(textView.getText().toString());


                                    FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                                            .setQuery(query, new SnapshotParser<itemdetails>() {
                                                @NonNull
                                                @Override
                                                public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                                    return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                                                }
                                            }).build();

                                    firebaseRecyclerAdapter.updateOptions(options);
                                }
                            }

                        } else {
                            TextView textView=null;
                            View view = recyclerView.getLayoutManager().findViewByPosition(i);
                            if(view!=null)
                                textView=view.findViewById(R.id.listitem);
                            if (textView != null) {
                                textView.setTextSize(14);
                                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                            }
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.listitem);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name, price;
        ImageView image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
        }
    }

}