package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import static maes.tech.intentanim.CustomIntent.customType;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder> firebaseRecyclerAdapter;
    double t = 0;
    int no=0;
    TextView total, total2, subtotal;
    ConstraintLayout cartlayout,progresslayout;
    CardView back, emptycart;
    TextView itemsnumber;
    private final String[] steps = {"Cart", "Address", "Payment", "Placed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.stepsView);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Cart", -1);
        StepBean stepBean1 = new StepBean("Address", 0);
        StepBean stepBean2 = new StepBean("Payment", 0);
        StepBean stepBean3 = new StepBean("Placed", 0);

        stepsBeanList.add(stepBean0);

        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);

        setpview5.setStepViewTexts(stepsBeanList)//???

                .setTextSize(12)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))//??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray))//??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))//??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray))//??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd))//??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ring))//??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd));

        recyclerView = findViewById(R.id.cartrecyview);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        total = findViewById(R.id.total);
        total2 = findViewById(R.id.total2);
        subtotal = findViewById(R.id.subtotal);
        back = findViewById(R.id.back);
        emptycart = findViewById(R.id.emptycart);
        cartlayout = findViewById(R.id.cartlayout);
        itemsnumber=findViewById(R.id.itemno);
        progresslayout=findViewById(R.id.progresslayout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                t=0;
                no=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("id").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            t=t+((dataSnapshot.child("quantity").getValue(Integer.class)))*Integer.valueOf(snapshot2.child("price").getValue(String.class));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            subtotal.setText(format.format(new BigDecimal(String.valueOf(t))));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    no++;

                    if(no==snapshot.getChildrenCount())
                    {
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        subtotal.setText(format.format(new BigDecimal(String.valueOf(t))));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");

        emptycart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDialog mDialog = new MaterialDialog.Builder(Cart.this)
                        .setTitle("Empty The Cart?")
                        .setMessage("Delete All Items")
                        .setCancelable(false)
                        .setPositiveButton("Empty", R.drawable.delete2, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                query.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Sneaker.with(Cart.this)
                                                    .setTitle("Cart Empty", R.color.white)
                                                    .setMessage("All Items Removed From Cart", R.color.white)
                                                    .setDuration(2000)
                                                    .setIcon(R.drawable.delete2, R.color.white)
                                                    .autoHide(true)
                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                    .setCornerRadius(10, 0)
                                                    .sneak(R.color.green);
                                            dialogInterface.dismiss();

                                        } else {
                                            Sneaker.with(Cart.this)
                                                    .setTitle("Error Emptying Cart ", R.color.white)
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
                mDialog.show();

            }
        });

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (!snapshot.exists()) {
                    cartlayout.setVisibility(View.GONE);
                    findViewById(R.id.emptycartimg).setVisibility(View.VISIBLE);
                    findViewById(R.id.emptycarttext).setVisibility(View.VISIBLE);
                    recyclerView.scheduleLayoutAnimation();
                } else {
                    cartlayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.emptycartimg).setVisibility(View.GONE);
                    findViewById(R.id.emptycarttext).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                itemsnumber.setText(firebaseRecyclerAdapter.getItemCount()+" items");
                progresslayout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cartitemdetails model) {

                FirebaseDatabase.getInstance().getReference().child("Items").child(model.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));

                        holder.product.setText(itemdetails.getProduct());

                        holder.quantity.setText(""+model.getQuantity());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.size.setText("Color: " + model.getColorname() + ", " + "Size : " + model.getSize());

                Glide.with(getApplicationContext()).load(model.getImage()).into(holder.image);

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progresslayout.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        firebaseRecyclerAdapter.getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child("quantity").getRef().setValue(snapshot.child("quantity").getValue(Integer.class)+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progresslayout.setVisibility(View.GONE);
                                            }
                                        }, 1000);
                                        if(task.isSuccessful())
                                        {
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //holder.quantity.setText((Integer.valueOf(holder.quantity.getText().toString()) + 1) + "");
                    }
                });

                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        firebaseRecyclerAdapter.getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("quantity").getValue(Integer.class)>1) {
                                    progresslayout.setVisibility(View.VISIBLE);
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    snapshot.child("quantity").getRef().setValue(snapshot.child("quantity").getValue(Integer.class) - 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            final Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progresslayout.setVisibility(View.GONE);
                                                }
                                            }, 1000);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog mDialog = new MaterialDialog.Builder(Cart.this)
                                .setTitle("Remove Item From Cart?")
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
                                                    Sneaker.with(Cart.this)
                                                            .setTitle("Item Removed From Cart", R.color.white)
                                                            .setMessage(firebaseRecyclerAdapter.getItemCount() + " item(s) left in cart", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green);
                                                } else {
                                                    Sneaker.with(Cart.this)
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem, parent, false);
                return new CartViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Cart.this, "fadein-to-fadeout");
    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        TextView name, size, price, quantity, product;
        ImageView add, minus, image;
        CardView delete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            add = itemView.findViewById(R.id.plus);
            product = itemView.findViewById(R.id.product);
            minus = itemView.findViewById(R.id.minus);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}