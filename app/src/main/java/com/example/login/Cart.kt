package com.example.login


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import androidx.core.content.ContextCompat
import com.irozon.sneaker.Sneaker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.baoyachi.stepview.bean.StepBean
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.cardview.widget.CardView
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.example.login.Fragments.itemdetails
import com.bumptech.glide.Glide
import android.os.*
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.login.address.Address
import com.example.login.databinding.ActivityCartBinding
import com.example.login.dataclass.cartitemdetails
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class Cart : AppCompatActivity() {
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>
    var t: Double = 0.0
    var no: Int = 0

    lateinit var binding: ActivityCartBinding

    private val steps: Array<String> = arrayOf("Cart", "Address", "Payment", "Placed")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.executePendingBindings()

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        binding.continuetopay.setOnClickListener {
            startActivity(Intent(this@Cart, Address::class.java))
            CustomIntent.customType(this@Cart, "left-to-right")
        }
        val stepsBeanList: MutableList<StepBean> = ArrayList()
        val stepBean0: StepBean = StepBean("Cart", -1)
        val stepBean1: StepBean = StepBean("Address", 0)
        val stepBean2: StepBean = StepBean("Payment", 0)
        val stepBean3: StepBean = StepBean("Placed", 0)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        binding.stepsView.setStepViewTexts(stepsBeanList) //???
                .setTextSize(12) //set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsViewIndicator??????
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray)) //??StepsViewIndicator???????
                .setStepViewComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text??????
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.darkgrey)) //??StepsView text???????
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.checkedd)) //??StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_radio_button_checked_24)) //??StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uncheckedd))

        binding.back.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                onBackPressed()
            }
        })
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    YoYo.with(Techniques.FadeInDown)
                            .duration(1000)
                            .playOn(binding.cartrecyview)
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                t = 0.0
                no = 0
                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference().child("Items").child((dataSnapshot.child("id").getValue(String::class.java))!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        public override fun onDataChange(snapshot2: DataSnapshot) {
                            t = t + ((dataSnapshot.child("quantity").getValue(Int::class.java)))!! * Integer.valueOf(snapshot2.child("price").getValue(String::class.java))
                            val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                            binding.subtotal.setText(format.format(BigDecimal(t.toString())))
                        }

                        public override fun onCancelled(error: DatabaseError) {}
                    })
                    no++
                    if (no.toLong() == snapshot.getChildrenCount()) {
                        val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                        binding.subtotal.setText(format.format(BigDecimal(t.toString())))
                    }
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addValueEventListener(object : ValueEventListener {
            public override fun onDataChange(snapshot: DataSnapshot) {
                val handler: Handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable {
                    public override fun run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.progresslayout.setVisibility(View.GONE)
                    }
                }, 700)
                if (!snapshot.exists()) {
                    binding.cartlayout.setVisibility(View.GONE)
                    findViewById<View>(R.id.emptycartimg).setVisibility(View.VISIBLE)
                    findViewById<View>(R.id.emptycarttext).setVisibility(View.VISIBLE)
                } else {
                    binding.cartlayout.setVisibility(View.VISIBLE)
                    findViewById<View>(R.id.emptycartimg).setVisibility(View.GONE)
                    findViewById<View>(R.id.emptycarttext).setVisibility(View.GONE)
                }
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        val query: Query = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser()!!.getUid()).child("Cart")
        binding.emptycart.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val mDialog: MaterialDialog = MaterialDialog.Builder(this@Cart)
                        .setTitle("Empty The Cart?")
                        .setMessage("Delete All Items")
                        .setCancelable(false)
                        .setPositiveButton("Empty", R.drawable.delete2, object : AbstractDialog.OnClickListener {
                            public override fun onClick(dialogInterface: DialogInterface, which: Int) {
                                query.getRef().removeValue().addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    public override fun onComplete(task: Task<Void?>) {
                                        if (task.isSuccessful()) {
                                            Sneaker.with(this@Cart)
                                                    .setTitle("Cart Empty", R.color.white)
                                                    .setMessage("All Items Removed From Cart", R.color.white)
                                                    .setDuration(2000)
                                                    .setIcon(R.drawable.delete2, R.color.white)
                                                    .autoHide(true)
                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                    .setCornerRadius(10, 0)
                                                    .sneak(R.color.green)
                                            dialogInterface.dismiss()
                                        } else {
                                            Sneaker.with(this@Cart)
                                                    .setTitle("Error Emptying Cart ", R.color.white)
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
                mDialog.show()
            }
        })
        val options: FirebaseRecyclerOptions<cartitemdetails> = FirebaseRecyclerOptions.Builder<cartitemdetails>()
                .setQuery(query, object : SnapshotParser<cartitemdetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): cartitemdetails {
                        return cartitemdetails(snapshot.child("size").getValue(String::class.java), snapshot.child("sizename").getValue(String::class.java), snapshot.child("colorcode").getValue(String::class.java), snapshot.child("colorname").getValue(String::class.java), snapshot.child("id").getValue(String::class.java), snapshot.child("image").getValue(String::class.java), snapshot.child("quantity").getValue(Int::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<cartitemdetails, CartViewHolder>(options) {
            public override fun onViewAttachedToWindow(holder: CartViewHolder) {
                super.onViewAttachedToWindow(holder)
                binding.itemno.setText(firebaseRecyclerAdapter!!.getItemCount().toString() + " items")
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onBindViewHolder(holder: CartViewHolder, @SuppressLint("RecyclerView") position: Int, model: cartitemdetails) {
                holder.move.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        binding.progresslayout.setVisibility(View.VISIBLE)
                        val fromPath: DatabaseReference = firebaseRecyclerAdapter!!.getRef(position)
                        val toPath: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist")
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
                        holder.quantity.setText("" + model.quantity)
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
                holder.size.setText("Color: " + model.colorname + ", " + "Size : " + model.size)
                Glide.with(getApplicationContext()).load(model.image).into(holder.image)
                holder.add.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        binding.progresslayout.setVisibility(View.VISIBLE)
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        firebaseRecyclerAdapter!!.getRef(position).addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.child("quantity").getRef().setValue(snapshot.child("quantity").getValue(Int::class.java)!! + 1).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    public override fun onComplete(task: Task<Void?>) {
                                        val handler: Handler = Handler(Looper.getMainLooper())
                                        handler.postDelayed(object : Runnable {
                                            public override fun run() {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                binding.progresslayout.setVisibility(View.GONE)
                                            }
                                        }, 700)
                                    }
                                })
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                        //holder.quantity.setText((Integer.valueOf(holder.quantity.getText().toString()) + 1) + "");
                    }
                })
                holder.minus.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        firebaseRecyclerAdapter!!.getRef(position).addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.child("quantity").getValue(Int::class.java)!! > 1) {
                                    binding.progresslayout.setVisibility(View.VISIBLE)
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    snapshot.child("quantity").getRef().setValue(snapshot.child("quantity").getValue(Int::class.java)!! - 1).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        public override fun onComplete(task: Task<Void?>) {
                                            val handler: Handler = Handler(Looper.getMainLooper())
                                            handler.postDelayed(object : Runnable {
                                                public override fun run() {
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    binding.progresslayout.setVisibility(View.GONE)
                                                }
                                            }, 700)
                                        }
                                    })
                                }
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                })
                holder.delete.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        val mDialog: MaterialDialog = MaterialDialog.Builder(this@Cart)
                                .setTitle("Remove Item From Cart?")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.delete2, object : AbstractDialog.OnClickListener {
                                    public override fun onClick(dialogInterface: DialogInterface, which: Int) {
                                        firebaseRecyclerAdapter!!.getRef(position).removeValue().addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful()) {
                                                    dialogInterface.dismiss()
                                                    firebaseRecyclerAdapter!!.notifyDataSetChanged()
                                                    Sneaker.with(this@Cart)
                                                            .setTitle("Item Removed From Cart", R.color.white)
                                                            .setMessage(firebaseRecyclerAdapter!!.getItemCount().toString() + " item(s) left in cart", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.delete2, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.green)
                                                } else {
                                                    Sneaker.with(this@Cart)
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
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem, parent, false)
                return CartViewHolder(view)
            }
        }
        binding.cartrecyview.setLayoutManager(LinearLayoutManager(getApplicationContext()))
        binding.cartrecyview.setAdapter(firebaseRecyclerAdapter)
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter!!.stopListening()
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@Cart, "up-to-bottom")
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
            price = itemView.findViewById(R.id.price)
            move = itemView.findViewById(R.id.movetowishlist)
            quantity = itemView.findViewById(R.id.quantity)
            add = itemView.findViewById(R.id.plus)
            product = itemView.findViewById(R.id.product)
            minus = itemView.findViewById(R.id.minus)
            delete = itemView.findViewById(R.id.delete)
        }
    }
}