package com.example.login

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.irozon.sneaker.Sneaker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.baoyachi.stepview.bean.StepBean
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.fragments.itemdetails
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
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityOrderPaymentDetailsBinding
import com.example.login.dataclass.cartitemdetails
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import java.lang.StringBuilder
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class OrderPaymentDetails : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>
    lateinit var name: String
    lateinit var phone: String
    lateinit var addresstext: String

    var t: Int = 0
    var no: Int = 0
    var orderid: String = ""
    lateinit var binding: ActivityOrderPaymentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_payment_details)
        binding.executePendingBindings()

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        checkOrder()
        binding.back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressedDispatcher.onBackPressed()
            }
        })
        name = intent.getStringExtra("name")!!
        addresstext = intent.getStringExtra("address")!!
        phone = intent.getStringExtra("phone")!!

        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                t = 0
                no = 0
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    FirebaseDatabase.getInstance().reference.child("Items").child((dataSnapshot.child("id").getValue(String::class.java))!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot2: DataSnapshot) {
                            t = t + ((dataSnapshot.child("quantity").getValue(Int::class.java)))!! * Integer.valueOf(snapshot2.child("price").getValue(String::class.java))
                            val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                            binding.total.text = format.format(BigDecimal(t.toString()))
                            binding.total2.text = format.format(BigDecimal(t.toString()))
                            binding.subtotal.text = format.format(BigDecimal(t.toString()))
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                    no++
                    if (no.toLong() == snapshot.childrenCount) {
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        binding.total.text = format.format(BigDecimal(t.toString()))
                        binding.total2.text = format.format(BigDecimal(t.toString()))
                        binding.subtotal.text = format.format(BigDecimal(t.toString()))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0 = StepBean("Cart", 1)
        val stepBean1 = StepBean("Address", 1)
        val stepBean2 = StepBean("Payment", -1)
        val stepBean3 = StepBean("Placed", 0)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        binding.stepsView.setStepViewTexts(stepsBeanList) //???
                .setTextSize(12) //set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(applicationContext, android.R.color.darker_gray)) //??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.darkgrey)) //??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(applicationContext, R.drawable.checkedd)) //??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(applicationContext, R.drawable.outline_radio_button_checked_24)) //??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(applicationContext, R.drawable.uncheckedd))
        val query: Query = FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart")
        val options: FirebaseRecyclerOptions<cartitemdetails> = FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, object : SnapshotParser<cartitemdetails?> {
                    override fun parseSnapshot(snapshot: DataSnapshot): cartitemdetails {
                        return cartitemdetails(snapshot.child("size").getValue(String::class.java), snapshot.child("sizename").getValue(String::class.java), snapshot.child("colorcode").getValue(String::class.java), snapshot.child("colorname").getValue(String::class.java), snapshot.child("id").getValue(String::class.java), snapshot.child("image").getValue(String::class.java), snapshot.child("quantity").getValue(Int::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<cartitemdetails, ItemViewHolder>(options) {
            override fun onViewAttachedToWindow(holder: ItemViewHolder) {
                super.onViewAttachedToWindow(holder)
                binding.progresslayout.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.itemsrecyview.scheduleLayoutAnimation()
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: cartitemdetails) {
                FirebaseDatabase.getInstance().reference.child("Items").child(model.id!!).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val itemdetails: itemdetails? = snapshot.getValue(itemdetails::class.java)
                        holder.name.text = itemdetails!!.name
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        holder.price.text = format.format(BigDecimal(itemdetails.price))
                        holder.product.text = itemdetails.product
                        holder.quantity.text = "Quantity : " + model.quantity
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
                holder.size.text = "Color: " + model.colorname + ", " + "Size : " + model.size
                Glide.with(applicationContext).load(model.image).into(holder.imageView)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.orderitem, parent, false)
                return ItemViewHolder(view)
            }
        }
        binding.itemsrecyview.layoutManager = LinearLayoutManager(this@OrderPaymentDetails)
        binding.itemsrecyview.adapter = firebaseRecyclerAdapter
        binding.continuetopay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                binding.progresslayout.visibility = View.VISIBLE
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //FirebaseDatabase.getInstance().getReference().child()
                FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val order = mutableMapOf<String?, Any?>()
                        order.put("name", name)
                        order.put("address", addresstext)
                        order.put("phone", phone)
                        order.put("email", snapshot.child("email").getValue(String::class.java))
                        order.put("price", t)
                        // order.put("discount", String.valueOf(dis));
                        // order.put("delivery", String.valueOf(del));
                        // order.put("total", total);
                        order.put("userid", FirebaseAuth.getInstance().currentUser.uid)
                        order.put("timestamp", ServerValue.TIMESTAMP)
                        order.put("status", "Payment Pending")
                        order.put("orderid", orderid)
                        order.put("type", "new")
                        val fromPath: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart")
                        val toPath: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Orders").child(orderid)
                        fromPath.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                toPath.child("items").setValue(dataSnapshot.value
                                ) { error, ref ->
                                    toPath.updateChildren(order)
                                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            override fun onComplete(p0: Task<Void?>) {
                                                if (p0.isSuccessful) {
                                                    val co: Checkout = Checkout()
                                                    val image: Int =
                                                        R.mipmap.ic_launcher // Can be any drawable
                                                    co.setKeyID("rzp_test_dt2sl2ttn6g114")
                                                    try {
                                                        val options: JSONObject = JSONObject()
                                                        options.put("name", "The Series Store")
                                                        options.put("description", "Order")
                                                        options.put("currency", "INR")
                                                        options.put("amount", t * 100)
                                                        options.put("send_sms_hash", true)
                                                        val preFill: JSONObject = JSONObject()
                                                        preFill.put(
                                                            "email",
                                                            snapshot.child("email")
                                                                .getValue(String::class.java)
                                                        )
                                                        preFill.put("contact", phone)
                                                        options.put("prefill", preFill)
                                                        co.open(this@OrderPaymentDetails, options)
                                                    } catch (e: JSONException) {
                                                        e.printStackTrace()
                                                    }
                                                } else {
                                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    binding.progresslayout.visibility = View.GONE
                                                    Sneaker.with(this@OrderPaymentDetails)
                                                        .setTitle(
                                                            "Some Error Occurred",
                                                            R.color.white
                                                        )
                                                        .setMessage(
                                                            "Please Try Again",
                                                            R.color.white
                                                        )
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

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter.stopListening()
    }

    override fun onPaymentSuccess(s: String, paymentData: PaymentData) {
        val map = mutableMapOf<String?, Any?>()
        map.put("text", "Order Placed")
        map.put("timestamp", ServerValue.TIMESTAMP)
        Log.i("payemnt", paymentData.toString())
        val order = mutableMapOf<String?, Any?>()
        order.put("status", "Payment Success")
        order.put("razorpay_payment_id", paymentData.paymentId)
        order.put("razorpay_order_id", paymentData.orderId)
        order.put("razorpay_signature", paymentData.signature)
        FirebaseDatabase.getInstance().reference.child("Orders").child(orderid).child("tracking").child(UUID.randomUUID().toString()).setValue(map)
        FirebaseDatabase.getInstance().reference.child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    binding.progresslayout.visibility = View.GONE
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

    override fun onPaymentError(i: Int, s: String, paymentData: PaymentData) {
        val order = mutableMapOf<String?, Any?>()
        order.put("status", "Payment Failed")
        FirebaseDatabase.getInstance().reference.child("Orders").child(orderid).updateChildren(order).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                }
            }
        })
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progresslayout.visibility = View.GONE
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
                        override fun onClick(view: View?) {}
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
                        override fun onClick(view: View?) {}
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
                        override fun onClick(view: View?) {}
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
                        override fun onClick(view: View?) {}
                    })
                    .setRoundedCorners(true) // Default value is true
                    .setBackgroundColor(Color.WHITE) // Your custom background color
                    .setButtonsColorRes(R.color.darkgrey) // You can use dialogSheetAccent style attribute instead
                    .show()
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        FirebaseDatabase.getInstance().reference.child("Orders").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(orderid).exists()) {
                    checkOrder()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return orderid
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@OrderPaymentDetails, "right-to-left")
    }
}