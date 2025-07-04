package com.example.restaurantapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.RestaurantAdapter
import com.example.restaurantapp.models.CuisineType
import com.example.restaurantapp.models.Restaurant
import com.example.restaurantapp.network.ApiClient
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyMessage: TextView
    private lateinit var restaurantAdapter: RestaurantAdapter

    // 필터 UI 요소들
    private lateinit var scrollViewFilters: ScrollView
    private lateinit var layoutFilterSummary: LinearLayout
    private lateinit var layoutSelectedFiltersContainer: LinearLayout
    private lateinit var layoutSelectedFilters: LinearLayout
    private lateinit var layoutCuisineTypes: LinearLayout
    private lateinit var layoutRatings: LinearLayout
    private lateinit var layoutSortOptions: LinearLayout
    private lateinit var btnToggleFilter: Button
    private lateinit var btnResetAll: Button
    private lateinit var tvFilterSummary: TextView
    private lateinit var tvResultCount: TextView

    private var isFilterVisible = false

    // 필터 데이터
    private var cuisineTypes: List<CuisineType> = emptyList()
    private val selectedFilters = mutableMapOf<String, String>()

    // 필터 옵션들 (가격대 제거)
    private val ratingOptions = listOf(
        "전체" to "",
        "4.0★↑" to "4.0",
        "4.5★↑" to "4.5"
    )

    private val sortOptions = listOf(
        "인기순" to "reviews-desc",
        "별점순" to "rating-desc",
        "이름순" to "name-asc"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupFilterUI()

        loadCuisineTypes()
        loadRestaurants()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewRestaurants)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)

        // 필터 UI 요소들
        scrollViewFilters = view.findViewById(R.id.scrollViewFilters)
        layoutFilterSummary = view.findViewById(R.id.layoutFilterSummary)
        layoutSelectedFiltersContainer = view.findViewById(R.id.layoutSelectedFiltersContainer)
        layoutSelectedFilters = view.findViewById(R.id.layoutSelectedFilters)
        layoutCuisineTypes = view.findViewById(R.id.layoutCuisineTypes)
        layoutRatings = view.findViewById(R.id.layoutRatings)
        layoutSortOptions = view.findViewById(R.id.layoutSortOptions)
        btnToggleFilter = view.findViewById(R.id.btnToggleFilter)
        btnResetAll = view.findViewById(R.id.btnResetAll)
        tvFilterSummary = view.findViewById(R.id.tvFilterSummary)
        tvResultCount = view.findViewById(R.id.tvResultCount)
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            Toast.makeText(requireContext(), "${restaurant.name} 클릭됨", Toast.LENGTH_SHORT).show()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = restaurantAdapter
        }
    }

    private fun setupFilterUI() {
        // 별점 필터 버튼들 생성
        setupFilterRow(layoutRatings, ratingOptions, "rating")

        // 정렬 필터 버튼들 생성
        setupFilterRow(layoutSortOptions, sortOptions, "sort")

        // 토글 버튼
        btnToggleFilter.setOnClickListener {
            toggleFilter()
        }

        // 전체 초기화 버튼
        btnResetAll.setOnClickListener {
            resetAllFilters()
        }

        // 기본 정렬 설정
        selectedFilters["sort"] = "reviews-desc"

        // 기본 상태에서 요약 표시
        updateFilterSummary()
    }

    private fun toggleFilter() {
        isFilterVisible = !isFilterVisible

        if (isFilterVisible) {
            scrollViewFilters.visibility = View.VISIBLE
            btnToggleFilter.text = "닫기"
        } else {
            scrollViewFilters.visibility = View.GONE
            btnToggleFilter.text = "필터"
        }
    }

    private fun setupFilterRow(parentLayout: LinearLayout, options: List<Pair<String, String>>, filterType: String) {
        var currentRow: LinearLayout? = null
        val maxButtonsPerRow = 4 // 한 줄에 4개씩
        var buttonCount = 0

        options.forEach { (label, value) ->
            if (buttonCount % maxButtonsPerRow == 0) {
                currentRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 6)
                    }
                }
                parentLayout.addView(currentRow)
            }

            val button = createFilterButton(label, filterType, value)

            // 기본 선택 설정
            if ((filterType == "rating" && value.isEmpty()) ||
                (filterType == "sort" && value == "reviews-desc")) {
                button.isSelected = true
            }

            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 0, if (buttonCount % maxButtonsPerRow < maxButtonsPerRow - 1) 6 else 0, 0)
            }
            button.layoutParams = layoutParams

            currentRow?.addView(button)
            buttonCount++
        }
    }

    private fun createFilterButton(label: String, filterType: String, value: String): Button {
        val button = Button(requireContext()).apply {
            text = label
            textSize = 11f
            setPadding(8, 8, 8, 8)
            minHeight = 0
            minimumHeight = 0
            setBackgroundResource(R.drawable.filter_button_background)
            setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.filter_button_text_color))

            setOnClickListener {
                handleFilterButtonClick(this, filterType, value, label)
            }
        }
        return button
    }

    private fun handleFilterButtonClick(button: Button, filterType: String, value: String, label: String) {
        // 같은 타입의 다른 버튼들 선택 해제
        when (filterType) {
            "cuisine" -> deselectButtons(layoutCuisineTypes)
            "rating" -> deselectButtons(layoutRatings)
            "sort" -> deselectButtons(layoutSortOptions)
        }

        // 현재 버튼 선택
        button.isSelected = true

        // 필터 상태 업데이트
        if (value.isEmpty()) {
            selectedFilters.remove(filterType)
        } else {
            selectedFilters[filterType] = value
        }

        // UI 업데이트
        updateSelectedFilterTags()
        updateFilterSummary()

        // 필터 적용
        applyFilters()
    }

    private fun updateFilterSummary() {
        val cuisineName = if (selectedFilters.containsKey("cuisine")) {
            cuisineTypes.find { it.cuisineTypeId.toString() == selectedFilters["cuisine"] }?.name ?: "선택됨"
        } else "전체"

        val ratingText = if (selectedFilters.containsKey("rating")) {
            "${selectedFilters["rating"]}★↑"
        } else ""

        val sortText = sortOptions.find { it.second == selectedFilters["sort"] }?.first ?: "인기순"

        val summaryParts = mutableListOf<String>()
        summaryParts.add(cuisineName)
        if (ratingText.isNotEmpty()) summaryParts.add(ratingText)
        summaryParts.add(sortText)

        tvFilterSummary.text = summaryParts.joinToString(" · ")
        layoutFilterSummary.visibility = View.VISIBLE
    }

    private fun updateResultCount(count: Int) {
        tvResultCount.text = "${count}개"
    }

    private fun deselectButtons(parentLayout: LinearLayout) {
        for (i in 0 until parentLayout.childCount) {
            val row = parentLayout.getChildAt(i) as LinearLayout
            for (j in 0 until row.childCount) {
                (row.getChildAt(j) as Button).isSelected = false
            }
        }
    }

    private fun updateSelectedFilterTags() {
        layoutSelectedFilters.removeAllViews()

        val activeFilters = selectedFilters.filter { it.value.isNotEmpty() }

        if (activeFilters.isEmpty()) {
            layoutSelectedFiltersContainer.visibility = View.GONE
            return
        }

        layoutSelectedFiltersContainer.visibility = View.VISIBLE

        var currentRow: LinearLayout? = null
        var tagCount = 0
        val maxTagsPerRow = 2

        activeFilters.forEach { (type, value) ->
            if (tagCount % maxTagsPerRow == 0) {
                currentRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 6)
                    }
                }
                layoutSelectedFilters.addView(currentRow)
            }

            val tagView = createFilterTag(getFilterDisplayName(type, value), type)
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 0, if (tagCount % maxTagsPerRow == 0) 6 else 0, 0)
            }
            tagView.layoutParams = layoutParams

            currentRow?.addView(tagView)
            tagCount++
        }
    }

    private fun createFilterTag(label: String, filterType: String): View {
        val tagContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(requireContext(), R.drawable.selected_filter_tag_background)
            setPadding(12, 6, 6, 6)
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val textView = TextView(requireContext()).apply {
            text = label
            textSize = 10f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val closeButton = Button(requireContext()).apply {
            text = "×"
            textSize = 12f
            setTextColor(Color.WHITE)
            background = null
            setPadding(4, 0, 4, 0)
            minHeight = 0
            minimumHeight = 0
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                removeFilter(filterType)
            }
        }

        tagContainer.addView(textView)
        tagContainer.addView(closeButton)

        return tagContainer
    }

    private fun getFilterDisplayName(type: String, value: String): String {
        return when (type) {
            "cuisine" -> cuisineTypes.find { it.cuisineTypeId.toString() == value }?.name ?: value
            "rating" -> "${value}★↑"
            "sort" -> sortOptions.find { it.second == value }?.first ?: value
            else -> value
        }
    }

    private fun removeFilter(filterType: String) {
        selectedFilters.remove(filterType)

        // 해당 필터 버튼들을 기본 상태로 되돌리기
        when (filterType) {
            "cuisine" -> selectDefaultButton(layoutCuisineTypes, "전체")
            "rating" -> selectDefaultButton(layoutRatings, "전체")
        }

        updateSelectedFilterTags()
        updateFilterSummary()
        applyFilters()
    }

    private fun selectDefaultButton(parentLayout: LinearLayout, defaultText: String) {
        for (i in 0 until parentLayout.childCount) {
            val row = parentLayout.getChildAt(i) as LinearLayout
            for (j in 0 until row.childCount) {
                val button = row.getChildAt(j) as Button
                button.isSelected = button.text == defaultText
            }
        }
    }

    private fun resetAllFilters() {
        selectedFilters.clear()
        selectedFilters["sort"] = "reviews-desc" // 기본 정렬 유지

        // 모든 필터 버튼들을 기본 상태로 되돌리기
        selectDefaultButton(layoutCuisineTypes, "전체")
        selectDefaultButton(layoutRatings, "전체")
        selectDefaultButton(layoutSortOptions, "인기순")

        updateSelectedFilterTags()
        updateFilterSummary()
        applyFilters()
    }

    private fun loadCuisineTypes() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getCuisineTypes()
                if (response.isSuccessful && response.body()?.success == true) {
                    cuisineTypes = response.body()?.data ?: emptyList()
                    setupCuisineTypeButtons()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "요리 종류 로딩 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCuisineTypeButtons() {
        layoutCuisineTypes.removeAllViews()

        val allOptions = mutableListOf<Pair<String, String>>()
        allOptions.add("전체" to "")
        allOptions.addAll(cuisineTypes.map { it.name to it.cuisineTypeId.toString() })

        setupFilterRow(layoutCuisineTypes, allOptions, "cuisine")
    }

    private fun applyFilters() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val cuisineTypeId = selectedFilters["cuisine"]?.toIntOrNull()
                val minRating = selectedFilters["rating"]?.toFloatOrNull()

                val sortValue = selectedFilters["sort"] ?: "reviews-desc"
                val sortParts = sortValue.split("-")
                val sortBy = sortParts[0]
                val sortOrder = if (sortParts.size > 1) sortParts[1] else "desc"

                val response = ApiClient.apiService.getFilteredRestaurants(
                    cuisineTypeId = cuisineTypeId,
                    minRating = minRating,
                    sortBy = sortBy,
                    sortOrder = sortOrder
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    val restaurants = response.body()?.data ?: emptyList()
                    displayRestaurants(restaurants)
                    updateResultCount(restaurants.size)
                } else {
                    val errorMessage = response.body()?.error ?: "필터링 실패"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "필터링 오류: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun loadRestaurants() {
        applyFilters() // 기본 필터로 로드
    }

    private fun displayRestaurants(restaurants: List<Restaurant>) {
        if (restaurants.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvEmptyMessage.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvEmptyMessage.visibility = View.GONE
            restaurantAdapter.updateRestaurants(restaurants)
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}