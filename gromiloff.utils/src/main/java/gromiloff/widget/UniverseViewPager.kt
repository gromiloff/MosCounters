package gromiloff.widget

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Draws a line for each page. The current page line is colored differently than the unselected page
 * lines
 **/
class UniverseViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs) {
    private val mViewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageSelected(position: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            frame.scrollBy(position, positionOffset)
        }
    }

    private var alreadyWork = false
    private val mAdapterChangeListener = object : DataSetObserver() {
        override fun onChanged() {
            if ((viewPager.adapter?.count ?: 0) < 2) {
                if (childCount == 2) removeView(frame)
            } else if (childCount == 1) {
                frame.setCount(viewPager.adapter?.count ?: 0)
                addView(frame, frameParams)
            }
        }
    }

    private val viewPager = ViewPager(context, attrs)
    private val frameParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
    private val frame = Frame(context, attrs, defStyle)

    init {
        addView(this.viewPager)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        this.viewPager.adapter?.also {
            if (visibility == View.VISIBLE) {
                // становится видимым контейнер
                if (this.visibility != View.VISIBLE) {
                    // эта вью группа невидима
                    if (this.alreadyWork) {
                        alreadyWork = false
                        try {
                            it.unregisterDataSetObserver(this.mAdapterChangeListener)
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                        this.viewPager.removeOnPageChangeListener(this.mViewPagerListener)
                    }
                } else if (!alreadyWork) {
                    // эта вью группа видима и слушателя еще не назначено
                    alreadyWork = true
                    it.registerDataSetObserver(this.mAdapterChangeListener)
                    this.viewPager.addOnPageChangeListener(this.mViewPagerListener)
                }
            } else {
                // становится невидимым контейнер
                alreadyWork = false
                try {
                    it.unregisterDataSetObserver(this.mAdapterChangeListener)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                this.viewPager.removeOnPageChangeListener(this.mViewPagerListener)
            }
        }
    }

    fun setAdapter(a: PagerAdapter?) {
        if (this.alreadyWork) {
            try {
                this.viewPager.adapter?.unregisterDataSetObserver(this.mAdapterChangeListener)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        this.viewPager.adapter = a
        this.viewPager.adapter?.registerDataSetObserver(this.mAdapterChangeListener)
        this.viewPager.addOnPageChangeListener(this.mViewPagerListener)
        this.alreadyWork = true
    }

    fun getAdapter() = this.viewPager.adapter

    fun changeCurrentPage(page: Int) {
        this.viewPager.currentItem = page
    }

    fun addViewPagerListener(listener: ViewPager.OnPageChangeListener) {
        this.viewPager.addOnPageChangeListener(listener)
    }

    fun removeViewPagerListener(listener: ViewPager.OnPageChangeListener) {
        this.viewPager.removeOnPageChangeListener(listener)
    }

    fun changeColors(indicatorPositionColor: Int = 0, indicatorBackgroundColor: Int = 0) {
        this.frame.changeColors(indicatorPositionColor, indicatorBackgroundColor)
    }

    fun margins(left: Int, right: Int, bottom: Int, height: Int) {
        this.frameParams.addRule(ALIGN_PARENT_BOTTOM)
        this.frameParams.leftMargin = left
        this.frameParams.rightMargin = right
        this.frameParams.bottomMargin = bottom
        this.frameParams.height = height
    }

    class Frame @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @Suppress("UNUSED_PARAMETER") defStyle: Int = 0) : View(context, attrs) {
        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var indicatorPositionColor: Int = 0
        private var indicatorBackgroundColor: Int = 0

        private var selectedIndex = 0
        private var count = 0
        private var pageWidth = 0
        private var addScrollingValue = 0f

        var enableAutoFade = true
        var fadeFrameMs = 75L
        var fadeFullFrame = 1000L

        private val mFadeRunnable = object : Runnable {
            override fun run() {
                if (!enableAutoFade) return

                alpha = Math.max(alpha - 1f / (fadeFullFrame / fadeFrameMs), 0f)
                invalidate()
                if (alpha > 0) {
                    postDelayed(this, fadeFrameMs)
                }
            }
        }

        fun setCount(count: Int) {
            this.count = count
            this.pageWidth = 0
            invalidate()
        }

        fun scrollBy(position: Int, value: Float) {
            this.selectedIndex = position
            this.addScrollingValue = value * this.pageWidth
            if (enableAutoFade) {
                alpha = 1f
                removeCallbacks(this.mFadeRunnable)
                postDelayed(this.mFadeRunnable, this.fadeFullFrame)
            }
            invalidate()
        }

        fun changeColors(indicatorPositionColor: Int = 0, indicatorBackgroundColor: Int = 0) {
            if (indicatorPositionColor != 0) this.indicatorPositionColor = indicatorPositionColor
            if (indicatorBackgroundColor != 0) this.indicatorBackgroundColor = indicatorBackgroundColor
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            if (this.pageWidth == 0 && this.count > 0 && width > 0) {
                this.pageWidth = width / this.count
                this.mPaint.strokeWidth = height.toFloat()
            }

            canvas?.also {
                this.mPaint.color = this.indicatorBackgroundColor
                canvas.drawLine(0f, this.height.toFloat() / 2, width.toFloat(), this.height.toFloat() / 2, this.mPaint)

                val left = (pageWidth * this.selectedIndex).toFloat() + this.addScrollingValue
                this.mPaint.color = this.indicatorPositionColor
                canvas.drawLine(left, this.height.toFloat() / 2, left + pageWidth, this.height.toFloat() / 2, this.mPaint)
            }
        }
    }
}