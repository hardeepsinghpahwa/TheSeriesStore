package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class OrderPaymentDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder> firebaseRecyclerAdapter;
    String name, phone, addresstext;
    ConstraintLayout progresslayout;
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        recyclerView=findViewById(R.id.itemsrecyview);
        name=getIntent().getStringExtra("name");
        addresstext=getIntent().getStringExtra("address");
        phone=getIntent().getStringExtra("phone");
        address=findViewById(R.id.address);

        address.setText(name+"\n"+phone+"\n"+addresstext);
        progresslayout=findViewById(R.id.progresslayout);

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
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray))//??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd))//??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24))//??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd));


        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");

        FirebaseRecyclerOptions<cartitemdetails> options = new FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, new SnapshotParser<cartitemdetails>() {
                    @NonNull
                    @Override
                    public cartitemdetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new cartitemdetails(snapshot.child("size").getValue(String.class), snapshot.child("sizename").getValue(String.class), snapshot.child("colorcode").getValue(String.class), snapshot.child("colorname").getValue(String.class), snapshot.child("id").getValue(String.class), snapshot.child("image").getValue(String.class),snapshot.child("quantity").getValue(Integer.class));
                    }
                }).build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>(options) {

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

                        holder.quantity.setText("Quantity : "+model.getQuantity());


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

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView name, size, price, quantity, product;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.image);
            quantity = itemView.findViewById(R.id.quantity);
            product = itemView.findViewById(R.id.product);
        }
    }

    // function to generate a random string of length n
    String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

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

}
