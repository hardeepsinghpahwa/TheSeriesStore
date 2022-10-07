package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.irozon.sneaker.Sneaker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import android.content.Intent
import maes.tech.intentanim.CustomIntent
import com.firebase.ui.database.FirebaseRecyclerOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.cardview.widget.CardView
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.bumptech.glide.Glide
import com.irozon.sneaker.interfaces.OnSneakerClickListener
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class ItemDetail : AppCompatActivity() {
    lateinit var image: ImageView
    lateinit var back: CardView
    lateinit var cart: CardView
    lateinit var name: TextView
    lateinit var price: TextView
    lateinit var product: TextView
    lateinit var detail1: TextView
    lateinit var detail2: TextView
    lateinit var detail3: TextView
    lateinit var detail4: TextView
    lateinit var addtowishlist: TextView
    lateinit var addtocart: TextView
    lateinit var colors: RecyclerView
    lateinit var sizes: RecyclerView
    lateinit var id: String
    lateinit var colorcode: String
    lateinit var colorname: String
    lateinit var size: String
    lateinit var sizename: String
    lateinit var currentimage: String
    var no: Int = 0
    var count: Int = 0
    lateinit var progresslayout: ConstraintLayout
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<colordetails, ColorViewHolder>
    lateinit var firebaseRecyclerAdapter2: FirebaseRecyclerAdapter<colordetails, SizeViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        colors = findViewById(R.id.colorsrecyview)
        image = findViewById(R.id.image)
        name = findViewById(R.id.name)
        price = findViewById(R.id.price)
        sizes = findViewById(R.id.sizerecyview)
        product = findViewById(R.id.product)
        detail1 = findViewById(R.id.detail1)
        detail2 = findViewById(R.id.detail2)
        detail3 = findViewById(R.id.detail3)
        detail4 = findViewById(R.id.detail4)
        addtocart = findViewById(R.id.addtocart)
        addtowishlist = findViewById(R.id.wishlistitem)
        cart = findViewById(R.id.cart)
        back = findViewById(R.id.back)
        progresslayout = findViewById(R.id.progresslayout)
        back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })
        cart.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@ItemDetail, Cart::class.java))
                CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
            }
        })

        //id = "01";
        id = getIntent().getStringExtra("id")!!
        FirebaseDatabase.getInstance().getReference().child("Items").child((id)!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progresslayout.setVisibility(View.GONE)
                name.setText(Objects.requireNonNull(snapshot).child("name").getValue(String::class.java))
                val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                price.setText(format.format(BigDecimal(Objects.requireNonNull(snapshot).child("price").getValue(String::class.java))))
                Glide.with(getApplicationContext()).load(Objects.requireNonNull(snapshot).child("image").getValue(String::class.java)).into(image)
                YoYo.with(Techniques.FadeInDown)
                        .duration(500)
                        .playOn(image)
                currentimage = snapshot.child("image").getValue(String::class.java)!!
                product.setText(Objects.requireNonNull(snapshot).child("product").getValue(String::class.java))
                if (snapshot.child("detail1").getValue(String::class.java) != null) {
                    detail1.setText(snapshot.child("detail1").getValue(String::class.java))
                } else {
                    detail1.setVisibility(View.GONE)
                }
                if (snapshot.child("detail2").getValue(String::class.java) != null) {
                    detail2.setText(snapshot.child("detail2").getValue(String::class.java))
                } else {
                    detail2.setVisibility(View.GONE)
                }
                if (snapshot.child("detail3").getValue(String::class.java) != null) {
                    detail3.setText(snapshot.child("detail3").getValue(String::class.java))
                } else {
                    detail3.setVisibility(View.GONE)
                }
                if (snapshot.child("detail4").getValue(String::class.java) != null) {
                    detail4.setText(snapshot.child("detail4").getValue(String::class.java))
                } else {
                    detail4.setVisibility(View.GONE)
                }
                addtowishlist.setOnClickListener {
                        run {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            progresslayout.bringToFront()
                            progresslayout.setVisibility(View.VISIBLE)
                            FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist").addListenerForSingleValueEvent(object : ValueEventListener {
                                public override fun onDataChange(snapshot: DataSnapshot) {
                                    count = 0
                                    for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                        if ((dataSnapshot.child("id").getValue(String::class.java) == id) && (dataSnapshot.child("colorcode").getValue(String::class.java) == colorcode) && (dataSnapshot.child("size").getValue(String::class.java) == size)) {
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                            progresslayout.setVisibility(View.GONE)
                                            count++
                                            Sneaker.with(this@ItemDetail)
                                                    .setTitle("Item Already In Wishlist", R.color.white)
                                                    .setMessage("Check Your Wishlist", R.color.white)
                                                    .setDuration(2000)
                                                    .setIcon(R.drawable.info, R.color.white)
                                                    .autoHide(true)
                                                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                    .setCornerRadius(10, 0)
                                                    .setOnSneakerClickListener(object : OnSneakerClickListener {
                                                        public override fun onSneakerClick(view: View) {
                                                            startActivity(Intent(this@ItemDetail, Cart::class.java))
                                                            CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                        }
                                                    })
                                                    .sneak(R.color.teal_200)
                                            break
                                        }
                                    }
                                    if (count == 0) {
                                        val map = mutableMapOf<String?, Any?>()
                                        map.put("id", id)
                                        map.put("colorcode", colorcode)
                                        map.put("colorname", colorname)
                                        map.put("size", size)
                                        map.put("sizename", sizename)
                                        map.put("image", currentimage)
                                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wishlist").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful()) {
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    progresslayout.setVisibility(View.GONE)
                                                    Toast.makeText(this@ItemDetail, "Added", Toast.LENGTH_SHORT).show()
                                                    Sneaker.with(this@ItemDetail)
                                                            .setTitle("Item Added To Wishlist", R.color.white)
                                                            .setMessage("Check Your Wishlist", R.color.white)
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .setOnSneakerClickListener(object : OnSneakerClickListener {
                                                                public override fun onSneakerClick(view: View) {
                                                                    startActivity(Intent(this@ItemDetail, Wishlist::class.java))
                                                                    CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                                }
                                                            })
                                                            .sneak(R.color.green)
                                                } else {
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    progresslayout.setVisibility(View.GONE)
                                                    Sneaker.with(this@ItemDetail)
                                                            .setTitle("Error Adding Item To Wishlist", R.color.white)
                                                            .setMessage("Please Try Again")
                                                            .setDuration(2000)
                                                            .setIcon(R.drawable.info, R.color.white)
                                                            .autoHide(true)
                                                            .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                            .setCornerRadius(10, 0)
                                                            .sneak(R.color.teal_200)
                                                }
                                            }
                                        })
                                    }
                                }

                                public override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                }

                addtocart.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        progresslayout.bringToFront()
                        progresslayout.setVisibility(View.VISIBLE)
                        FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
                            public override fun onDataChange(snapshot: DataSnapshot) {
                                count = 0
                                for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                    if ((dataSnapshot.child("id").getValue(String::class.java) == id) && (dataSnapshot.child("colorcode").getValue(String::class.java) == colorcode) && (dataSnapshot.child("size").getValue(String::class.java) == size)) {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        progresslayout.setVisibility(View.GONE)
                                        count++
                                        Sneaker.with(this@ItemDetail)
                                                .setTitle("Item Already In Cart", R.color.white)
                                                .setMessage("Check Your Cart", R.color.white)
                                                .setDuration(2000)
                                                .setIcon(R.drawable.info, R.color.white)
                                                .autoHide(true)
                                                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                .setCornerRadius(10, 0)
                                                .setOnSneakerClickListener(object : OnSneakerClickListener {
                                                    public override fun onSneakerClick(view: View) {
                                                        startActivity(Intent(this@ItemDetail, Cart::class.java))
                                                        CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                    }
                                                })
                                                .sneak(R.color.teal_200)
                                        break
                                    }
                                }
                                if (count == 0) {
                                    val map= mutableMapOf<String?, Any?>()
                                    map.put("id", id)
                                    map.put("colorcode", colorcode)
                                    map.put("colorname", colorname)
                                    map.put("size", size)
                                    map.put("quantity", 1)
                                    map.put("sizename", sizename)
                                    map.put("image", currentimage)
                                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        public override fun onComplete(task: Task<Void?>) {
                                            if (task.isSuccessful()) {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                progresslayout.setVisibility(View.GONE)
                                                Toast.makeText(this@ItemDetail, "Added", Toast.LENGTH_SHORT).show()
                                                Sneaker.with(this@ItemDetail)
                                                        .setTitle("Item Added To Cart", R.color.white)
                                                        .setMessage("Check Your Cart", R.color.white)
                                                        .setDuration(2000)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .autoHide(true)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .setOnSneakerClickListener(object : OnSneakerClickListener {
                                                            public override fun onSneakerClick(view: View) {
                                                                startActivity(Intent(this@ItemDetail, Cart::class.java))
                                                                CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                            }
                                                        })
                                                        .sneak(R.color.green)
                                            } else {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                progresslayout.setVisibility(View.GONE)
                                                Sneaker.with(this@ItemDetail)
                                                        .setTitle("Error Adding Item To Cart", R.color.white)
                                                        .setMessage("Please Try Again")
                                                        .setDuration(2000)
                                                        .setIcon(R.drawable.info, R.color.white)
                                                        .autoHide(true)
                                                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                                        .setCornerRadius(10, 0)
                                                        .sneak(R.color.teal_200)
                                            }
                                        }
                                    })
                                }
                            }

                            public override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                })
            }

            public override fun onCancelled(error: DatabaseError) {}
        })
        val query: Query = FirebaseDatabase.getInstance().getReference().child("Items").child((id)!!).child("colors")
        val options: FirebaseRecyclerOptions<colordetails> = FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query, object : SnapshotParser<colordetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): colordetails {
                        return colordetails(snapshot.child("colorcode").getValue(String::class.java), snapshot.child("name").getValue(String::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<colordetails, ColorViewHolder>(options) {
            override fun onBindViewHolder(holder: ColorViewHolder, @SuppressLint("RecyclerView") position: Int, model: colordetails) {
                Log.i("color", model.colorcode!!)
                holder.color.setBackgroundColor(Color.parseColor(model.colorcode))
                if (position == 0) {
                    colorcode = model.colorcode!!
                    colorname = model.name!!
                    holder.colorlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke))
                }
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        colorcode = firebaseRecyclerAdapter!!.getItem(position).colorcode!!
                        colorname = firebaseRecyclerAdapter!!.getItem(position).name!!
                        for (i in 0 until firebaseRecyclerAdapter!!.getItemCount()) {
                            if (position == i) {
                                val view: View? = colors.getLayoutManager()!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke))
                                }
                                FirebaseDatabase.getInstance().getReference().child("Items").child((id)!!).child("images").addListenerForSingleValueEvent(object : ValueEventListener {
                                    public override fun onDataChange(snapshot: DataSnapshot) {
                                        no = 0
                                        for (dataSnapshot: DataSnapshot in snapshot.getChildren()) {
                                            if (no == position) {
                                                currentimage = dataSnapshot.child("image").getValue(String::class.java)!!
                                                Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String::class.java)).into(image)
                                                YoYo.with(Techniques.FadeInDown)
                                                        .duration(500)
                                                        .playOn(image)
                                                break
                                            }
                                            no++
                                        }
                                    }

                                    public override fun onCancelled(error: DatabaseError) {}
                                })
                            } else {
                                val view: View? = colors.getLayoutManager()!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.setBackground(null)
                                }
                            }
                        }
                    }
                })
            }

            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.coloritem, parent, false)
                return ColorViewHolder(view)
            }
        }
        colors.setLayoutManager(LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false))
        colors.setAdapter(firebaseRecyclerAdapter)
        val query2: Query = FirebaseDatabase.getInstance().getReference().child("Items").child((id)!!).child("sizes")
        val options2: FirebaseRecyclerOptions<colordetails> = FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query2, object : SnapshotParser<colordetails?> {
                    public override fun parseSnapshot(snapshot: DataSnapshot): colordetails {
                        return colordetails(snapshot.child("size").getValue(String::class.java), snapshot.child("name").getValue(String::class.java))
                    }
                }).build()
        firebaseRecyclerAdapter2 = object : FirebaseRecyclerAdapter<colordetails, SizeViewHolder>(options2) {
            override fun onBindViewHolder(holder: SizeViewHolder, @SuppressLint("RecyclerView") position: Int, model: colordetails) {
                holder.size.setText(model.colorcode)
                if (position == 0) {
                    size = model.colorcode!!
                    sizename = model.name!!
                    holder.colorlayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke))
                }
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        size = firebaseRecyclerAdapter2!!.getItem(position).colorcode!!
                        sizename = firebaseRecyclerAdapter2!!.getItem(position).name!!
                        for (i in 0 until firebaseRecyclerAdapter2!!.getItemCount()) {
                            if (position == i) {
                                val view: View? = sizes.getLayoutManager()!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.greystroke))
                                }
                            } else {
                                val view: View? = sizes.getLayoutManager()!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.setBackground(null)
                                }
                            }
                        }
                    }
                })
            }

            public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
                val view: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.sizeitem, parent, false)
                return SizeViewHolder(view)
            }
        }
        sizes.setLayoutManager(LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false))
        sizes.setAdapter(firebaseRecyclerAdapter2)
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
        firebaseRecyclerAdapter2!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter!!.stopListening()
        firebaseRecyclerAdapter2!!.startListening()
    }

    inner class ColorViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorlayout: RelativeLayout
        var color: RelativeLayout

        init {
            color = itemView.findViewById(R.id.color)
            colorlayout = itemView.findViewById(R.id.colorlayout)
        }
    }

    inner class SizeViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorlayout: RelativeLayout
        var size: TextView

        init {
            size = itemView.findViewById(R.id.specs)
            colorlayout = itemView.findViewById(R.id.colorlayout)
        }
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@ItemDetail, "right-to-left")
    }
}