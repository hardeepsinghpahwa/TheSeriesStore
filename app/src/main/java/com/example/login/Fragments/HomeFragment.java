package com.example.login.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.login.Cart;
import com.example.login.Home;
import com.example.login.ItemDetail;
import com.example.login.Login;
import com.example.login.NewLogin;
import com.example.login.R;
import com.example.login.SliderItem;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class HomeFragment extends Fragment {


    ArrayList<SliderItem> items;
    SliderView sliderView;
    RecyclerView tshirts,contentplatforms,shopbycat;
    FirebaseRecyclerAdapter<itemdetails, ItemViewHolder> firebaseRecyclerAdapter,firebaseRecyclerAdapter3;
    FirebaseRecyclerAdapter<itemdetails,CategoryViewHolder>firebaseRecyclerAdapter2;
    ArrayList<String> names;
    BottomNavigationView bottomNavigationView;
    ShimmerFrameLayout tshirtsshimmer,slideviewshimmer,contentplatformsshimmer;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sliderView = view.findViewById(R.id.imageSlider);

        bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(2);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        tshirtsshimmer=view.findViewById(R.id.tshirtsshimmer);
        contentplatformsshimmer=view.findViewById(R.id.contentplatformsshimmer);
        slideviewshimmer=view.findViewById(R.id.imageslidershimmer);

        tshirtsshimmer.startShimmer();
        contentplatformsshimmer.startShimmer();
        slideviewshimmer.startShimmer();

        tshirts = view.findViewById(R.id.tshirtsrecyview);
        contentplatforms=view.findViewById(R.id.contentplatformsrecyview);
        shopbycat=view.findViewById(R.id.shopbyseriesrecyview);



        view.findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Cart.class));
                customType(getActivity(),"fadein-to-fadeout");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("SliderView").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    items.add(new SliderItem(snapshot1.child("image").getValue(String.class)));
                }

                sliderView.setSliderAdapter(new SliderAdapterExample(getActivity(), items));
                sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
                sliderView.setIndicatorVisibility(false);
                sliderView.setScrollTimeInSec(5);
                sliderView.startAutoCycle();
                YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(sliderView);
                tshirtsshimmer.setVisibility(View.GONE);
                slideviewshimmer.setVisibility(View.GONE);
                contentplatformsshimmer.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Items");

        Query query = databaseReference.orderByChild("product").equalTo("Tshirts");

        Query query2 = FirebaseDatabase.getInstance().getReference().child("Categories");

        FirebaseRecyclerOptions<itemdetails> options = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                    }
                }).build();

        FirebaseRecyclerOptions<itemdetails> options2 = new FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query2, new SnapshotParser<itemdetails>() {
                    @NonNull
                    @Override
                    public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull itemdetails model) {

                holder.name.setText(model.getName());
                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                holder.price.setText(format.format(new BigDecimal(model.getPrice())));
                Glide.with(getActivity()).load(model.getImage()).into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), ItemDetail.class);
                        intent.putExtra("id",firebaseRecyclerAdapter.getItem(position).getId());
                        startActivity(intent);
                        customType(getActivity(),"left-to-right");
                    }
                });
            }

            @Override
            public void onViewAttachedToWindow(@NonNull ItemViewHolder holder) {
                super.onViewAttachedToWindow(holder);
              /*  YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(tshirts);*/
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layoutanimltor);
                tshirts.setLayoutAnimation(animation);
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.tshirtiteme, parent, false);
                return new ItemViewHolder(view1);
            }
        };

        firebaseRecyclerAdapter2=new FirebaseRecyclerAdapter<itemdetails, CategoryViewHolder>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull itemdetails model) {

                holder.name.setText(model.getName());
                Glide.with(getActivity()).load(model.getImage()).into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*View view = bottomNavigationView.findViewById(R.id.page_2);
                        view.performClick();*/
                        bottomNavigationView.setSelectedItemId(R.id.page_2);
                        Home home=(Home)getActivity();
                        home.posi=position;
                        home.cat=firebaseRecyclerAdapter2.getItem(position).getName();
                    }
                });

            }

            @Override
            public void onViewAttachedToWindow(@NonNull CategoryViewHolder holder) {
                super.onViewAttachedToWindow(holder);

           /*     YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(contentplatforms);*/

                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layoutanim);
                contentplatforms.setLayoutAnimation(animation);
            }


            @Override
            public int getItemViewType(int position) {
                if(position%2==0)
                {
                    return 1;
                }
                else return 2;
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if(viewType==1)
                {
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitem, parent, false);
                    return new CategoryViewHolder(view1);
                }
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitem2, parent, false);
                return new CategoryViewHolder(view1);
            }
        };


        tshirts.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
        tshirts.setAdapter(firebaseRecyclerAdapter);
        YoYo.with(Techniques.FadeInDown)
                .duration(1000)
                .playOn(tshirts);

        contentplatforms.setLayoutManager((new LinearLayoutManager(getActivity())));
        contentplatforms.setAdapter(firebaseRecyclerAdapter2);


        FirebaseDatabase.getInstance().getReference().child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                names=new ArrayList<>();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(!names.contains(dataSnapshot.child("subcategory").getValue(String.class)))
                    {
                        names.add(dataSnapshot.child("subcategory").getValue(String.class));
                    }
                }
                shopbycat.setLayoutManager(new LinearLayoutManager(getActivity()));
                shopbycat.setAdapter(new SubAdapter(names));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter2.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(firebaseRecyclerAdapter3!=null)
            firebaseRecyclerAdapter3.stopListening();

        if(firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.stopListening();

        if(firebaseRecyclerAdapter2!=null)
            firebaseRecyclerAdapter2.stopListening();
    }

    public class SubAdapter extends RecyclerView.Adapter<SubCategoryViewHolder>
    {
        ArrayList<String> items;

        public SubAdapter(ArrayList<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategoryitem, parent, false);
            return new SubCategoryViewHolder(view1);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

            holder.name.setText(items.get(position));


            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bottomNavigationView.setSelectedItemId(R.id.page_3);
                    Home home=(Home)getActivity();
                    home.posi=position;
                    home.cat=items.get(position);

                }
            });

            Query query2 = FirebaseDatabase.getInstance().getReference().child("Items").orderByChild("subcategory").equalTo(items.get(position));

            FirebaseRecyclerOptions<itemdetails> options2 = new FirebaseRecyclerOptions.Builder<itemdetails>()
                    .setQuery(query2, new SnapshotParser<itemdetails>() {
                        @NonNull
                        @Override
                        public itemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                            return new itemdetails(snapshot.child("image").getValue(String.class), snapshot.child("name").getValue(String.class), snapshot.child("rating").getValue(String.class), snapshot.child("category").getValue(String.class), snapshot.child("price").getValue(String.class), snapshot.getKey());
                        }
                    }).build();

            firebaseRecyclerAdapter3=new FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options2) {
                @Override
                protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull itemdetails model) {
//                holder.category.setText(model.getCategory());
                    holder.name.setText(model.getName());
                    holder.price.setText(model.getPrice());
                    //    holder..setText(model.getRating());
                    Glide.with(getActivity()).load(model.getImage()).into(holder.image);
                }

                @NonNull
                @Override
                public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.tshirtiteme, parent, false);
                    return new ItemViewHolder(view1);
                }
            };

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
            holder.recyclerView.setAdapter(firebaseRecyclerAdapter3);

            firebaseRecyclerAdapter3.startListening();

        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    public class SliderAdapterExample extends
            SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

        private Context context;
        private List<SliderItem> mSliderItems;

        public SliderAdapterExample(Context context, List<SliderItem> mSliderItems) {
            this.context = context;
            this.mSliderItems = mSliderItems;
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
            return new SliderAdapterExample.SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {


            SliderItem sliderItem = mSliderItems.get(position);

            Glide.with(viewHolder.itemView)
                    .load(sliderItem.getImage())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            return mSliderItems.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.image);
                this.itemView = itemView;
            }
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

    private class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

    private class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RecyclerView recyclerView;
        ConstraintLayout constraintLayout;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            recyclerView = itemView.findViewById(R.id.subcategoryrecyview);
            constraintLayout=itemView.findViewById(R.id.cons5);
        }
    }


}