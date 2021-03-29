package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
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

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dmax.dialog.SpotsDialog;

public class Address extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView noaddresstext, addnew, continuetopay;
    ImageView noaddress;
    ConstraintLayout progresslayout;
    int posi;
    String type = "";
    String name, phone, address;
    FirebaseRecyclerAdapter<addressdetail, AddressViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        recyclerView = findViewById(R.id.addressrecyview);
        noaddresstext = findViewById(R.id.noaddressestext);
        addnew = findViewById(R.id.addnew);
        continuetopay = findViewById(R.id.continuetopay);
        noaddress = findViewById(R.id.noaddresses);
        progresslayout = findViewById(R.id.progresslayout);

        progresslayout.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addnew.setEnabled(false);

                final EditText name, address, city, phone, pincode;
                final TextView state, save, home, office;
                ImageView cross;

                final Dialog dialog = new Dialog(Address.this);
                dialog.setContentView(R.layout.addaddress);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.AppTheme_UpDown;
                name = dialog.findViewById(R.id.name);
                address = dialog.findViewById(R.id.address);
                city = dialog.findViewById(R.id.city);
                state = dialog.findViewById(R.id.state);
                save = dialog.findViewById(R.id.save);
                phone = dialog.findViewById(R.id.phone);
                pincode = dialog.findViewById(R.id.pincode);
                cross = dialog.findViewById(R.id.cross);
                home = dialog.findViewById(R.id.home);
                office = dialog.findViewById(R.id.office);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        addnew.setEnabled(true);
                    }
                });

                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("Home")) {
                            home.setBackgroundResource(R.drawable.stroke2);
                            home.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                        }
                        type = "Home";
                        home.setTextColor(Color.WHITE);
                        home.setBackgroundResource(R.drawable.stroke3);

                        office.setBackgroundResource(R.drawable.stroke2);
                        office.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                    }
                });

                office.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (type.equals("Office")) {
                            office.setBackgroundResource(R.drawable.stroke2);
                            office.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                        }
                        type = "Office";
                        office.setTextColor(Color.WHITE);
                        office.setBackgroundResource(R.drawable.stroke3);

                        home.setBackgroundResource(R.drawable.stroke2);
                        home.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                    }
                });

                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                state.setKeyListener(null);

                final String[] listItems = {"Andaman and Nicobar Islands", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadeep", "Madya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Pondicherry", "Rajasthan", "Sikkim", "Tamil Nadu", "Telagana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal"};

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Address.this);
                builder.setTitle("Your State");

                builder.setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state.setText(listItems[which]);
                    }
                });

                final androidx.appcompat.app.AlertDialog dialog1 = builder.create();

                state.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        dialog1.show();

                        return false;
                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (name.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Enter Name", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);

                        } else if (phone.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Enter Phone", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (phone.getText().toString().length() < 10) {
                            Sneaker.with(Address.this)
                                    .setTitle("Invalid Phone", R.color.white)
                                    .setMessage("Enter a valid phone number", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (address.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Enter House No,Street,Road,Area,Colony", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (city.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Enter City", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (state.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Select State", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (pincode.getText().toString().equals("")) {
                            Sneaker.with(Address.this)
                                    .setTitle("Enter Pincode", R.color.white)
                                    .setMessage("This field cant be empty", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else if (pincode.getText().toString().length() < 6) {
                            Sneaker.with(Address.this)
                                    .setTitle("Invalid Pincode", R.color.white)
                                    .setMessage("Enter a valid 6 digit Pincode", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.info, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);
                        } else {

                            final AlertDialog alertDialog = new SpotsDialog.Builder()
                                    .setCancelable(false)
                                    .setContext(Address.this)
                                    .setTheme(R.style.ProgressDialog)
                                    .setMessage("Saving Address")
                                    .build();

                            alertDialog.show();

                            Map map = new HashMap();
                            map.put("name", name.getText().toString());
                            map.put("phone", phone.getText().toString());
                            map.put("address", address.getText().toString());
                            map.put("city", city.getText().toString());
                            map.put("state", state.getText().toString());
                            map.put("pincode", pincode.getText().toString());
                            map.put("type", type);

                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Addresses").child(UUID.randomUUID().toString()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        alertDialog.dismiss();
                                        Sneaker.with(Address.this)
                                                .setTitle("Address Saved", R.color.white)
                                                .setMessage("Your details have been saved", R.color.white)
                                                .setDuration(2000)
                                                .setIcon(R.drawable.info, R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .sneak(R.color.green);
                                        dialog.dismiss();
                                        firebaseRecyclerAdapter.notifyDataSetChanged();

                                    } else {
                                        alertDialog.dismiss();
                                        Sneaker.with(Address.this)
                                                .setTitle("Error Saving Address", R.color.white)
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
                });


                dialog.show();

            }
        });

        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.stepsView);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Cart", 1);
        StepBean stepBean1 = new StepBean("Address", -1);
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
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24))//??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd));


        Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Addresses");

        query.getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progresslayout.setVisibility(View.GONE);
                continuetopay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name != null) {
                            Intent intent = new Intent(Address.this, OrderPaymentDetails.class);
                            intent.putExtra("name", name);
                            intent.putExtra("address", address);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }
                        else {
                            Sneaker.with(Address.this)
                                    .setTitle("Select An Address", R.color.white)
                                    .setMessage("Select existing or add a new address to continue", R.color.white)
                                    .setDuration(2000)
                                    .setIcon(R.drawable.delete2, R.color.white)
                                    .autoHide(true)
                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                    .setCornerRadius(10, 0)
                                    .sneak(R.color.teal_200);

                        }
                    }
                });
                if (!snapshot.exists()) {
                    noaddress.setVisibility(View.VISIBLE);
                    noaddresstext.setVisibility(View.VISIBLE);
                } else {
                    noaddress.setVisibility(View.GONE);
                    noaddresstext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<addressdetail> options = new FirebaseRecyclerOptions.Builder<addressdetail>()
                .setQuery(query, new SnapshotParser<addressdetail>() {
                    @NonNull
                    @Override
                    public addressdetail parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new addressdetail(snapshot.child("name").getValue(String.class), snapshot.child("phone").getValue(String.class), snapshot.child("address").getValue(String.class), snapshot.child("city").getValue(String.class), snapshot.child("state").getValue(String.class), snapshot.child("pincode").getValue(String.class), snapshot.child("type").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<addressdetail, AddressViewHolder>(options) {

            @Override
            public void onViewAttachedToWindow(@NonNull AddressViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder holder, int position, @NonNull addressdetail model) {

                if (posi == position) {
                    holder.check.setChecked(true, true);

                    name = firebaseRecyclerAdapter.getItem(position).getName();
                    phone = firebaseRecyclerAdapter.getItem(position).getPhone();
                    address = firebaseRecyclerAdapter.getItem(position).getAddress() + ", " + firebaseRecyclerAdapter.getItem(position).getCity() + ", " + firebaseRecyclerAdapter.getItem(position).getState() + ", " + firebaseRecyclerAdapter.getItem(position).getPincode();

                    Log.i("name", name);
                    Log.i("phone", phone);
                    Log.i("add", address);

                } else {
                    holder.check.setChecked(false, false);
                }

                if (!model.getType().equals("")) {
                    holder.type.setText(model.getType());
                    holder.type.setVisibility(View.VISIBLE);
                } else {
                    holder.type.setVisibility(View.GONE);
                }
                holder.name.setText(model.getName());
                holder.phone.setText(model.getPhone());
                holder.address.setText(model.getAddress() + " " + model.getCity() + " " + model.getState() + " " + model.getPincode());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        posi = position;
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                    }
                });

                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        posi = position;
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDialog mDialog = new MaterialDialog.Builder(Address.this)
                                .setTitle("Delete This Address?")
                                .setMessage("Remove this address permanently")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.delete2, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {

                                        firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialogInterface.dismiss();
                                                    Sneaker.with(Address.this)
                                                            .setTitle("Address Deleted ", R.color.white)
                                                            .setMessage("Address Removed From The List", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green);
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                } else {
                                                    Sneaker.with(Address.this)
                                                            .setTitle("Error Delting Address ", R.color.white)
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
                                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .build();


                        mDialog.show();

                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        {

                            final EditText name, address, city, phone, pincode;
                            final TextView state, save, home, office;
                            ImageView cross;

                            final Dialog dialog = new Dialog(Address.this);
                            dialog.setContentView(R.layout.addaddress);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.AppTheme_UpDown;
                            name = dialog.findViewById(R.id.name);
                            address = dialog.findViewById(R.id.address);
                            city = dialog.findViewById(R.id.city);
                            state = dialog.findViewById(R.id.state);
                            save = dialog.findViewById(R.id.save);
                            phone = dialog.findViewById(R.id.phone);
                            pincode = dialog.findViewById(R.id.pincode);
                            cross = dialog.findViewById(R.id.cross);
                            home = dialog.findViewById(R.id.home);
                            office = dialog.findViewById(R.id.office);

                            name.setText(firebaseRecyclerAdapter.getItem(position).getName());
                            address.setText(firebaseRecyclerAdapter.getItem(position).getAddress());
                            if (firebaseRecyclerAdapter.getItem(position).getType() != null) {
                                if (firebaseRecyclerAdapter.getItem(position).getType().equals("Home")) {
                                    type = "Home";
                                    home.setTextColor(Color.WHITE);
                                    home.setBackgroundResource(R.drawable.stroke3);

                                } else if (firebaseRecyclerAdapter.getItem(position).getType().equals("Office")) {
                                    type = "Office";
                                    office.setTextColor(Color.WHITE);
                                    office.setBackgroundResource(R.drawable.stroke3);

                                }
                            }
                            city.setText(firebaseRecyclerAdapter.getItem(position).getCity());
                            state.setText(firebaseRecyclerAdapter.getItem(position).getState());
                            phone.setText(firebaseRecyclerAdapter.getItem(position).getPhone());
                            pincode.setText(firebaseRecyclerAdapter.getItem(position).getPincode());

                            home.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    type = "Home";
                                    home.setTextColor(Color.WHITE);
                                    home.setBackgroundResource(R.drawable.stroke3);

                                    office.setBackgroundResource(R.drawable.stroke2);
                                    office.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                                }
                            });

                            office.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    type = "Office";
                                    office.setTextColor(Color.WHITE);
                                    office.setBackgroundResource(R.drawable.stroke3);

                                    home.setBackgroundResource(R.drawable.stroke2);
                                    home.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

                                }
                            });

                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            state.setKeyListener(null);

                            final String[] listItems = {"Andaman and Nicobar Islands", "Andra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadar and Nagar Haveli", "Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadeep", "Madya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Orissa", "Punjab", "Pondicherry", "Rajasthan", "Sikkim", "Tamil Nadu", "Telagana", "Tripura", "Uttarakhand", "Uttar Pradesh", "West Bengal"};

                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Address.this);
                            builder.setTitle("Your State");

                            builder.setItems(listItems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    state.setText(listItems[which]);
                                }
                            });

                            final androidx.appcompat.app.AlertDialog dialog1 = builder.create();

                            state.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {

                                    dialog1.show();

                                    return false;
                                }
                            });


                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (name.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Enter name", Toast.LENGTH_SHORT).show();
                                    } else if (phone.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Enter phone", Toast.LENGTH_SHORT).show();
                                    } else if (phone.getText().toString().length() < 10) {
                                        Toast.makeText(Address.this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                                    } else if (address.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Enter house number", Toast.LENGTH_SHORT).show();
                                    } else if (city.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Select city", Toast.LENGTH_SHORT).show();
                                    } else if (state.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Enter state", Toast.LENGTH_SHORT).show();
                                    } else if (pincode.getText().toString().equals("")) {
                                        Toast.makeText(Address.this, "Enter pincode", Toast.LENGTH_SHORT).show();
                                    } else if (pincode.getText().toString().length() < 6) {
                                        Toast.makeText(Address.this, "Invalid pincode", Toast.LENGTH_SHORT).show();
                                    } else {

                                        final AlertDialog alertDialog = new SpotsDialog.Builder()
                                                .setCancelable(false)
                                                .setContext(Address.this)
                                                .setTheme(R.style.ProgressDialog)
                                                .setMessage("Saving Address")
                                                .build();

                                        alertDialog.show();

                                        Map map = new HashMap();
                                        map.put("name", name.getText().toString());
                                        map.put("phone", phone.getText().toString());
                                        map.put("address", address.getText().toString());
                                        map.put("city", city.getText().toString());
                                        map.put("state", state.getText().toString());
                                        map.put("pincode", pincode.getText().toString());
                                        map.put("type", type);

                                        firebaseRecyclerAdapter.getRef(position).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    alertDialog.dismiss();
                                                    Sneaker.with(Address.this)
                                                            .setTitle("Changes Done", R.color.white)
                                                            .setMessage("Your changes made are saved", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green);
                                                    dialog.dismiss();
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();

                                                } else {
                                                    alertDialog.dismiss();
                                                    Sneaker.with(Address.this)
                                                            .setTitle("Error Saving Changes", R.color.white)
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
                            });


                            dialog.show();

                        }
                    }
                });


            }

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addressitem, parent, false);
                return new AddressViewHolder(view);
            }
        };


        recyclerView.setLayoutManager(new LinearLayoutManager(Address.this));
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

    private class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone, delete, edit, type;
        CustomCheckBox check;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            check = itemView.findViewById(R.id.check);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            type = itemView.findViewById(R.id.type);
        }
    }
}