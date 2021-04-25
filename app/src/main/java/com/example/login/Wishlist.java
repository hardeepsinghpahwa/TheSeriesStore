package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.login.Fragments.itemdetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import static maes.tech.intentanim.CustomIntent.customType;

public class Wishlist extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder> firebaseRecyclerAdapter;
    ConstraintLayout progresslayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerView=findViewById(R.id.wishlistrecyview);
        progresslayout=findViewById(R.id.progresslayout);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Wishlist.this,Cart.class));
                customType(Wishlist.this,"left-to-right");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progresslayout.setVisibility(View.GONE);
                    }
                }, 700);
                if (!snapshot.exists()) {
                    findViewById(R.id.emptywishlistimg).setVisibility(View.VISIBLE);
                    findViewById(R.id.emptyishlisttext).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.emptywishlistimg).setVisibility(View.VISIBLE);
                    findViewById(R.id.emptyishlisttext).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist");

        FirebaseRecyclerOptions<cartitemdetails> options = new FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, new SnapshotParser<cartitemdetails>() {
                    @NonNull
                    @Override
                    public cartitemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new cartitemdetails(snapshot.child("size").getValue(String.class), snapshot.child("sizename").getValue(String.class), snapshot.child("colorcode").getValue(String.class), snapshot.child("colorname").getValue(String.class), snapshot.child("id").getValue(String.class), snapshot.child("image").getValue(String.class),snapshot.child("quantity").getValue(Integer.class));
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {

            @Override
            public void onViewAttachedToWindow(@NonNull CartViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                progresslayout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cartitemdetails model) {

                holder.move.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progresslayout.setVisibility(View.VISIBLE);
                        DatabaseReference fromPath = firebaseRecyclerAdapter.getRef(position);
                        DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");


                        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                toPath.child(UUID.randomUUID().toString()).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                progresslayout.setVisibility(View.GONE);
                                                fromPath.removeValue();
                                            }
                                        }
                                );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Items").child(model.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));

                        holder.product.setText(itemdetails.getProduct());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.size.setText("Color: " + model.getColorname() + ", " + "Size : " + model.getSize());

                Glide.with(getApplicationContext()).load(model.getImage()).into(holder.image);

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog mDialog = new MaterialDialog.Builder(Wishlist.this)
                                .setTitle("Remove Item From Wishlist?")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.delete2, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {

                                        firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialogInterface.dismiss();
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                    Sneaker.with(Wishlist.this)
                                                            .setTitle("Item Removed From Wishlist", R.color.white)
                                                            .setMessage(firebaseRecyclerAdapter.getItemCount() + " item(s) left in cart", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green);
                                                } else {
                                                    Sneaker.with(Wishlist.this)
                                                            .setTitle("Error Removing Item ", R.color.white)
                                                            .setMessage("Please Try Again", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.teal_200);

                                                }
                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("Cancel", R.drawable.cancel, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .build();

                        // Show Dialog
                        mDialog.show();
                    }
                });



            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlistitem, parent, false);
                return new CartViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(Wishlist.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRecyclerAdapter.stopListening();
    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        TextView name, size, price, quantity, product,move;
        ImageView add, minus, image;
        CardView delete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            size = itemView.findViewById(R.id.specs);
            move = itemView.findViewById(R.id.movetowishlist);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            add = itemView.findViewById(R.id.plus);
            product = itemView.findViewById(R.id.product);
            minus = itemView.findViewById(R.id.minus);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Wishlist.this,"right-to-left");
    }
}