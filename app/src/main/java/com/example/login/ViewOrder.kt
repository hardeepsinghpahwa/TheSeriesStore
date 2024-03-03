package com.example.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import maes.tech.intentanim.CustomIntent
import com.bumptech.glide.Glide
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.opensooq.pluto.PlutoView
import com.opensooq.pluto.base.PlutoAdapter
import com.example.login.databinding.ActivityViewOrderBinding
import com.example.login.dataclass.cartitemdetails
import com.opensooq.pluto.base.PlutoViewHolder
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewOrder : AppCompatActivity() {
    lateinit var id: String
    lateinit var products: ArrayList<cartitemdetails?>
    lateinit var binding: ActivityViewOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_order)
        binding.executePendingBindings()

        id = getIntent().getStringExtra("id")!!
        binding.back.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })

        products = ArrayList()
        val pluto: PlutoView = findViewById(R.id.slider_view)
        FirebaseDatabase.getInstance().getReference().child("Orders").child((id)!!).addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                binding.total.text =
                    format.format(BigDecimal(Objects.requireNonNull(snapshot).child("price").getValue(Long::class.java).toString()))
                //                subtotal.setText(format.format(new BigDecimal("")));
                binding.address.setText(snapshot.child("name").getValue(String::class.java) + "\n" + snapshot.child("phone").getValue(String::class.java) + "\n" + snapshot.child("address").getValue(String::class.java))
                snapshot.child("items").getRef().addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                            val ite: cartitemdetails? = dataSnapshot.getValue(cartitemdetails::class.java)
                            products.add((ite))
                        }
                        val adapter: YourAdapter = YourAdapter(products)
                        pluto.create(adapter, 2000, lifecycle)
                        pluto.setCustomIndicator(findViewById(R.id.indicator))
                        pluto.setDuration(5000)
                        pluto.stopAutoCycle()
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                snapshot.child("tracking").getRef().addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {

                        val stepsBeanList: MutableList<String> = ArrayList()
                        for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                            stepsBeanList.add(dataSnapshot.child("text").getValue(String::class.java) + ", " + getDate((dataSnapshot.child("timestamp").getValue(Long::class.java))!!))
                        }
                        binding.stepView.setStepsViewIndicatorComplectingPosition((snapshot.getChildrenCount() - 1).toInt()) //设置完成的步数
                                .setTextSize(12)
                                .reverseDraw(false) //default is true
                                .setStepViewTexts(stepsBeanList) //总步骤
                                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(applicationContext, R.color.darkgrey))
                                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(applicationContext, R.color.grey))
                                .setStepViewComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.darkgrey))
                                .setStepViewUnComplectedTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(applicationContext, R.drawable.outline_radio_button_checked_24))
                                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(applicationContext, R.drawable.checkedd))
                                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(applicationContext, R.drawable.outline_radio_button_checked_black_24))
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class YourAdapter constructor(items: ArrayList<cartitemdetails?>?) : PlutoAdapter<cartitemdetails?, PlutoViewHolder<cartitemdetails?>>((items)!!) {
        override fun getViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            return OrderViewHolder(parent, R.layout.orderitem)
        }

        inner class OrderViewHolder constructor(parent: ViewGroup?, itemLayoutId: Int) : PlutoViewHolder<cartitemdetails?>((parent)!!, itemLayoutId) {
            var image: ImageView
            var name: TextView
            var product: TextView
            var specs: TextView
            var price: TextView
            var quantity: TextView

            init {
                image = getView(R.id.image)
                name = getView(R.id.name)
                price = getView(R.id.price)
                specs = getView(R.id.specs)
                product = getView(R.id.product)
                quantity = getView(R.id.quantity)
            }

            public override fun set(item: cartitemdetails?, pos: Int) {
                //Glide.with(applicationContext).load(item.getImage()).into(image);
                specs.setText("Size : " + item!!.size + ", Color : " + item.colorname)
                quantity.setText("Quantity : " + item.quantity)
                Glide.with(applicationContext).load(item.image).into(image)
                FirebaseDatabase.getInstance().getReference().child("Items").child(item.id!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        name.setText(snapshot.child("name").getValue(String::class.java))
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        price.setText(format.format(BigDecimal(snapshot.child("price").getValue(String::class.java))))
                        product.setText(snapshot.child("product").getValue(String::class.java))
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@ViewOrder, "right-to-left")
    }

    private fun getDate(time: Long): String {
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(time)
        val d: Date = c.getTime()
        val sdf: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy hh:mm aa")
        return sdf.format(d)
    }
}