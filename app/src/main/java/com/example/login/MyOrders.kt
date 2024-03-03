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
    lateinit var binding:ActivityMyOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders)
        binding.executePendingBindings()

        imgs = ArrayList()
        binding.back.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        val query: Query = FirebaseDatabase.getInstance().getReference().child("Orders").orderByChild("userid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
        val options: FirebaseRecyclerOptions<cartitemdetails> = FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, object : SnapshotParser<cartitemdetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): cartitemdetails {
                        return cartitemdetails(snapshot.child("size").getValue(String::class.java), snapshot.child("sizename").getValue(String::class.java), snapshot.child("colorcode").getValue(String::class.java), snapshot.child("colorname").getValue(String::class.java), snapshot.getKey(), snapshot.child("image").getValue(String::class.java), snapshot.child("quantity").getValue(Int::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {
            public override fun onViewAttachedToWindow(holder: CartViewHolder) {
                super.onViewAttachedToWindow(holder)
                // itemsnumber.setText(firebaseRecyclerAdapter.getItemCount()+" items");
                //progresslayout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onBindViewHolder(holder: CartViewHolder, @SuppressLint("RecyclerView") position: Int, model: cartitemdetails) {
                imgs = ArrayList()
                if (position % 2 != 0) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightgrey))
                }
                firebaseRecyclerAdapter!!.getRef(position).child("tracking").getRef().orderByChild("timestamp").addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                            holder.status.setText(dataSnapshot.child("text").getValue(String::class.java) + ", " + getTime((dataSnapshot.child("timestamp").getValue(Long::class.java))!!))
                            break
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                firebaseRecyclerAdapter!!.getRef(position).child("items").addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        /* itemdetails itemdetails = snapshot.getValue(com.example.login.Fragments.itemdetails.class);

                        holder.name.setText(itemdetails.getName());
                        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
                        holder.price.setText(format.format(new BigDecimal(itemdetails.getPrice())));
*/
                        // holder.product.setText(itemdetails.getProduct());

                        // holder.quantity.setText(""+model.getQuantity());
                        val cartitemdetails: cartitemdetails? = snapshot.getValue(cartitemdetails::class.java)
                        if (snapshot.getChildrenCount() == 1L) {
                            holder.itemsnum.setVisibility(View.GONE)
                        } else {
                            holder.itemsnum.setText("+ " + (snapshot.getChildrenCount() - 1) + " items")
                        }
                        snapshot.getRef().addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                    Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String::class.java)).into(holder.image)
                                    holder.specs.setText("Size : " + dataSnapshot.child("size").getValue(String::class.java) + ", " + dataSnapshot.child("colorname").getValue(String::class.java))
                                    FirebaseDatabase.getInstance().getReference().child("Items").child((dataSnapshot.child("id").getValue(String::class.java))!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                        public override fun onDataChange(snapshot: DataSnapshot) {
                                            val itemdetails: itemdetails? = snapshot.getValue(itemdetails::class.java)
                                            i = 0
                                            holder.name.setText(itemdetails!!.name)
                                        }

                                        public override fun onCancelled(error: DatabaseError) {}
                                    })
                                    break
                                }
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        val intent: Intent = Intent(this@MyOrders, ViewOrder::class.java)
                        intent.putExtra("id", firebaseRecyclerAdapter!!.getItem(position).id)
                        startActivity(intent)
                        CustomIntent.customType(this@MyOrders, "left-to-right")
                    }
                })
            }

            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.order, parent, false)
                return CartViewHolder(view)
            }
        }
        binding.ordersrecyview.setLayoutManager(LinearLayoutManager(getApplicationContext()))
        binding.ordersrecyview.setAdapter(firebaseRecyclerAdapter)
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@MyOrders, "right-to-left")
    }

    inner class CartViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        c.setTimeInMillis(time)
        val d: Date = c.getTime()
        val sdf: SimpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aa")
        return sdf.format(d)
    }
}