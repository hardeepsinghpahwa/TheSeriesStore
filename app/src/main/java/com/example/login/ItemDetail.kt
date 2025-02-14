package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.bumptech.glide.Glide
import com.irozon.sneaker.interfaces.OnSneakerClickListener
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.login.databinding.ActivityItemDetailBinding
import com.example.login.dataclass.colordetails
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class ItemDetail : AppCompatActivity() {

    lateinit var id: String
    lateinit var colorcode: String
    lateinit var colorname: String
    lateinit var size: String
    lateinit var sizename: String
    lateinit var currentimage: String
    var no: Int = 0
    var count: Int = 0
    lateinit var binding:ActivityItemDetailBinding
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<colordetails, ColorViewHolder>
    lateinit var firebaseRecyclerAdapter2: FirebaseRecyclerAdapter<colordetails, SizeViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail)
        binding.executePendingBindings()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.cart.setOnClickListener {
            startActivity(Intent(this@ItemDetail, Cart::class.java))
            CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
        }

        //id = "01";
        id = intent.getStringExtra("id")!!
        FirebaseDatabase.getInstance().reference.child("Items").child((id)).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.progresslayout.visibility = View.GONE
                binding.name.text = Objects.requireNonNull(snapshot).child("name").getValue(String::class.java)
                val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                binding.price.text =
                    format.format(BigDecimal(Objects.requireNonNull(snapshot).child("price").getValue(String::class.java)))
                Glide.with(applicationContext).load(Objects.requireNonNull(snapshot).child("image").getValue(String::class.java)).into(binding.image)
                YoYo.with(Techniques.FadeInDown)
                        .duration(500)
                        .playOn(binding.image)
                currentimage = snapshot.child("image").getValue(String::class.java)!!
                binding.product.text = Objects.requireNonNull(snapshot).child("product").getValue(String::class.java)
                if (snapshot.child("detail1").getValue(String::class.java) != null) {
                    binding.detail1.text = snapshot.child("detail1").getValue(String::class.java)
                } else {
                    binding.detail1.visibility = View.GONE
                }
                if (snapshot.child("detail2").getValue(String::class.java) != null) {
                    binding.detail2.text = snapshot.child("detail2").getValue(String::class.java)
                } else {
                    binding.detail2.visibility = View.GONE
                }
                if (snapshot.child("detail3").getValue(String::class.java) != null) {
                    binding.detail3.text = snapshot.child("detail3").getValue(String::class.java)
                } else {
                    binding.detail3.visibility = View.GONE
                }
                if (snapshot.child("detail4").getValue(String::class.java) != null) {
                    binding.detail4.text = snapshot.child("detail4").getValue(String::class.java)
                } else {
                    binding.detail4.visibility = View.GONE
                }
                binding.wishlistitem.setOnClickListener {
                        run {
                            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            binding.progresslayout.bringToFront()
                            binding.progresslayout.visibility = View.VISIBLE
                            FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Wishlist").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    count = 0
                                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                                        if ((dataSnapshot.child("id").getValue(String::class.java) == id) && (dataSnapshot.child("colorcode").getValue(String::class.java) == colorcode) && (dataSnapshot.child("size").getValue(String::class.java) == size)) {
                                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                            binding.progresslayout.visibility = View.GONE
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
                                                        override fun onSneakerClick(view: View) {
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
                                        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Wishlist").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful) {
                                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    binding.progresslayout.visibility = View.GONE
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
                                                                override fun onSneakerClick(view: View) {
                                                                    startActivity(Intent(this@ItemDetail, Wishlist::class.java))
                                                                    CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                                }
                                                            })
                                                            .sneak(R.color.green)
                                                } else {
                                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                    binding.progresslayout.visibility = View.GONE
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

                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                }

                binding.addtocart.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.progresslayout.bringToFront()
                        binding.progresslayout.visibility = View.VISIBLE
                        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                count = 0
                                for (dataSnapshot: DataSnapshot in snapshot.children) {
                                    if ((dataSnapshot.child("id").getValue(String::class.java) == id) && (dataSnapshot.child("colorcode").getValue(String::class.java) == colorcode) && (dataSnapshot.child("size").getValue(String::class.java) == size)) {
                                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        binding.progresslayout.visibility = View.GONE
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
                                                    override fun onSneakerClick(view: View) {
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
                                    FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").child(UUID.randomUUID().toString()).setValue(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
                                        override fun onComplete(task: Task<Void?>) {
                                            if (task.isSuccessful) {
                                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                binding.progresslayout.visibility = View.GONE
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
                                                            override fun onSneakerClick(view: View) {
                                                                startActivity(Intent(this@ItemDetail, Cart::class.java))
                                                                CustomIntent.customType(this@ItemDetail, "fadein-to-fadeout")
                                                            }
                                                        })
                                                        .sneak(R.color.green)
                                            } else {
                                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                                binding.progresslayout.visibility = View.GONE
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

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val query: Query = FirebaseDatabase.getInstance().reference.child("Items").child((id)).child("colors")
        val options: FirebaseRecyclerOptions<colordetails> = FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query, object : SnapshotParser<colordetails?> {
                    override fun parseSnapshot(snapshot: DataSnapshot): colordetails {
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
                    holder.colorlayout.background = ContextCompat.getDrawable(applicationContext, R.drawable.greystroke)
                }
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        colorcode = firebaseRecyclerAdapter.getItem(position).colorcode!!
                        colorname = firebaseRecyclerAdapter.getItem(position).name!!
                        for (i in 0 until firebaseRecyclerAdapter.itemCount) {
                            if (position == i) {
                                val view: View? = binding.colorsrecyview.layoutManager!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.background =
                                        ContextCompat.getDrawable(
                                            applicationContext, R.drawable.greystroke)
                                }
                                FirebaseDatabase.getInstance().reference.child("Items").child((id)).child("images").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        no = 0
                                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                                            if (no == position) {
                                                currentimage = dataSnapshot.child("image").getValue(String::class.java)!!
                                                Glide.with(applicationContext).load(dataSnapshot.child("image").getValue(String::class.java)).into(binding.image)
                                                YoYo.with(Techniques.FadeInDown)
                                                        .duration(500)
                                                        .playOn(binding.image)
                                                break
                                            }
                                            no++
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            } else {
                                val view: View? = binding.colorsrecyview.layoutManager!!.findViewByPosition(i)
                                if (view != null) {
                                    val relativeLayout: RelativeLayout? = view.findViewById(R.id.colorlayout)
                                    if (relativeLayout != null) relativeLayout.background = null
                                }
                            }
                        }
                    }
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.coloritem, parent, false)
                return ColorViewHolder(view)
            }
        }
        binding.colorsrecyview.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        binding.colorsrecyview.adapter = firebaseRecyclerAdapter
        val query2: Query = FirebaseDatabase.getInstance().reference.child("Items").child((id)).child("sizes")
        val options2: FirebaseRecyclerOptions<colordetails> = FirebaseRecyclerOptions.Builder<colordetails>()
                .setQuery(query2
                ) { snapshot ->
                    colordetails(
                        snapshot.child("size").getValue(String::class.java),
                        snapshot.child("name").getValue(String::class.java)
                    )
                }.build()
        firebaseRecyclerAdapter2 = object : FirebaseRecyclerAdapter<colordetails, SizeViewHolder>(options2) {
            override fun onBindViewHolder(holder: SizeViewHolder, @SuppressLint("RecyclerView") position: Int, model: colordetails) {
                holder.size.text = model.colorcode
                if (position == 0) {
                    size = model.colorcode!!
                    sizename = model.name!!
                    holder.colorlayout.background = ContextCompat.getDrawable(applicationContext, R.drawable.greystroke)
                }
                holder.itemView.setOnClickListener {
                    size = firebaseRecyclerAdapter2.getItem(position).colorcode!!
                    sizename = firebaseRecyclerAdapter2.getItem(position).name!!
                    for (i in 0 until firebaseRecyclerAdapter2.itemCount) {
                        if (position == i) {
                            val view: View? =
                                binding.sizerecyview.layoutManager!!.findViewByPosition(i)
                            if (view != null) {
                                val relativeLayout: RelativeLayout? =
                                    view.findViewById(R.id.colorlayout)
                                if (relativeLayout != null) relativeLayout.background =
                                    ContextCompat.getDrawable(
                                        applicationContext, R.drawable.greystroke
                                    )
                            }
                        } else {
                            val view: View? =
                                binding.sizerecyview.layoutManager!!.findViewByPosition(i)
                            if (view != null) {
                                val relativeLayout: RelativeLayout? =
                                    view.findViewById(R.id.colorlayout)
                                if (relativeLayout != null) relativeLayout.background = null
                            }
                        }
                    }
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sizeitem, parent, false)
                return SizeViewHolder(view)
            }
        }
        binding.sizerecyview.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        binding.sizerecyview.adapter = firebaseRecyclerAdapter2
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter.startListening()
        firebaseRecyclerAdapter2.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseRecyclerAdapter.stopListening()
        firebaseRecyclerAdapter2.startListening()
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorlayout: RelativeLayout
        var color: RelativeLayout

        init {
            color = itemView.findViewById(R.id.color)
            colorlayout = itemView.findViewById(R.id.colorlayout)
        }
    }

    inner class SizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorlayout: RelativeLayout
        var size: TextView

        init {
            size = itemView.findViewById(R.id.specs)
            colorlayout = itemView.findViewById(R.id.colorlayout)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CustomIntent.customType(this@ItemDetail, "right-to-left")
    }
}