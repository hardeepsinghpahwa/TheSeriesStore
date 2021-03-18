package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.login.Fragments.itemdetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.irozon.sneaker.interfaces.OnSneakerClickListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class ItemDetail extends AppCompatActivity {

    ImageView image;
    CardView back, cart;
    TextView name, price, product, detail1, detail2, detail3, detail4, addtowishlist, addtocart;
    RecyclerView colors, sizes;
    String id, colorcode, colorname, size, sizename, currentimage;
    int no = 0;
    FirebaseRecyclerAdapter<colordetails, ColorViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<colordetails, SizeViewHolder> firebaseRecyclerAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        colors = findViewById(R.id.colorsrecyview);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        sizes = findViewById(R.id.sizerecyview);
        product = findViewById(R.id.product);
        detail1 = findViewById(R.id.detail1);
        detail2 = findViewById(R.id.detail2);
        detail3 = findViewById(R.id.detail3);
        detail4 = findViewById(R.id.detail4);
        addtocart = findViewById(R.id.addtocart);
        addtowishlist = findViewById(R.id.wishlistitem);
        cart = findViewById(R.id.cart);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemDetail.this, Cart.class));
                customType(ItemDetail.this, "fadein-to-fadeout");
            }
        });

        id = "01";

        FirebaseDatabase.getInstance().getReference().child("Items").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(Objects.requireNonNull(snapshot).child("name").getValue(String.class));
                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                price.setText(format.format(new BigDecimal(Objects.requireNonNull(snapshot).child("price").getValue(String.class))));
                Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot).child("image").getValue(String.class)).into(image);
                YoYo.with(Techniques.FadeInDown)
                        .duration(500)
                        .playOn(image);

                currentimage = snapshot.child("image").getValue(String.class);

                product.setText(Objects.requireNonNull(snapshot).child("product").getValue(String.class));

                if (snapshot.child("detail1").getValue(String.class) != null) {
                    detail1.setText(snapshot.child("detail1").getValue(String.class));
                } else {
                    detail1.setVisibility(View.GONE);
                }
                if (snapshot.child("detail2").getValue(String.class) != null) {
                    detail2.setText(snapshot.child("detail2").getValue(String.class));
                } else {
                    detail2.setVisibility(View.GONE);
                }
                if (snapshot.child("detail3").getValue(String.class) != null) {
                    detail3.setText(snapshot.child("detail3").getValue(String.class));
                } else {
                    detail3.setVisibility(View.GONE);
                }
                if (snapshot.child("detail4").getValue(String.class) != null) {
                    detail4.setText(snapshot.child("detail4").getValue(String.class));
                } else {
                    detail4.setVisibility(View.GONE);
                }

                addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map map = new HashMap();
                        map.put("id", id);
                        map.put("colorcode", colorcode);
                        map.put("colorname", colorname);
                        map.put("size", size);
                        map.put("sizename", sizename);
                        map.put("image", currentimage);

                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ItemDetail.this, "Added", Toast.LENGTH_SHORT).show();
                                    Sneaker.with(ItemDetail.this)
                                            .setTitle("Item Added To Cart", R.color.white)
                                            .setMessage("Check Your Cart", R.color.white)
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .setOnSneakerClickListener(new OnSneakerClickListener() {
                                                @Override
                                                public void onSneakerClick(View view) {
                                                    startActivity(new Intent(ItemDetail.this, Cart.class));
                                                    customType(ItemDetail.this, "fadein-to-fadeout");
                                                }
                                            })
                                            .sneak(R.color.green);

                                } else {
                                    Sneaker.with(ItemDetail.this)
                                            .setTitle("Error Adding Item To Cart", R.color.white)
                                            .setMessage("Please Try Again")
                                            .setDuration(2000)
                                            .setIcon(R.drawable.info, R.color.white)
                                            .autoHide(true)
                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                            .setCornerRadius(10, 0)
                                            .sneak(R.color.teal_200);
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Items").child(id).child("colors");

        FirebaseRecyclerOptions<colordetails> options = new FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query, new SnapshotParser<colordetails>() {
                    @NonNull
                    @Override
                    public colordetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new colordetails(snapshot.child("colorcode").getValue(String.class), snapshot.child("name").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<colordetails, ColorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ColorViewHolder holder, int position, @NonNull colordetails model) {
                Log.i("color", model.getColorcode());

                holder.color.setBackgroundColor(Color.parseColor(model.getColorcode()));

                if (position == 0) {
                    colorcode = model.getColorcode();
                    colorname = model.getName();
                    holder.colorlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < firebaseRecyclerAdapter.getItemCount(); i++) {
                            if (position == i) {
                                View view = colors.getLayoutManager().findViewByPosition(i);

                                if (view != null) {
                                    RelativeLayout relativeLayout = view.findViewById(R.id.colorlayout);
                                    if (relativeLayout != null)
                                        relativeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke));
                                }

                                FirebaseDatabase.getInstance().getReference().child("Items").child(id).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        no = 0;
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (no == position) {
                                                currentimage = dataSnapshot.child("image").getValue(String.class);
                                                Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(image);
                                                YoYo.with(Techniques.FadeInDown)
                                                        .duration(500)
                                                        .playOn(image);
                                                break;
                                            }
                                            no++;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {
                                View view = colors.getLayoutManager().findViewByPosition(i);

                                if (view != null) {
                                    RelativeLayout relativeLayout = view.findViewById(R.id.colorlayout);
                                    if (relativeLayout != null)
                                        relativeLayout.setBackground(null);
                                }
                            }
                        }

                    }
                });

            }

            @NonNull
            @Override
            public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coloritem, parent, false);
                return new ColorViewHolder(view);
            }
        };


        colors.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        colors.setAdapter(firebaseRecyclerAdapter);


        Query query2 = FirebaseDatabase.getInstance().getReference().child("Items").child(id).child("sizes");

        FirebaseRecyclerOptions<colordetails> options2 = new FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query2, new SnapshotParser<colordetails>() {
                    @NonNull
                    @Override
                    public colordetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new colordetails(snapshot.child("size").getValue(String.class), snapshot.child("name").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<colordetails, SizeViewHolder>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull SizeViewHolder holder, int position, @NonNull colordetails model) {
                holder.size.setText(model.getColorcode());

                if (position == 0) {
                    size = model.getColorcode();
                    sizename = model.getName();
                    holder.colorlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < firebaseRecyclerAdapter2.getItemCount(); i++) {
                            if (position == i) {
                                View view = sizes.getLayoutManager().findViewByPosition(i);

                                if (view != null) {
                                    RelativeLayout relativeLayout = view.findViewById(R.id.colorlayout);
                                    if (relativeLayout != null)
                                        relativeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke));
                                }

                            } else {
                                View view = sizes.getLayoutManager().findViewByPosition(i);

                                if (view != null) {
                                    RelativeLayout relativeLayout = view.findViewById(R.id.colorlayout);
                                    if (relativeLayout != null)
                                        relativeLayout.setBackground(null);
                                }
                            }
                        }

                    }
                });
            }

            @NonNull
            @Override
            public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sizeitem, parent, false);
                return new SizeViewHolder(view);
            }
        };


        sizes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        sizes.setAdapter(firebaseRecyclerAdapter2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter2.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
        firebaseRecyclerAdapter2.startListening();
    }

    private class ColorViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout colorlayout, color;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);

            color = itemView.findViewById(R.id.color);
            colorlayout = itemView.findViewById(R.id.colorlayout);
        }
    }

    private class SizeViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout colorlayout;
        TextView size;


        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);

            size = itemView.findViewById(R.id.size);
            colorlayout = itemView.findViewById(R.id.colorlayout);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ItemDetail.this, "right-to-left");
    }
}