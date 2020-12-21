package com.adam.lib.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By Adam on 2020/11/27
 */
class MyItemDecoration : RecyclerView.ItemDecoration {
    private var space: Int = 0
    private var mPaint: Paint? = null

    constructor(space: Int) {
        this.space = space
    }

    constructor(space: Int, dividerColor: Int) {
        this.space = space
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = dividerColor
        mPaint!!.style = Paint.Style.FILL
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        outRect.top = space
    }

    //绘制分割线
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mPaint != null) {
            drawHorizontal(c, parent)
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.getPaddingLeft()//获取分割线的左边距，即RecyclerView的padding值
        val right = parent.getMeasuredWidth() - parent.getPaddingRight()//分割线右边距
        val childSize = parent.getChildCount()
        //遍历所有item view，为它们的下方绘制分割线
        for (i in 0 until childSize - 1) {
            val child = parent.getChildAt(i)
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams
            val top = child.getBottom() + layoutParams.bottomMargin
            val bottom = top + space
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
            }
        }
    }
}
