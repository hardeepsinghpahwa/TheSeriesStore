package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.irozon.sneaker.Sneaker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.cardview.widget.CardView
import com.example.login.fragments.itemdetails
import com.bumptech.glide.Glide
import android.os.*
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityWishlistBinding
import com.example.login.dataclass.cartitemdetails
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class Wishlist : AppCompatActivity() {
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>
    lateinit var binding:ActivityWishlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_wishlist)
        binding.executePendingBindings()

        findViewById<View>(R.id.back).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        findViewById<View>(R.id.cart).setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(this@Wishlist, Cart::class.java))
                CustomIntent.customType(this@Wishlist, "left-to-right")
            }
        })
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist").addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                val handler: Handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable {
                    public override fun run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.progresslayout.setVisibility(View.GONE)
                    }
                }, 700)
                if (!snapshot.exists()) {
                    findViewById<View>(R.id.emptywishlistimg).setVisibility(View.VISIBLE)
                    findViewById<View>(R.id.emptyishlisttext).setVisibility(View.VISIBLE)
                } else {
                    findViewById<View>(R.id.emptywishlistimg).setVisibility(View.VISIBLE)
                    findViewById<View>(R.id.emptyishlisttext).setVisibility(View.VISIBLE)
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        val query: Query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist")
        val options: FirebaseRecyclerOptions<cartitemdetails> = FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, object : SnapshotParser<cartitemdetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): cartitemdetails {
                        return cartitemdetails(snapshot.child("size").getValue(String::class.java), snapshot.child("sizename").getValue(String::class.java), snapshot.child("colorcode").getValue(String::class.java), snapshot.child("colorname").getValue(String::class.java), snapshot.child("id").getValue(String::class.java), snapshot.child("image").getValue(String::class.java), snapshot.child("quantity").getValue(Int::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {
            public override fun onViewAttachedToWindow(holder: CartViewHolder) {
                super.onViewAttachedToWindow(holder)
                binding.progresslayout.setVisibility(View.GONE)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onBindViewHolder(holder: CartViewHolder, @SuppressLint("RecyclerView") position: Int, model: cartitemdetails) {
                holder.move.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        binding.progresslayout.setVisibility(View.VISIBLE)
                        val fromPath: DatabaseReference = firebaseRecyclerAdapter!!.getRef(position)
                        val toPath: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart")
                        fromPath.addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(dataSnapshot: DataSnapshot) {
                                toPath.child(UUID.randomUUID().toString()).setValue(dataSnapshot.getValue(), object : DatabaseReference.CompletionListener {
                                    public override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                                        binding.progresslayout.setVisibility(View.GONE)
                                        fromPath.removeValue()
                                    }
                                }
                                )
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                })
                FirebaseDatabase.getInstance().getReference().child("Items").child(model.id!!).addValueEventListener(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        val itemdetails: itemdetails? = snapshot.getValue(itemdetails::class.java)
                        holder.name.setText(itemdetails!!.name)
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        holder.price.setText(format.format(BigDecimal(itemdetails.price)))
                        holder.product.setText(itemdetails.product)
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                holder.size.setText("Color: " + model.colorname + ", " + "Size : " + model.size)
                Glide.with(getApplicationContext()).load(model.image).into(holder.image)
                holder.delete.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        val mDialog: MaterialDialog = MaterialDialog.Builder(this@Wishlist)
                                .setTitle("Remove Item From Wishlist?")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.delete2, object : AbstractDialog.OnClickListener {
                                    public override fun onClick(dialogInterface: DialogInterface, which: Int) {
                                        firebaseRecyclerAdapter.getRef(position).removeValue().addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful()) {
                                                    dialogInterface.dismiss()
                                                    firebaseRecyclerAdapter!!.notifyDataSetChanged()
                                                    Sneaker.with(this@Wishlist)
                                                            .setTitle("Item Removed From Wishlist", R.color.white)
                                                            .setMessage(firebaseRecyclerAdapter!!.getItemCount().toString() + " item(s) left in cart", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green)
                                                } else {
                                                    Sneaker.with(this@Wishlist)
                                                            .setTitle("Error Removing Item ", R.color.white)
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
                                })
                                .setNegativeButton("Cancel", R.drawable.cancel, object : AbstractDialog.OnClickListener {
                                    public override fun onClick(dialogInterface: DialogInterface, which: Int) {
                                        dialogInterface.dismiss()
                                    }
                                })
                                .build()

                        // Show Dialog
                        mDialog.show()
                    }
                })
            }

            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlistitem, parent, false)
                return CartViewHolder(view)
            }
        }
        binding.wishlistrecyview.setLayoutManager(LinearLayoutManager(this@Wishlist))
        binding.wishlistrecyview.setAdapter(firebaseRecyclerAdapter)
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter!!.stopListening()
    }

    inner class CartViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var size: TextView
        var price: TextView
        var quantity: TextView
        var product: TextView
        var move: TextView
        var add: ImageView
        var minus: ImageView
        var image: ImageView
        var delete: CardView

        init {
            name = itemView.findViewById(R.id.name)
            image = itemView.findViewById(R.id.image)
            size = itemView.findViewById(R.id.specs)
            move = itemView.findViewById(R.id.movetowishlist)
            price = itemView.findViewById(R.id.price)
            quantity = itemView.findViewById(R.id.quantity)
            add = itemView.findViewById(R.id.plus)
            product = itemView.findViewById(R.id.product)
            minus = itemView.findViewById(R.id.minus)
            delete = itemView.findViewById(R.id.delete)
        }
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@Wishlist, "right-to-left")
    }
}