package com.example.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.login.databinding.ActivityAllProductScreenBinding
import com.example.login.fragments.itemdetails
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import maes.tech.intentanim.CustomIntent
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.Locale


class AllProductScreen : AppCompatActivity() {

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>
    lateinit var binding: ActivityAllProductScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_product_screen)
        binding.executePendingBindings()


        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.cart.setOnClickListener {
            startActivity(Intent(this@AllProductScreen, Cart::class.java))
            CustomIntent.customType(this@AllProductScreen, "fadein-to-fadeout")
        }

        val databaseReference = FirebaseDatabase.getInstance().reference.child("Items")


        val options = FirebaseRecyclerOptions.Builder<itemdetails>()
            .setQuery(databaseReference) { snapshot ->
                itemdetails(
                    snapshot.child("image").getValue(String::class.java),
                    snapshot.child("name").getValue(String::class.java),
                    snapshot.child("rating").getValue(String::class.java),
                    snapshot.child("category").getValue(String::class.java),
                    snapshot.child("price").getValue(String::class.java),
                    snapshot.key,
                    snapshot.child("product").getValue(String::class.java)
                )
            }.build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options) {
                override fun onBindViewHolder(
                    holder: ItemViewHolder,
                    @SuppressLint("RecyclerView") position: Int,
                    model: itemdetails
                ) {
                    if (position == 1) {
                        binding.progresslayout.visibility = View.GONE
                    }
                    holder.name.text = model.name
                    val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                    holder.price.text = format.format(BigDecimal(model.price))
                    Glide.with(applicationContext).load(model.image).into(holder.image)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(this@AllProductScreen, ItemDetail::class.java)
                        intent.putExtra("id", firebaseRecyclerAdapter.getItem(position).id)
                        startActivity(intent)
                        CustomIntent.customType(this@AllProductScreen, "left-to-right")
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                    val view1 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.tshirtiteme, parent, false)
                    return ItemViewHolder(view1)
                }


                override fun onViewAttachedToWindow(holder: ItemViewHolder) {
                    super.onViewAttachedToWindow(holder)
                    binding.secondaryProgress.visibility = View.GONE
                }


            }


        binding.allrecyview.layoutManager =
            GridLayoutManager(this, 2)
        binding.allrecyview.adapter = firebaseRecyclerAdapter

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.secondaryProgress.visibility = View.VISIBLE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val dataRef =
                    FirebaseDatabase.getInstance().reference.child("Items").orderByChild("name")
                        .startAt(binding.search.text.toString())
                        .endAt(binding.search.text.toString() + "~")

                dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.noProducts.visibility = View.GONE
                        } else {
                            binding.secondaryProgress.visibility = View.GONE
                            binding.noProducts.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

                val newOptions = FirebaseRecyclerOptions.Builder<itemdetails>()
                    .setQuery(dataRef) { snapshot ->
                        itemdetails(
                            snapshot.child("image").getValue(String::class.java),
                            snapshot.child("name").getValue(String::class.java),
                            snapshot.child("rating").getValue(String::class.java),
                            snapshot.child("category").getValue(String::class.java),
                            snapshot.child("price").getValue(String::class.java),
                            snapshot.key,
                            snapshot.child("product").getValue(String::class.java)
                        )
                    }.build()

                firebaseRecyclerAdapter.updateOptions(newOptions)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.search.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val dataRef =
                    FirebaseDatabase.getInstance().reference.child("Items").orderByChild("name")
                        .startAt(binding.search.text.toString())

                val newOptions = FirebaseRecyclerOptions.Builder<itemdetails>()
                    .setQuery(dataRef) { snapshot ->
                        itemdetails(
                            snapshot.child("image").getValue(String::class.java),
                            snapshot.child("name").getValue(String::class.java),
                            snapshot.child("rating").getValue(String::class.java),
                            snapshot.child("category").getValue(String::class.java),
                            snapshot.child("price").getValue(String::class.java),
                            snapshot.key,
                            snapshot.child("product").getValue(String::class.java)
                        )
                    }.build()

                firebaseRecyclerAdapter.updateOptions(newOptions)

                return@OnEditorActionListener true
            }
            false
        })
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var price: TextView
        var image: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            price = itemView.findViewById(R.id.price)
            image = itemView.findViewById(R.id.image)
        }
    }


    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening()
        }
    }
}