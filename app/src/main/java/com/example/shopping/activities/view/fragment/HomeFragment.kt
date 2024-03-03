package com.example.shopping.activities.view.fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.R
import com.example.shopping.activities.adapter.CategoryAdapter
import com.example.shopping.activities.adapter.ProductAdapter
import com.example.shopping.activities.adapter.ProductPagingAdapter
import com.example.shopping.activities.adapter.SliderPagerAdapter
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.view.activities.CartActivity
import com.example.shopping.activities.view.activities.ProductActivity
import com.example.shopping.activities.view.activities.ProductListActivity
import com.example.shopping.activities.view.activities.SearchActivity
import com.example.shopping.activities.view.auth.LoginActivity
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.activities.viewmodel.ProductViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3


@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var searchButton: ImageView
    private lateinit var cartButton: ImageView
    private lateinit var badge: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productPagingAdapter: ProductPagingAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var scrollView: NestedScrollView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private val viewModel by viewModels<ProductViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<DataViewModel>()
    private val imageList = mutableListOf(
        "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1537832816519-689ad163238b?q=80&w=2059&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    )
    private val categoryList = mutableListOf<Category>()
    private val productList = mutableListOf<Product>()

    private var isFetchingProducts = true
    private var offset: Int = 0

    @SuppressLint("LogNotTimber")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initialView(view)
        initialListener()

        return view
    }

    override fun onResume() {
        super.onResume()
        authViewModel.currentUser?.let { user ->
            dataViewModel.getProductsInCart(user.uid)
            dataViewModel.cartQuantity.observe(viewLifecycleOwner) { state ->
                if (state > 0) {
                    badge.text = state.toString()
                    badge.visibility = View.VISIBLE
                } else {
                    badge.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initialView(view: View) {
        searchButton = view.findViewById(R.id.searchButton)
        cartButton = view.findViewById(R.id.cart)
        progressBar = view.findViewById(R.id.progressBar)
        scrollView = view.findViewById(R.id.scrollView)
        viewPager = view.findViewById(R.id.slideBanner)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        productRecyclerView = view.findViewById(R.id.productRecyclerView)
        indicator = view.findViewById(R.id.indicator)
        badge = view.findViewById(R.id.badge)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initialListener() {
        ViewCompat.setNestedScrollingEnabled(productRecyclerView, false)

        //slider
        setupViewPagerSlider()

        searchButton.setOnClickListener {
            toSearchScreen()
        }

        //category
        viewModel.getCategory()
        viewModel.categories.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resources.Success -> {
                    val category = res.result
                    categoryList.clear()
                    categoryList.addAll(category)
                    showCategories()
                }

                is Resources.Failure -> {}

                is Resources.Loading -> {}
            }
        }

        //products
        viewModel.getProductAsPage(offset, 10)
        viewModel.products.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resources.Failure -> {
                    Log.d("TAG", "initialListener: ${state.e}")
                    progressBar.visibility = View.GONE
                }

                is Resources.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    val products = state.result
                    if (products.isEmpty()) {
                        isFetchingProducts = false
                        progressBar.visibility = View.GONE
                    } else {
                        productList.addAll(products)
                        Log.d("TAG", "initialListener: ${productList.get(0)}")
                        setupProductAdapter(productList)
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        cartButton.setOnClickListener {
            if (authViewModel.currentUser != null)
                toCartScreen()
            else toLoginScreen()
        }




        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val childHeight = v.getChildAt(0).height
            //check if scroll to bottom and isFetchingProducts is true
            if (scrollY + v.height >= childHeight && isFetchingProducts) {
                try {
                    offset += 10
                    viewModel.getProductAsPage(offset, 10)
                } catch (e: Exception) {
                    // Handle exception
                    requireActivity().toast(e.message ?: "Unknown error")
                }
                Log.i("TAG", "BOTTOM SCROLL")
            }
        })


    }

    private fun toLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.fade_out)
                .toBundle()
        )
    }

    private fun toCartScreen() {
        val intent = Intent(context, CartActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupProductAdapter(products: List<Product>) {
        productAdapter = ProductAdapter(products) { product ->
            toProductScreen(product)
        }
        val layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        productRecyclerView.layoutManager = layoutManager
        productRecyclerView.adapter = productAdapter
    }

    private fun toProductScreen(product: Product) {
        val intent = Intent(context, ProductActivity::class.java)
        intent.putExtra("product", product)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

    private fun showCategories() {
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            toSearchScreen(category)
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun toProductListScreen(category: Category) {
        val intent = Intent(context, ProductListActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

    private fun setupViewPagerSlider() {
        sliderPagerAdapter = SliderPagerAdapter(imageList)
        viewPager.adapter = sliderPagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val currentPageIndex = 0
        viewPager.currentItem = currentPageIndex

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})

        indicator.setViewPager(viewPager)

        sliderPagerAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)
    }

    private fun toSearchScreen(category: Category? = null) {
        val intent = Intent(context, SearchActivity::class.java)
        intent.putExtra("category", category)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }
}