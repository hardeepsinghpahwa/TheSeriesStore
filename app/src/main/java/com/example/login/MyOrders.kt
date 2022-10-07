package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.stfalcon.multiimageview.MultiImageView;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import pereira.agnaldo.previewimgcol.ImageCollectionView;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyOrders extends AppCompatActivity {

    FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder> firebaseRecyclerAdapter;
    RecyclerView recyclerView;
    ArrayList<Bitmap> imgs;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        imgs = new ArrayList<>();
        recyclerView = findViewById(R.id.ordersrecyview);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Query query = FirebaseDatabase.getInstance().getReference().child("Orders").orderByChild("userid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseRecyclerOptions<cartitemdetails> options = new FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, new SnapshotParser<cartitemdetails>() {
                    @NonNull
                    @Override
                    public cartitemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new cartitemdetails(snapshot.child("size").getValue(String.class), snapshot.child("sizename").getValue(String.class), snapshot.child("colorcode").getValue(String.class), snapshot.child("colorname").getValue(String.class), snapshot.getKey(), snapshot.child("image").getValue(String.class), snapshot.child("quantity").getValue(Integer.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {

            @Override
            public void onViewAttachedToWindow(@NonNull CartViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                // itemsnumber.setText(firebaseRecyclerAdapter.getItemCount()+" items");
                //progresslayout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cartitemdetails model) {
                imgs = new ArrayList<>();

                if(position%2!=0)
                {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightgrey));
                }
                firebaseRecyclerAdapter.getRef(position).child("tracking").getRef().orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            holder.status.setText(dataSnapshot.child("text").getValue(String.class) + ", " + getTime(dataSnapshot.child("timestamp").getValue(Long.class)));
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                firebaseRecyclerAdapter.getRef(position).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       /* itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));
*/
                        // holder.product.setText(itemdetails.getProduct());

                        // holder.quantity.setText(""+model.getQuantity());

                        cartitemdetails cartitemdetails=snapshot.getValue(com.example.login.cartitemdetails.class);


                        if(snapshot.getChildrenCount()==1)
                        {
                            holder.itemsnum.setVisibility(View.GONE);
                        }
                        else {
                            holder.itemsnum.setText("+ "+(snapshot.getChildrenCount()-1)+" items");
                        }




                        snapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(holder.image);


                                    holder.specs.setText("Size : " + dataSnapshot.child("size").getValue(String.class) + ", " + dataSnapshot.child("colorname").getValue(String.class));


                                    FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("id").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);
                                            i = 0;
                                            holder.name.setText(itemdetails.getName());

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MyOrders.this,ViewOrder.class);
                        intent.putExtra("id",firebaseRecyclerAdapter.getItem(position).getId());
                        startActivity(intent);
                        customType(MyOrders.this,"left-to-right");
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false);
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
    public void onBackPressed() {
        super.onBackPressed();
        customType(MyOrders.this,"right-to-left");
    }

    private class CartViewHolder extends RecyclerView.ViewHolder {

        TextView name, specs, itemsnum, status;
        ImageView image;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            itemsnum = itemView.findViewById(R.id.itemsno);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.images1);
            specs = itemView.findViewById(R.id.specs);
            status = itemView.findViewById(R.id.status);
        }
    }

    private String getTime(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa");
        return sdf.format(d);
    }

}