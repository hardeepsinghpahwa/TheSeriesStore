package com.example.login.Fragments

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.login.R
import android.content.Intent
import com.example.login.Cart
import maes.tech.intentanim.CustomIntent
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.bumptech.glide.Glide
import com.example.login.ItemDetail
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.Home
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.login.dataclass.SliderItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smarteist.autoimageslider.SliderAnimations
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.smarteist.autoimageslider.SliderViewAdapter
import com.example.login.Fragments.HomeFragment.SliderAdapterExample.SliderAdapterVH
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.login.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import java.math.BigDecimal
import java.text.Format
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {
    lateinit var items: ArrayList<SliderItem>
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>
    var firebaseRecyclerAdapter3: FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>?=null
    lateinit var firebaseRecyclerAdapter2: FirebaseRecyclerAdapter<itemdetails, CategoryViewHolder>
    lateinit var names: ArrayList<String>
    lateinit var images: ArrayList<String>
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        binding.executePendingBindings()

        bottomNavigationView = activity!!.findViewById(R.id.bottom_navigation)

        binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.sliderView.setScrollTimeInSec(2)
        binding.sliderView.setAutoCycle(true)
        binding.sliderView.startAutoCycle()
        binding.tshirtsshimmer.startShimmer()
        binding.contentplatformsshimmer.startShimmer()
        binding.imageslidershimmer.startShimmer()


        FirebaseDatabase.getInstance().reference.child("Profiles").child(FirebaseAuth.getInstance().currentUser.uid).child("Cart").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.cartcount.text = "" + snapshot.childrenCount
                } else {
                    binding.cartcount.text = "0"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        binding.tshirtsseeall.setOnClickListener(View.OnClickListener { })
        binding.shopbyseriesseeall.setOnClickListener(View.OnClickListener { bottomNavigationView.setSelectedItemId(R.id.page_3) })
        binding.shopbycontentplatformsseeall.setOnClickListener(View.OnClickListener { bottomNavigationView.setSelectedItemId(R.id.page_2) })
        binding.cart.setOnClickListener {
            startActivity(Intent(activity, Cart::class.java))
            CustomIntent.customType(activity, "bottom-to-up")
        }
        FirebaseDatabase.getInstance().reference.child("SliderView").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items = ArrayList()
                for (snapshot1 in snapshot.children) {
                    items!!.add(SliderItem(snapshot1.child("image").getValue(String::class.java)))
                }
                binding.sliderView.setSliderAdapter(SliderAdapterExample(activity, items!!))
                binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
                binding.sliderView.setIndicatorVisibility(false)
                binding.sliderView.setScrollTimeInSec(5)
                binding.sliderView.startAutoCycle()
                YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(binding.sliderView)
                binding.tshirtsshimmer.setVisibility(View.GONE)
                binding.imageslidershimmer.setVisibility(View.GONE)
                binding.contentplatformsshimmer.setVisibility(View.GONE)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Items")
        val query = databaseReference.orderByChild("product").equalTo("Tshirts")
        val query2: Query = FirebaseDatabase.getInstance().reference.child("Categories")
        val options = FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query) { snapshot -> itemdetails(snapshot.child("image").getValue(String::class.java), snapshot.child("name").getValue(String::class.java), snapshot.child("rating").getValue(String::class.java), snapshot.child("category").getValue(String::class.java), snapshot.child("price").getValue(String::class.java), snapshot.key, snapshot.child("product").getValue(String::class.java)) }.build()
        val options2 = FirebaseRecyclerOptions.Builder<itemdetails>()
                .setQuery(query2) { snapshot -> itemdetails(snapshot.child("image").getValue(String::class.java), snapshot.child("name").getValue(String::class.java), snapshot.child("rating").getValue(String::class.java), snapshot.child("category").getValue(String::class.java), snapshot.child("price").getValue(String::class.java), snapshot.key, snapshot.child("product").getValue(String::class.java)) }.build()
        firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options) {
            override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int, model: itemdetails) {
                holder.name.text = model.name
                val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                holder.price.text = format.format(BigDecimal(model.price))
                Glide.with(activity!!).load(model.image).into(holder.image)
                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, ItemDetail::class.java)
                    intent.putExtra("id", firebaseRecyclerAdapter!!.getItem(position).id)
                    startActivity(intent)
                    CustomIntent.customType(activity, "left-to-right")
                }
            }

            override fun onViewAttachedToWindow(holder: ItemViewHolder) {
                super.onViewAttachedToWindow(holder)
                /*  YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(tshirts);*/
                val animation = AnimationUtils.loadLayoutAnimation(activity, R.anim.layoutanimltor)
                binding.tshirtsrecyview.setLayoutAnimation(animation)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view1 = LayoutInflater.from(parent.context).inflate(R.layout.tshirtiteme, parent, false)
                return ItemViewHolder(view1)
            }
        }
        firebaseRecyclerAdapter2 = object : FirebaseRecyclerAdapter<itemdetails, CategoryViewHolder>(options2) {
            override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int, model: itemdetails) {
                holder.name.text = model.name
                Glide.with(activity!!).load(model.image).into(holder.image)
                holder.itemView.setOnClickListener { /*View view = bottomNavigationView.findViewById(R.id.page_2);
                        view.performClick();*/
                    bottomNavigationView.setSelectedItemId(R.id.page_2)
                    val home = activity as Home?
                    home!!.posi = position
                    home.cat = firebaseRecyclerAdapter2!!.getItem(position).name!!
                }
            }

            override fun onViewAttachedToWindow(holder: CategoryViewHolder) {
                super.onViewAttachedToWindow(holder)

                /*     YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(contentplatforms);*/
                val animation = AnimationUtils.loadLayoutAnimation(activity, R.anim.layoutanim)
                binding.contentplatformsrecyview.setLayoutAnimation(animation)
            }

            override fun getItemViewType(position: Int): Int {
                return if (position % 2 == 0) {
                    1
                } else 2
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
                if (viewType == 1) {
                    val view1 = LayoutInflater.from(parent.context).inflate(R.layout.categoryitem, parent, false)
                    return CategoryViewHolder(view1)
                }
                val view1 = LayoutInflater.from(parent.context).inflate(R.layout.categoryitem2, parent, false)
                return CategoryViewHolder(view1)
            }
        }
        binding.tshirtsrecyview.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false))
        binding.tshirtsrecyview.setAdapter(firebaseRecyclerAdapter)
        YoYo.with(Techniques.FadeInDown)
                .duration(1000)
                .playOn(binding.tshirtsrecyview)
        binding.contentplatformsrecyview.setLayoutManager(LinearLayoutManager(activity))
        binding.contentplatformsrecyview.setAdapter(firebaseRecyclerAdapter2)
        FirebaseDatabase.getInstance().reference.child("SubCategories").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                names = ArrayList()
                images = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    if (!names.contains(dataSnapshot.child("name").getValue(String::class.java))) {
                        names.add(dataSnapshot.child("name").getValue(String::class.java)!!)
                    }
                    images.add(dataSnapshot.child("image").getValue(String::class.java)!!)
                }
                binding.shopbyseriesrecyview.setLayoutManager(LinearLayoutManager(activity))
                binding.shopbyseriesrecyview.setAdapter(SubAdapter(names, images))
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter!!.startListening()
        firebaseRecyclerAdapter2!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (firebaseRecyclerAdapter3 != null) firebaseRecyclerAdapter3!!.stopListening()
        if (firebaseRecyclerAdapter != null) firebaseRecyclerAdapter!!.stopListening()
        if (firebaseRecyclerAdapter2 != null) firebaseRecyclerAdapter2!!.stopListening()
    }

    inner class SubAdapter(var items: ArrayList<String>, var imgs: ArrayList<String>) : RecyclerView.Adapter<SubCategoryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
            val view1 = LayoutInflater.from(parent.context).inflate(R.layout.subcategoryitem, parent, false)
            return SubCategoryViewHolder(view1)
        }

        override fun onBindViewHolder(holder: SubCategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {
            holder.name.text = items[position]
            Glide.with(activity!!).load(imgs[position]).into(holder.imageView)
            holder.constraintLayout.setOnClickListener {
                bottomNavigationView!!.selectedItemId = R.id.page_3
                val home = activity as Home?
                home!!.posi = position
                home.cat = items[position]
            }
            val query2 = FirebaseDatabase.getInstance().reference.child("Items").orderByChild("subcategory").equalTo(items[position])
            val options2 = FirebaseRecyclerOptions.Builder<itemdetails>()
                    .setQuery(query2) { snapshot -> itemdetails(snapshot.child("image").getValue(String::class.java), snapshot.child("name").getValue(String::class.java), snapshot.child("rating").getValue(String::class.java), snapshot.child("category").getValue(String::class.java), snapshot.child("price").getValue(String::class.java), snapshot.key, snapshot.child("product").getValue(String::class.java)) }.build()
            firebaseRecyclerAdapter3 = object : FirebaseRecyclerAdapter<itemdetails, ItemViewHolder>(options2) {
                override fun onBindViewHolder(holder: ItemViewHolder, @SuppressLint("RecyclerView") position: Int, model: itemdetails) {
//                holder.category.setText(model.getCategory());
                    holder.name.text = model.name
                    val format: Format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                    holder.price.text = format.format(BigDecimal(model.price))
                    //    holder..setText(model.getRating());
                    Glide.with(activity!!).load(model.image).into(holder.image)
                    holder.itemView.setOnClickListener {
                        val intent = Intent(activity, ItemDetail::class.java)
                        intent.putExtra("id", firebaseRecyclerAdapter.getItem(position).id)
                        startActivity(intent)
                        CustomIntent.customType(activity, "left-to-right")
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                    val view1 = LayoutInflater.from(parent.context).inflate(R.layout.tshirtiteme, parent, false)
                    return ItemViewHolder(view1)
                }
            }
            holder.recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            holder.recyclerView.adapter = firebaseRecyclerAdapter3
            firebaseRecyclerAdapter3?.startListening()
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    inner class SliderAdapterExample(private val context: Context?, private val mSliderItems: List<SliderItem>) : SliderViewAdapter<SliderAdapterVH>() {
        override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
            val inflate = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
            return SliderAdapterVH(inflate)
        }

        override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
            val sliderItem = mSliderItems[position]
            Glide.with(viewHolder.itemView)
                    .load(sliderItem.image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(viewHolder.imageViewBackground)
            viewHolder.itemView.setOnClickListener { Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT).show() }
        }

        override fun getCount(): Int {
            //slider view count could be dynamic size
            return mSliderItems.size
        }

        inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
            var item: View
            var imageViewBackground: ImageView

            init {
                imageViewBackground = itemView.findViewById(R.id.image)
                item = itemView
            }
        }
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

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var image: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            image = itemView.findViewById(R.id.image)
        }
    }

    inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var recyclerView: RecyclerView
        var constraintLayout: ConstraintLayout
        var imageView: ImageView

        init {
            name = itemView.findViewById(R.id.name)
            recyclerView = itemView.findViewById(R.id.subcategoryrecyview)
            constraintLayout = itemView.findViewById(R.id.cons5)
            imageView = itemView.findViewById(R.id.image)
        }
    }
}