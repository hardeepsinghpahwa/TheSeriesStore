package com.example.login


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.fragments.itemdetails
import com.bumptech.glide.Glide
import android.graphics.Bitmap
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityMyOrdersBinding
import com.example.login.dataclass.cartitemdetails
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MyOrders : AppCompatActivity() {
    var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>? = null
    var imgs: ArrayList<Bitmap>? = null
    var i: Int = 0
    lateinit var binding: ActivityMyOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders)
        binding.executePendingBindings()

        imgs = ArrayList()
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val query: Query =
            FirebaseDatabase.getInstance().reference.child("Orders").orderByChild("userid").equalTo(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
        val options: FirebaseRecyclerOptions<cartitemdetails> =
            FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(
                    query
                ) { snapshot ->
                    cartitemdetails(
                        snapshot.child("size").getValue(String::class.java),
                        snapshot.child("sizename").getValue(String::class.java),
                        snapshot.child("colorcode").getValue(String::class.java),
                        snapshot.child("colorname").getValue(String::class.java),
                        snapshot.key,
                        snapshot.child("image").getValue(String::class.java),
                        snapshot.child("quantity").getValue(Int::class.java)
                    )
                }.build()
        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {
                override fun onViewAttachedToWindow(holder: CartViewHolder) {
                    super.onViewAttachedToWindow(holder)
                    // itemsnumber.setText(firebaseRecyclerAdapter.getItemCount()+" items");
                    //progresslayout.setVisibility(View.GONE);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onBindViewHolder(
                    holder: CartViewHolder,
                    @SuppressLint("RecyclerView") position: Int,
                    model: cartitemdetails
                ) {
                    imgs = ArrayList()
                    if (position % 2 != 0) {
                        holder.itemView.setBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.lightgrey
                            )
                        )
                    }
                    firebaseRecyclerAdapter!!.getRef(position)
                        .child("tracking").ref.orderByChild("timestamp")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot: DataSnapshot in snapshot.children) {
                                    holder.status.text = dataSnapshot.child("text")
                                        .getValue(String::class.java) + "\n" + getTime(
                                        (dataSnapshot.child(
                                            "timestamp"
                                        ).getValue(Long::class.java))!!
                                    )
                                    break
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    firebaseRecyclerAdapter!!.getRef(position).child("items")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                /* itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                                holder.name.setText(itemdetails.getName());
                                Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                                holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));
        */
                                // holder.product.setText(itemdetails.getProduct());

                                // holder.quantity.setText(""+model.getQuantity());
                                val cartitemdetails: cartitemdetails? =
                                    snapshot.getValue(cartitemdetails::class.java)
                                if (snapshot.childrenCount == 1L) {
                                    holder.itemsnum.visibility = View.GONE
                                } else {
                                    holder.itemsnum.text =
                                        "+ " + (snapshot.childrenCount - 1) + " items"
                                }
                                snapshot.ref.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                                            Glide.with(applicationContext).load(
                                                dataSnapshot.child("image")
                                                    .getValue(String::class.java)
                                            ).into(holder.image)
                                            holder.specs.text =
                                                "Size : " + dataSnapshot.child("size")
                                                    .getValue(String::class.java) + ", " + dataSnapshot.child(
                                                    "colorname"
                                                ).getValue(String::class.java)
                                            FirebaseDatabase.getInstance().reference.child("Items")
                                                .child(
                                                    (dataSnapshot.child("id")
                                                        .getValue(String::class.java))!!
                                                ).addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val itemdetails: itemdetails? =
                                                        snapshot.getValue(itemdetails::class.java)
                                                    i = 0
                                                    holder.name.text = itemdetails!!.name
                                                }

                                                override fun onCancelled(error: DatabaseError) {}
                                            })
                                            break
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    holder.itemView.setOnClickListener {
                        val intent = Intent(this@MyOrders, ViewOrder::class.java)
                        intent.putExtra("id", firebaseRecyclerAdapter!!.getItem(position).id)
                        startActivity(intent)
                        CustomIntent.customType(this@MyOrders, "left-to-right")
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
                    val view: View =
                        LayoutInflater.from(parent.context).inflate(R.layout.order, parent, false)
                    return CartViewHolder(view)
                }
            }
        binding.ordersrecyview.layoutManager = LinearLayoutManager(applicationContext)
        binding.ordersrecyview.adapter = firebaseRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@MyOrders, "right-to-left")
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var specs: TextView
        var itemsnum: TextView
        var status: TextView
        var image: ImageView

        init {
            itemsnum = itemView.findViewById(R.id.itemsno)
            name = itemView.findViewById(R.id.name)
            image = itemView.findViewById(R.id.images1)
            specs = itemView.findViewById(R.id.specs)
            status = itemView.findViewById(R.id.status)
        }
    }

    private fun getTime(time: Long): String {
        val c: Calendar = Calendar.getInstance()
        c.timeInMillis = time
        val d: Date = c.time
        val sdf: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aa")
        return sdf.format(d)
    }
}