package com.example.login.fragments

import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.login.R
import android.content.Intent
import android.graphics.Color
import com.example.login.Cart
import maes.tech.intentanim.CustomIntent
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.bumptech.glide.Glide
import com.example.login.ItemDetail
import android.os.Looper
import com.example.login.Home
import android.graphics.Typeface
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login.databinding.FragmentCategoriesBinding
import com.google.firebase.database.*
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class Categories : Fragment() {

    lateinit var its: ArrayList<String>

    lateinit var binding: FragmentCategoriesBinding
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_categories, container, false)
        binding.executePendingBindings()
        // Inflate the layout for this fragment

        binding.cart.setOnClickListener {
            startActivity(Intent(activity, Cart::class.java))
            CustomIntent.customType(activity, "bottom-to-up")
        }

        FirebaseDatabase.getInstance().reference.child("Profiles")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Cart")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.cartcount.text = "" + snapshot.childrenCount
                    } else {
                        binding.cartcount.text = "0"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        val query: Query = FirebaseDatabase.getInstance().reference.child("Items")
        val options = FirebaseRecyclerOptions.Builder<itemdetails>()
            .setQuery(query) { snapshot ->
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
                    position: Int,
                    model: itemdetails
                ) {
                    /*if(position%2==0)
                    {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.lightgrey));
                    }*/
                    holder.product.text = model.product
                    holder.name.text = model.name
                    val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                    holder.price.text = format.format(BigDecimal(model.price))
                    //    holder.rating.setText(model.getRating());
                    Glide.with(activity!!).load(model.image).into(holder.image)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(activity, ItemDetail::class.java)
                        intent.putExtra("id", firebaseRecyclerAdapter.getItem(position).id)
                        startActivity(intent)
                        CustomIntent.customType(activity, "left-to-right")

                    }

                    if (position == 0) {
                        binding.progressbar.visibility = View.GONE
                        val animation =
                            AnimationUtils.loadLayoutAnimation(activity, R.anim.layoutanim)
                        binding.productsrecyview.layoutAnimation = animation
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                    val view1 = LayoutInflater.from(parent.context)
                        .inflate(R.layout.productitem, parent, false)
                    return ItemViewHolder(view1)
                }
            }
        binding.productsrecyview.layoutManager = LinearLayoutManager(activity)
        binding.listrecyview.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        FirebaseDatabase.getInstance().reference.child("Categories")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    its = ArrayList()
                    its.add("All")
                    for (dataSnapshot in snapshot.children) {
                        if (!its.contains(
                                dataSnapshot.child("name").getValue(String::class.java)
                            )
                        ) {
                            its.add(dataSnapshot.child("name").getValue(String::class.java)!!)
                        }
                    }
                    binding.listrecyview.adapter = SubAdapter((its))
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ binding.productsrecyview.adapter = firebaseRecyclerAdapter }, 100)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (firebaseRecyclerAdapter != null) firebaseRecyclerAdapter.stopListening()
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView

        init {
            textView = itemView.findViewById(R.id.listitem)
        }
    }

    inner class SubAdapter(var items: ArrayList<String>) :
        RecyclerView.Adapter<SubCategoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
            val view1 =
                LayoutInflater.from(parent.context).inflate(R.layout.listitem, parent, false)
            return SubCategoryViewHolder(view1)
        }

        override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
            holder.name.text = items[position]
            val home = activity as Home?
            if (home!!.cat == items[position]) {
                holder.name.textSize = 18f
                holder.name.setTypeface(holder.name.typeface, Typeface.BOLD)
                holder.name.setTextColor(Color.BLACK)
                home.cat = ""
                val query =
                    FirebaseDatabase.getInstance().reference.child("Items").orderByChild("category")
                        .equalTo(holder.name.text.toString())
                val options = FirebaseRecyclerOptions.Builder<itemdetails>()
                    .setQuery(query) { snapshot ->
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
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ firebaseRecyclerAdapter.updateOptions(options) }, 100)
            }
            if (position == 0 && home.cat == "") {
                holder.name.textSize = 18f
                holder.name.setTypeface(holder.name.typeface, Typeface.BOLD)
                holder.name.setTextColor(Color.BLACK)
            }
            holder.name.setOnClickListener {
                for (i in items.indices) {
                    if (position == i) {
                        lateinit var textView: TextView
                        val view = binding.listrecyview.layoutManager!!.findViewByPosition(i)
                        if (view != null)
                            textView = view.findViewById(R.id.listitem)
                        if (textView != null) {
                            textView.textSize = 18f
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                            textView.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
                            if (textView.text.toString() == "All") {
                                binding.progressbar.visibility = View.VISIBLE
                                val query: Query =
                                    FirebaseDatabase.getInstance().reference.child("Items")
                                val options = FirebaseRecyclerOptions.Builder<itemdetails>()
                                    .setQuery(query) { snapshot ->
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
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed(
                                    { firebaseRecyclerAdapter.updateOptions(options) },
                                    100
                                )
                            } else {
                                binding.progressbar.visibility = View.VISIBLE
                                val query = FirebaseDatabase.getInstance().reference.child("Items")
                                    .orderByChild("category").equalTo(textView.text.toString())
                                val options = FirebaseRecyclerOptions.Builder<itemdetails>()
                                    .setQuery(query) { snapshot ->
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
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed(
                                    { firebaseRecyclerAdapter.updateOptions(options) },
                                    100
                                )
                            }
                        }
                    } else {
                        lateinit var textView: TextView
                        val view = binding.listrecyview.layoutManager!!.findViewByPosition(i)
                        if (view != null) textView = view.findViewById(R.id.listitem)
                        if (textView != null) {
                            textView.textSize = 14f
                            textView.setTextColor(ContextCompat.getColor(activity!!, R.color.grey))
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView

        init {
            name = itemView.findViewById(R.id.listitem)
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var price: TextView
        var product: TextView
        var image: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            product = itemView.findViewById(R.id.product)
            price = itemView.findViewById(R.id.price)
            image = itemView.findViewById(R.id.image)
        }
    }
}