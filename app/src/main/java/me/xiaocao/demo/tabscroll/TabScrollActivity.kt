package me.xiaocao.demo.tabscroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_tab_scroll.*
import me.xiaocao.demo.R
import me.xiaocao.demo.base.BaseActivity
import me.xiaocao.demo.tabscroll.binder.*

class TabScrollActivity : BaseActivity() {
    private val mItems = ArrayList<ItemBean>()
    private val mAdapter = MultiTypeAdapter()

    private var scrollToPosition: Int = 0
    private var canScroll: Boolean = false
    //记录上一次位置，防止在同一内容块里滑动 重复定位到tablayout
    private var lastPos: Int = 0
    //判读是否是recyclerView主动引起的滑动，true- 是，false- 否，由tablayout引起的
    private var isRecyclerScroll: Boolean = false
    private val mTitles = arrayOf("推荐套餐", "美食", "美景", "住宿", "评价")

    companion object {
        const val COUNT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_scroll)
        initData()
        initView()
    }

    private fun initView() {
        mAdapter.apply {
            items = mItems
            register(ItemBean::class.java)
                    .to(TitleViewBinder(), RecommendViewBinder(), FoodViewBinder(), ScapeViewBinder(), RoomViewBinder(), EvaluationViewBinder())
                    .withKotlinClassLinker { _, item ->
                        when (item.viewType) {
                            ItemBean.TYPE_TITLE -> TitleViewBinder::class
                            ItemBean.TYPE_RECOMMEND -> RecommendViewBinder::class
                            ItemBean.TYPE_FOOD -> FoodViewBinder::class
                            ItemBean.TYPE_VIEW -> ScapeViewBinder::class
                            ItemBean.TYPE_ROOM -> RoomViewBinder::class
                            ItemBean.TYPE_EVALUATION -> EvaluationViewBinder::class
                            else -> TitleViewBinder::class
                        }
                    }
        }
        val mLayoutManager = GridLayoutManager(this, COUNT)
        mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemBean = mItems[position]
                return when (itemBean.viewType) {
                    ItemBean.TYPE_TITLE, ItemBean.TYPE_RECOMMEND -> COUNT
                    else -> 1
                }
            }
        }

        for (i in 0 until mTitles.size) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]))
        }
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var pos = 0
                mItems.forEach { item ->
                    if (item.title == tab.text.toString()) {
                        pos = mItems.indexOf(item)
                    }
                }
                isRecyclerScroll = false
                moveToPosition(mLayoutManager, mRecyclerView, pos)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        mRecyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (canScroll) {
                        canScroll = false
                        moveToPosition(mLayoutManager, recyclerView, scrollToPosition)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (isRecyclerScroll) {
                        val item = mItems[mLayoutManager.findFirstVisibleItemPosition()]
                        if (item.viewType == ItemBean.TYPE_TITLE) {
                            val position = mTitles.indexOf(item.title)
                            if (lastPos != position) {
                                mTabLayout.setScrollPosition(position, 0f, true)
                            }
                            lastPos = position
                        }
                    }
                }
            })
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isRecyclerScroll = true
                }
                false
            }
        }
    }

    private fun initData() {
        mItems.add(ItemBean("推荐套餐", ItemBean.TYPE_TITLE))
        //推荐套餐
        for (i in 1..3) {
            mItems.add(ItemBean("", ItemBean.TYPE_RECOMMEND))
        }
        mItems.add(ItemBean("美食", ItemBean.TYPE_TITLE))
        //美食
        for (i in 1..6) {
            mItems.add(ItemBean("", ItemBean.TYPE_FOOD))
        }
        mItems.add(ItemBean("美景", ItemBean.TYPE_TITLE))
        //美景
        for (i in 1..3) {
            mItems.add(ItemBean("", ItemBean.TYPE_VIEW))
        }
        mItems.add(ItemBean("住宿", ItemBean.TYPE_TITLE))
        //住宿
        for (i in 1..6) {
            mItems.add(ItemBean("", ItemBean.TYPE_ROOM))
        }
        mItems.add(ItemBean("评价", ItemBean.TYPE_TITLE))
        //评价
        for (i in 1..20) {
            mItems.add(ItemBean("", ItemBean.TYPE_EVALUATION))
        }
    }

    fun moveToPosition(manager: GridLayoutManager, mRecyclerView: RecyclerView, position: Int) {
        // 第一个可见的view的位置
        val firstItem = manager.findFirstVisibleItemPosition()
        // 最后一个可见的view的位置
        val lastItem = manager.findLastVisibleItemPosition()
        when {
            position <= firstItem -> // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
                mRecyclerView.smoothScrollToPosition(position)
            position <= lastItem -> {
                // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
                val top = mRecyclerView.getChildAt(position - firstItem).top
                mRecyclerView.smoothScrollBy(0, top)
            }
            else -> {
                // 如果要跳转的位置在lastItem 之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
                // 再通过onScrollStateChanged控制再次调用当前moveToPosition方法，执行上一个判断中的方法
                mRecyclerView.smoothScrollToPosition(position)
                scrollToPosition = position
                canScroll = true
            }
        }
    }
}
