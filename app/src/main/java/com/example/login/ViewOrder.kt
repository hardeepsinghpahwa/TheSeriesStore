package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.bumptech.glide.Glide;
import com.example.login.Fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opensooq.pluto.PlutoIndicator;
import com.opensooq.pluto.PlutoView;
import com.opensooq.pluto.base.PlutoAdapter;
import com.opensooq.pluto.base.PlutoViewHolder;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class ViewOrder extends AppCompatActivity {

    String id;
    ArrayList<cartitemdetails> products;
    TextView address, total, total2, subtotal, orderidtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        id = getIntent().getStringExtra("id");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        address = findViewById(R.id.address);
        total = findViewById(R.id.total);
        total2 = findViewById(R.id.total2);
        subtotal = findViewById(R.id.subtotal);
        orderidtext = findViewById(R.id.orderid);
        products = new ArrayList<>();
        PlutoView pluto = findViewById(R.id.slider_view);

        FirebaseDatabase.getInstance().getReference().child("Orders").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                total.setText(format.format(new BigDecimal(String.valueOf(Objects.requireNonNull(snapshot).child("price").getValue(Long.class)))));
//                subtotal.setText(format.format(new BigDecimal("")));
                address.setText(snapshot.child("name").getValue(String.class)+"\n"+snapshot.child("phone").getValue(String.class)+"\n"+snapshot.child("address").getValue(String.class));

                snapshot.child("items").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            cartitemdetails ite = dataSnapshot.getValue(com.example.login.cartitemdetails.class);
                            products.add((ite));
                        }

                        YourAdapter adapter = new YourAdapter(products);
                        pluto.create(adapter,2000, getLifecycle());
                        pluto.setCustomIndicator(findViewById(R.id.indicator));
                        pluto.setDuration(5000);
                        pluto.stopAutoCycle();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                snapshot.child("tracking").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        VerticalStepView setpview5 =  findViewById(R.id.step_view);
                        List<String> stepsBeanList = new ArrayList<>();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            stepsBeanList.add(dataSnapshot.child("text").getValue(String.class) + ", "+getDate(dataSnapshot.child("timestamp").getValue(Long.class)));
                        }

                        setpview5.setStepsViewIndicatorComplectingPosition((int) (snapshot.getChildrenCount()-1))//设置完成的步数
                                .setTextSize(12)
                                .reverseDraw(false)//default is true
                                .setStepViewTexts(stepsBeanList)//总步骤
                                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))//设置StepsViewIndicator完成线的颜色
                                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.grey))//设置StepsViewIndicator未完成线的颜色
                                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey))//设置StepsView text完成线的颜色
                                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey))//设置StepsView text未完成线的颜色
                                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24))//??StepsViewIndicator DefaultIcon
                                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd))//设置StepsViewIndicator CompleteIcon
                                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_black_24));//设置StepsViewIndicator AttentionIcon
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

    public class YourAdapter extends PlutoAdapter<cartitemdetails, YourAdapter.OrderViewHolder> {

        public YourAdapter(ArrayList<cartitemdetails> items) {
            super(items);
        }

        @Override
        public OrderViewHolder getViewHolder(ViewGroup parent, int viewType) {
            return new OrderViewHolder(parent, R.layout.orderitem);
        }

        public class OrderViewHolder extends PlutoViewHolder<cartitemdetails> {
            ImageView image;
            TextView name, product, specs, price, quantity;

            public OrderViewHolder(ViewGroup parent, int itemLayoutId) {
                super(parent, itemLayoutId);
                image = getView(R.id.image);
                name = getView(R.id.name);
                price = getView(R.id.price);
                specs = getView(R.id.specs);
                product = getView(R.id.product);
                quantity = getView(R.id.quantity);
            }

            @Override
            public void set(cartitemdetails item, int pos) {
                //Glide.with(getApplicationContext()).load(item.getImage()).into(image);
                specs.setText("Size : " + item.getSize() + ", Color : " + item.getColorname());
                quantity.setText("Quantity : " + item.getQuantity());
                Glide.with(getApplicationContext()).load(item.getImage()).into(image);

                FirebaseDatabase.getInstance().getReference().child("Items").child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        name.setText(snapshot.child("name").getValue(String.class));
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        price.setText(format.format(new BigDecimal(snapshot.child("price").getValue(String.class))));
                        product.setText(snapshot.child("product").getValue(String.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(ViewOrder.this,"right-to-left");
    }

    private String getDate(long time) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
        return sdf.format(d);
    }
}