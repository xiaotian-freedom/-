package com.storn.freedom.viewcollision

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storn.freedom.viewcollision.utils.GlideHelper
import com.storn.freedom.viewcollision.view.BubbleTickView

class MainActivity : AppCompatActivity() {

    //是否处于静止状态
    private var mIsIdle = false

    //是否处于滚动状态
    private var mIsScrolling = false

    //定时器
    private var mCountDownTimer: CountDownTimer? = null

    //动态图地址
    private val gifUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201604%2F26%2F20160426211944_WtKRk.gif&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627888527&t=9c71928a24e7c579b7c60b7127fb4cfa"

    //停靠屏幕边缘图地址
    private val dockUrl =
        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg3.redocn.com%2F20120620%2FRedocn_2012062014371020.jpg&refer=http%3A%2F%2Fimg3.redocn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1627887321&t=59881710789bc3a143337857f3d67d60"

    //长按图片后到屏幕边缘等待时间
    private val waitDuration = 3

    private lateinit var mBubbleView: BubbleTickView
    private lateinit var mRootLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListView()
        initBubbleView()
    }

    /**
     * 初始化列表相关
     */
    private fun initListView() {
        val listView = findViewById<RecyclerView>(R.id.recyclerView)
        listView.layoutManager = LinearLayoutManager(this)
        val list = mutableListOf<String>()
        for (i in 0..20) {
            list.add("永远保持${i}颗热血赤城的心")
        }
        listView.adapter = ListAdapter(this, list)
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (!mIsScrolling) {
                            mIsScrolling = true
                            showBubbleView()
                        }
                        if (mIsIdle) {
                            mIsIdle = false
                            startListTimer()
                        }
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        mIsScrolling = true
                        if (mIsIdle) {
                            mIsIdle = false
                            cancelListTimer()
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        mIsScrolling = false
                        if (!mIsIdle) {
                            mIsIdle = true
                            startListTimer()
                        }
                    }
                }
            }
        })
    }

    /**
     * 停止列表滑动后开始计时
     */
    private fun startListTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                }

                override fun onFinish() {
                    if (!mIsIdle) {
                        return
                    }
                    hideBubbleView()
                }
            }
        }
        mCountDownTimer?.start()
    }

    /**
     * 取消列表倒计时
     */
    private fun cancelListTimer() {
        mCountDownTimer?.cancel()
    }

    /**
     * 初始化bubbleView
     */
    private fun initBubbleView() {
        mBubbleView = findViewById(R.id.mBubbleView)
        mRootLayout = findViewById(R.id.mRootLayout)
        mBubbleView.setOnClickListener {
            mBubbleView.visibility = View.GONE
        }
        loadImage(gifUrl, dockUrl)
    }

    /**
     * 显示bubbleView
     */
    private fun showBubbleView() {
        if (mBubbleView.visibility == View.GONE || mBubbleView.isInMoving()) return
        mBubbleView.dockToSide()
    }

    /**
     * 隐藏bubbleView
     */
    private fun hideBubbleView() {
        if (mBubbleView.visibility == View.GONE || mBubbleView.isInMoving()) return
        mBubbleView.hideToSide()
    }

    /**
     * 加载图片
     * 一张动态图、一张静态图
     * 第一种动态图可更换为静态图
     * 第二张动态图可空
     */
    private fun loadImage(gifUrl: String, dockUrl: String) {
        if (TextUtils.isEmpty(gifUrl)) return
        GlideHelper.loadGif(this, gifUrl, mBubbleView, object : OnImageLoadListener {
            override fun onSuccess(resource: Drawable) {
                val origin = resource
                if (TextUtils.isEmpty(dockUrl)) {
                    showWithoutDockDrawable()
                    return
                }
                GlideHelper.downloadImage(this@MainActivity, dockUrl,
                    object : OnImageLoadListener {
                        override fun onSuccess(resource: Drawable) {
                            showWithDockDrawable(origin, resource)
                        }

                        override fun onFailed() {
                            showWithoutDockDrawable()
                        }
                    })
            }

            override fun onFailed() = Unit
        })
    }

    /**
     * 执行带停靠图片的逻辑
     */
    private fun showWithDockDrawable(
        origin: Drawable,
        dock: Drawable
    ) {
        mBubbleView.visibility = View.VISIBLE
        mBubbleView.post {
            mBubbleView.Builder()
                .setX(mBubbleView.x)
                .setY(mBubbleView.y)
                .setWidth(mBubbleView.width)
                .setHeight(mBubbleView.height)
                .setMaxWidth(mRootLayout.width)
                .setMaxHeight(mRootLayout.height)
                .setDozeDuration(waitDuration)
                .setShowDockDrawable(true)
                .setDockDrawable(dock)
                .setOriginDrawable(origin)
                .move()
        }
    }

    /**
     * 执行不需要停靠图片的逻辑
     */
    private fun showWithoutDockDrawable() {
        mBubbleView.visibility = View.VISIBLE
        mBubbleView.post {
            mBubbleView.Builder()
                .setX(mBubbleView.x)
                .setY(mBubbleView.y)
                .setWidth(mBubbleView.width)
                .setHeight(mBubbleView.height)
                .setMaxWidth(mRootLayout.width)
                .setMaxHeight(mRootLayout.height)
                .setDozeDuration(waitDuration)
                .setShowDockDrawable(false)
                .move()
        }
    }
}
