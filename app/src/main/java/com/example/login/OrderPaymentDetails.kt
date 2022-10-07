package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.marcoscg.dialogsheet.DialogSheet;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static maes.tech.intentanim.CustomIntent.customType;

public class OrderPaymentDetails extends AppCompatActivity implements PaymentResultWithDataListener {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder> firebaseRecyclerAdapter;
    String name, phone, addresstext;
    ConstraintLayout progresslayout;
    TextView address, total, total2, subtotal, pay, orderidtext;
    int t = 0, no = 0;
    String orderid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        checkOrder();


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.itemsrecyview);
        name = getIntent().getStringExtra("name");
        addresstext = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        address = findViewById(R.id.address);
        total = findViewById(R.id.total);
        total2 = findViewById(R.id.total2);
        subtotal = findViewById(R.id.subtotal);
        pay = findViewById(R.id.continuetopay);
        orderidtext = findViewById(R.id.orderid);


        address.setText(name + "\n" + phone + "\n" + addresstext);
        progresslayout = findViewById(R.id.progresslayout);


        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                t = 0;
                no = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference().child("Items").child(dataSnapshot.child("id").getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            t = t + ((dataSnapshot.child("quantity").getValue(Integer.class))) * Integer.valueOf(snapshot2.child("price").getValue(String.class));
                            Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                            total.setText(format.format(new BigDecimal(String.valueOf(t))));
                            total2.setText(format.format(new BigDecimal(String.valueOf(t))));
                            subtotal.setText(format.format(new BigDecimal(String.valueOf(t))));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    no++;

                    if (no == snapshot.getChildrenCount()) {
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        total.setText(format.format(new BigDecimal(String.valueOf(t))));
                        total2.setText(format.format(new BigDecimal(String.valueOf(t))));
                        subtotal.setText(format.format(new BigDecimal(String.valueOf(t))));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.stepsView);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Cart", 1);
        StepBean stepBean1 = new StepBean("Address", 1);
        StepBean stepBean2 = new StepBean("Payment", -1);
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
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))//??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd))//??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24))//??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd));

        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");

        FirebaseRecyclerOptions<cartitemdetails> options = new FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, new SnapshotParser<cartitemdetails>() {
                    @NonNull
                    @Override
                    public cartitemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new cartitemdetails(snapshot.child("size").getValue(String.class), snapshot.child("sizename").getValue(String.class), snapshot.child("colorcode").getValue(String.class), snapshot.child("colorname").getValue(String.class), snapshot.child("id").getValue(String.class), snapshot.child("image").getValue(String.class), snapshot.child("quantity").getValue(Integer.class));
                    }
                }).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>(options) {

            @Override
            public void onViewAttachedToWindow(@NonNull ItemViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                progresslayout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                recyclerView.scheduleLayoutAnimation();
            }

            @Override

            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull cartitemdetails model) {

                FirebaseDatabase.getInstance().getReference().child("Items").child(model.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));

                        holder.product.setText(itemdetails.getProduct());

                        holder.quantity.setText("Quantity : " + model.getQuantity());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.size.setText("Color: " + model.getColorname() + ", " + "Size : " + model.getSize());

                Glide.with(getApplicationContext()).load(model.getImage()).into(holder.imageView);
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitem, parent, false);
                return new ItemViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(OrderPaymentDetails.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progresslayout.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //FirebaseDatabase.getInstance().getReference().child()

                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final Map order = new HashMap();
                        order.put("name", name);
                        order.put("address", addresstext);
                        order.put("phone", phone);
                        order.put("email", snapshot.child("email").getValue(String.class));
                        order.put("price", t);
                        // order.put("discount", String.valueOf(dis));
                        // order.put("delivery", String.valueOf(del));
                        // order.put("total", total);
                        order.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        order.put("timestamp", ServerValue.TIMESTAMP);
                        order.put("status", "Payment Pending");
                        order.put("orderid", orderid);
                        order.put("type", "new");

                        DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                        final DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid);
                        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                toPath.child("items").setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                toPath.updateChildren(order).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {
                                                            final Checkout co = new Checkout();
                                                            int image = R.mipmap.ic_launcher; // Can be any drawable
                                                            co.setKeyID("rzp_test_dt2sl2ttn6g114");
                                                            try {
                                                                JSONObject options = new JSONObject();
                                                                options.put("name", "The Series Store");
                                                                options.put("description", "Order");
                                                                options.put("currency", "INR");
                                                                options.put("amount", t * 100);
                                                                options.put("send_sms_hash", true);

                                                                JSONObject preFill = new JSONObject();
                                                                preFill.put("email", snapshot.child("email").getValue(String.class));
                                                                preFill.put("contact", phone);

                                                                options.put("prefill", preFill);

                                                                co.open(OrderPaymentDetails.this, options);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            progresslayout.setVisibility(View.GONE);
                                                            Sneaker.with(OrderPaymentDetails.this)
                                                                    .setTitle("Some Error Occurred", R.color.white)
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

                                        }
                                );
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

            }
        });
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
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        Map map = new HashMap();
        map.put("text", "Order Placed");
        map.put("timestamp", ServerValue.TIMESTAMP);

        Log.i("payemnt", String.valueOf(paymentData));
        Map order = new HashMap();
        order.put("status", "Payment Success");
        order.put("razorpay_payment_id", paymentData.getPaymentId());
        order.put("razorpay_order_id", paymentData.getOrderId());
        order.put("razorpay_signature", paymentData.getSignature());
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map);

        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progresslayout.setVisibility(View.GONE);
                    Toast.makeText(OrderPaymentDetails.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderPaymentDetails.this, OrderPaymentStatus.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("status", "success");
                    startActivity(intent);
                    customType(OrderPaymentDetails.this, "left-to-right");
                }
            }
        });


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Map order = new HashMap();
        order.put("status", "Payment Failed");
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progresslayout.setVisibility(View.GONE);
        if (i == Checkout.PAYMENT_CANCELED) {
            new DialogSheet(OrderPaymentDetails.this, false)
                    .setTitle("Payment Failed")
                    .setMessage("Payment Was Cancelled By User")
                    .setIconResource(R.drawable.info)
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18)
                    .setMessageTextSize(14)// In SP
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show();
        } else if (i == Checkout.NETWORK_ERROR) {
            new DialogSheet(OrderPaymentDetails.this, true)
                    .setTitle("Payment Failed")
                    .setMessage("Some Network Error Occurred")
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18) // In SP
                    .setMessageTextSize(14)// In SP
                    .setIconResource(R.drawable.info)
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show();

        } else if (i == Checkout.INVALID_OPTIONS) {
            new DialogSheet(OrderPaymentDetails.this, true)
                    .setTitle("Error Loading Payment Page")
                    .setMessageTextSize(14)// In SP
                    .setMessage("Please Try Again")
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18) // In SP
                    .setIconResource(R.drawable.info)
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show();

        } else {
            new DialogSheet(OrderPaymentDetails.this, true)
                    .setTitle("Some Error Occurred")
                    .setMessage("Please Try Again")
                    .setColoredNavigationBar(true)
                    .setMessageTextSize(14)// In SP
                    .setTitleTextSize(18) // In SP
                    .setCancelable(false)
                    .setIconResource(R.drawable.info)
                    .setPositiveButton("Okay", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show();

        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, size, price, quantity, product;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.specs);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.image);
            quantity = itemView.findViewById(R.id.quantity);
            product = itemView.findViewById(R.id.product);
        }
    }

    // function to generate a random string of length n
    String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNPQRSTUVXYZ"
                + "123456789";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private String checkOrder() {
        orderid = getAlphaNumericString(15);
        FirebaseDatabase.getInstance().getReference().child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(orderid).exists()) {
                    checkOrder();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return orderid;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(OrderPaymentDetails.this, "right-to-left");
    }
}
