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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopping.R
import com.example.shopping.activities.adapter.CategoryAdapter
import com.example.shopping.activities.adapter.ProductAdapter
import com.example.shopping.activities.adapter.SliderPagerAdapter
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.view.ProductListActivity
import com.example.shopping.activities.view.SearchActivity
import com.example.shopping.activities.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class HomeFragment : Fragment(), SliderPagerAdapter.OnItemClickListener {

    private lateinit var searchButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var sliderPagerAdapter: SliderPagerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var productRecyclerView: RecyclerView


    private val viewModel by viewModels<ProductViewModel>()
    private val imageList = mutableListOf<String>()
    private val categoryList = mutableListOf<Category>()
    private val productList = mutableListOf<Product>()

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

    private fun initialView(view: View) {
        searchButton = view.findViewById(R.id.searchButton)
        progressBar = view.findViewById(R.id.progressBar)
        viewPager = view.findViewById(R.id.slideBanner)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        productRecyclerView = view.findViewById(R.id.productRecyclerView)
        indicator = view.findViewById(R.id.indicator)
    }

    private fun initialListener() {
        searchButton.setOnClickListener {
            toSearchScreen()
        }

        viewModel.getProductAsPage(1, 10)

        viewModel.getCategory()

        viewModel.product.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resources.Success -> {
                    val products = res.result

                    for (product in products) {
                        Log.d("TAG", "onCreate: ${product.title}")
                        imageList.add(product.images[0])
                        productList.add(product)
                        progressBar.visibility = View.GONE
                    }


                    setupViewPagerSlider()

                }

                is Resources.Failure -> {
                    Log.d("TAG", "onCreate: ${res.e}")
                    progressBar.visibility = View.GONE
                }

                Resources.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resources.Success -> {
                    val category = res.result
                    categoryList.clear()
                    for (cat in category) {
                        Log.d("TAG", "onCreate: ${cat.name}")
                        categoryList.add(cat)
                    }

                    setupProductAdapter()
                    showCategories()
                }

                is Resources.Failure -> {}

                is Resources.Loading -> {}
            }
        }

    }

    private fun setupProductAdapter() {
        productAdapter = ProductAdapter(productList) { product ->
            toProductScreen(product)
        }

        val layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        productRecyclerView.layoutManager = layoutManager
        productRecyclerView.adapter = productAdapter

    }

    private fun toProductScreen(product: Product) {
        Toast.makeText(context, product.title, Toast.LENGTH_LONG).show()
    }

    private fun showCategories() {
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            toProductListScreen(category)
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.layoutManager = layoutManager
        categoryRecyclerView.adapter = categoryAdapter
    }

    private fun toProductListScreen(category: Category) {
        val intent = Intent(context, ProductListActivity::class.java)
        intent.putExtra("category", category)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

    override fun onItemClick(position: Int) {
        val itemClicked = imageList[position]
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

    private fun toSearchScreen() {
        val intent = Intent(context, SearchActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.fade_in,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

}