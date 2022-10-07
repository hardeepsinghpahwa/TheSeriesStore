package com.example.login

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.irozon.sneaker.Sneaker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.baoyachi.stepview.HorizontalStepView
import com.baoyachi.stepview.bean.StepBean
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.Fragments.itemdetails
import com.bumptech.glide.Glide
import android.graphics.Color
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Checkout
import org.json.JSONObject
import org.json.JSONException
import com.razorpay.PaymentData
import com.marcoscg.dialogsheet.DialogSheet
import android.util.Log
import android.view.*
import android.widget.*
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import java.lang.StringBuilder
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class OrderPaymentDetails : AppCompatActivity(), PaymentResultWithDataListener {
    lateinit var recyclerView: RecyclerView
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>
    lateinit var name: String
    lateinit var phone: String
    lateinit var addresstext: String
    lateinit var progresslayout: ConstraintLayout
    lateinit var address: TextView
    lateinit var total: TextView
    lateinit var total2: TextView
    lateinit var subtotal: TextView
    lateinit var pay: TextView
    lateinit var orderidtext: TextView
    var t: Int = 0
    var no: Int = 0
    var orderid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_payment_details)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        checkOrder()
        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        recyclerView = findViewById(R.id.itemsrecyview)
        name = getIntent().getStringExtra("name")!!
        addresstext = getIntent().getStringExtra("address")!!
        phone = getIntent().getStringExtra("phone")!!
        address = findViewById(R.id.address)
        total = findViewById(R.id.total)
        total2 = findViewById(R.id.total2)
        subtotal = findViewById(R.id.subtotal)
        pay = findViewById(R.id.continuetopay)
        orderidtext = findViewById(R.id.orderid)
        address.setText(name + "\n" + phone + "\n" + addresstext)
        progresslayout = findViewById(R.id.progresslayout)
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                t = 0
                no = 0
                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference().child("Items").child((dataSnapshot.child("id").getValue(String::class.java))!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        public override fun onDataChange(snapshot2: DataSnapshot) {
                            t = t + ((dataSnapshot.child("quantity").getValue(Int::class.java)))!! * Integer.valueOf(snapshot2.child("price").getValue(String::class.java))
                            val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                            total.setText(format.format(BigDecimal(t.toString())))
                            total2.setText(format.format(BigDecimal(t.toString())))
                            subtotal.setText(format.format(BigDecimal(t.toString())))
                        }

                        public override fun onCancelled(error: DatabaseError) {}
                    })
                    no++
                    if (no.toLong() == snapshot.getChildrenCount()) {
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        total.setText(format.format(BigDecimal(t.toString())))
                        total2.setText(format.format(BigDecimal(t.toString())))
                        subtotal.setText(format.format(BigDecimal(t.toString())))
                    }
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        val setpview5: HorizontalStepView = findViewById<View>(R.id.stepsView) as HorizontalStepView
        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0: StepBean = StepBean("Cart", 1)
        val stepBean1: StepBean = StepBean("Address", 1)
        val stepBean2: StepBean = StepBean("Payment", -1)
        val stepBean3: StepBean = StepBean("Placed", 0)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        setpview5.setStepViewTexts(stepsBeanList) //???
                .setTextSize(12) //set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray)) //??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd)) //??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24)) //??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd))
        val query: Query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart")
        val options: FirebaseRecyclerOptions<cartitemdetails> = FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, object : SnapshotParser<cartitemdetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): cartitemdetails {
                        return cartitemdetails(snapshot.child("size").getValue(String::class.java), snapshot.child("sizename").getValue(String::class.java), snapshot.child("colorcode").getValue(String::class.java), snapshot.child("colorname").getValue(String::class.java), snapshot.child("id").getValue(String::class.java), snapshot.child("image").getValue(String::class.java), snapshot.child("quantity").getValue(Int::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>(options) {
            public override fun onViewAttachedToWindow(holder: ItemViewHolder) {
                super.onViewAttachedToWindow(holder)
                progresslayout.setVisibility(View.GONE)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                recyclerView.scheduleLayoutAnimation()
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: cartitemdetails) {
                FirebaseDatabase.getInstance().getReference().child("Items").child(model.id!!).addValueEventListener(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        val itemdetails: itemdetails? = snapshot.getValue(itemdetails::class.java)
                        holder.name.setText(itemdetails!!.name)
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        holder.price.setText(format.format(BigDecimal(itemdetails.price)))
                        holder.product.setText(itemdetails.product)
                        holder.quantity.setText("Quantity : " + model.quantity)
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                holder.size.setText("Color: " + model.colorname+ ", " + "Size : " + model.size)
                Glide.with(getApplicationContext()).load(model.image).into(holder.imageView)
            }

            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitem, parent, false)
                return ItemViewHolder(view)
            }
        }
        recyclerView.setLayoutManager(LinearLayoutManager(this@OrderPaymentDetails))
        recyclerView.setAdapter(firebaseRecyclerAdapter)
        pay.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                progresslayout.setVisibility(View.VISIBLE)
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //FirebaseDatabase.getInstance().getReference().child()
                FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        val order = mutableMapOf<String?, Any?>()
                        order.put("name", name)
                        order.put("address", addresstext)
                        order.put("phone", phone)
                        order.put("email", snapshot.child("email").getValue(String::class.java))
                        order.put("price", t)
                        // order.put("discount", String.valueOf(dis));
                        // order.put("delivery", String.valueOf(del));
                        // order.put("total", total);
                        order.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        order.put("timestamp", ServerValue.TIMESTAMP)
                        order.put("status", "Payment Pending")
                        order.put("orderid", orderid)
                        order.put("type", "new")
                        val fromPath: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart")
                        val toPath: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid)
                        fromPath.addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                                toPath.child("items").setValue(dataSnapshot.getValue(), object : DatabaseReference.CompletionListener {
                                    public override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                                        toPath.updateChildren(order).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(p0: Task<Void?>) {
                                                if (p0.isSuccessful()) {
                                                    val co: Checkout = Checkout()
                                                    val image: Int = R.mipmap.ic_launcher // Can be any drawable
                                                    co.setKeyID("rzp_test_dt2sl2ttn6g114")
                                                    try {
                                                        val options: JSONObject = JSONObject()
                                                        options.put("name", "The Series Store")
                                                        options.put("description", "Order")
                                                        options.put("currency", "INR")
                                                        options.put("amount", t * 100)
                                                        options.put("send_sms_hash", true)
                                                        val preFill: JSONObject = JSONObject()
                                                        preFill.put("email", snapshot.child("email").getValue(String::class.java))
                                                        preFill.put("contact", phone)
                                                        options.put("prefill", preFill)
                                                        co.open(this@OrderPaymentDetails, options)
                                                    } catch (e: JSONException) {
                                                        e.printStackTrace()
                                                    }
                                                } else {
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    progresslayout.setVisibility(View.GONE)
                                                    Sneaker.with(this@OrderPaymentDetails)
                                                            .setTitle("Some Error Occurred", R.color.white)
                                                            .setMessage("Please Try Again", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.teal_200)
                                                }
                                            }
                                        })
                                    }
                                }
                                )
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter!!.stopListening()
    }

    public override fun onPaymentSuccess(s: String, paymentData: PaymentData) {
        val map = mutableMapOf<String?, Any?>()
        map.put("text", "Order Placed")
        map.put("timestamp", ServerValue.TIMESTAMP)
        Log.i("payemnt", paymentData.toString())
        val order = mutableMapOf<String?, Any?>()
        order.put("status", "Payment Success")
        order.put("razorpay_payment_id", paymentData.getPaymentId())
        order.put("razorpay_order_id", paymentData.getOrderId())
        order.put("razorpay_signature", paymentData.getSignature())
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map)
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(object : OnCompleteListener<Void?> {
            public override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful()) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    progresslayout!!.setVisibility(View.GONE)
                    Toast.makeText(this@OrderPaymentDetails, "Success", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(this@OrderPaymentDetails, OrderPaymentStatus::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("status", "success")
                    startActivity(intent)
                    CustomIntent.customType(this@OrderPaymentDetails, "left-to-right")
                }
            }
        })
    }

    public override fun onPaymentError(i: Int, s: String, paymentData: PaymentData) {
        val order= mutableMapOf<String?, Any?>()
        order.put("status", "Payment Failed")
        FirebaseDatabase.getInstance().getReference().child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(object : OnCompleteListener<Void?> {
            public override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful()) {
                }
            }
        })
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progresslayout!!.setVisibility(View.GONE)
        if (i == Checkout.PAYMENT_CANCELED) {
            DialogSheet(this@OrderPaymentDetails, false)
                    .setTitle("Payment Failed")
                    .setMessage("Payment Was Cancelled By User")
                    .setIconResource(R.drawable.info)
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18)
                    .setMessageTextSize(14) // In SP
                    .setCancelable(false)
                    .setPositiveButton("Okay", object : DialogSheet.OnPositiveClickListener {
                        public override fun onClick(view: View?) {}
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show()
        } else if (i == Checkout.NETWORK_ERROR) {
            DialogSheet(this@OrderPaymentDetails, true)
                    .setTitle("Payment Failed")
                    .setMessage("Some Network Error Occurred")
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18) // In SP
                    .setMessageTextSize(14) // In SP
                    .setIconResource(R.drawable.info)
                    .setCancelable(false)
                    .setPositiveButton("Okay", object : DialogSheet.OnPositiveClickListener {
                        public override fun onClick(view: View?) {}
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show()
        } else if (i == Checkout.INVALID_OPTIONS) {
            DialogSheet(this@OrderPaymentDetails, true)
                    .setTitle("Error Loading Payment Page")
                    .setMessageTextSize(14) // In SP
                    .setMessage("Please Try Again")
                    .setColoredNavigationBar(true)
                    .setTitleTextSize(18) // In SP
                    .setIconResource(R.drawable.info)
                    .setCancelable(false)
                    .setPositiveButton("Okay", object : DialogSheet.OnPositiveClickListener {
                        public override fun onClick(view: View?) {}
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show()
        } else {
            DialogSheet(this@OrderPaymentDetails, true)
                    .setTitle("Some Error Occurred")
                    .setMessage("Please Try Again")
                    .setColoredNavigationBar(true)
                    .setMessageTextSize(14) // In SP
                    .setTitleTextSize(18) // In SP
                    .setCancelable(false)
                    .setIconResource(R.drawable.info)
                    .setPositiveButton("Okay", object : DialogSheet.OnPositiveClickListener {
                        public override fun onClick(view: View?) {}
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show()
        }
    }

    inner class ItemViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var size: TextView
        var price: TextView
        var quantity: TextView
        var product: TextView
        var imageView: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            size = itemView.findViewById(R.id.specs)
            price = itemView.findViewById(R.id.price)
            imageView = itemView.findViewById(R.id.image)
            quantity = itemView.findViewById(R.id.quantity)
            product = itemView.findViewById(R.id.product)
        }
    }

    // function to generate a random string of length n
    fun getAlphaNumericString(n: Int): String {

        // chose a Character random from this String
        val AlphaNumericString: String = ("ABCDEFGHIJKLMNPQRSTUVXYZ"
                + "123456789")

        // create StringBuffer size of AlphaNumericString
        val sb: StringBuilder = StringBuilder(n)
        for (i in 0 until n) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            val index: Int = ((AlphaNumericString.length
                    * Math.random())).toInt()

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .get(index))
        }
        return sb.toString()
    }

    private fun checkOrder(): String {
        orderid = getAlphaNumericString(15)
        FirebaseDatabase.getInstance().getReference().child("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(orderid).exists()) {
                    checkOrder()
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        return orderid
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@OrderPaymentDetails, "right-to-left")
    }
}