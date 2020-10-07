package com.anksystems.fenomy.ui.adapter.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.withTranslation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class HeaderDecoration(parent: RecyclerView): RecyclerView.ItemDecoration() {

    private val header: MaterialTextView

    init {
        // inflate and measure the layout
        header = LayoutInflater.from(parent.context).inflate(R.layout.header_layout, parent, false) as MaterialTextView
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        header.measure(widthSpec, heightSpec)  //calculate measureHeight and measureWidth of header view
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        // layout basically just gets drawn on the reserved space on top of the first view
        val lp = header.layoutParams as ViewGroup.MarginLayoutParams

        header.layout(
            0 + lp.leftMargin,
            0,
            parent.right - lp.rightMargin,
            header.measuredHeight
        ) //calculate height and width of header view

        val adapter = (parent.adapter as? CustomAdapter<History>)
            ?: return

        //iterate over all visible views
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)


            val currHistory = adapter.items
                .getOrNull(position)
                ?: continue

            //your logic where draw header
            if (position == 0)
                drawHeader("1.01.1970", canvas, view)
            else {
                val prevHistory = adapter.items
                    .getOrNull(position - 1)
                    ?: continue

                if (currHistory.created != prevHistory.created)
                    drawHeader(currHistory.created, canvas, view)
            }

        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val lp = header.layoutParams as ViewGroup.MarginLayoutParams
            val position = parent.getChildAdapterPosition(view)

            if (position == 0) {
                //set offsets
                outRect.set(0, header.measuredHeight + lp.bottomMargin + lp.topMargin, 0, 0)
                continue
            }

            val adapter = (parent.adapter as? CustomAdapter<History>)
                ?: return

            val currHistory = adapter.items
                .getOrNull(i)
                ?: continue

            val prevHistory = adapter.items
                .getOrNull(i - 1)
                ?: continue
                
            //set offsets
            if (currHistory.created != prevHistory.created)
                outRect.set(0, header.measuredHeight + lp.bottomMargin + lp.topMargin, 0, 0)
            else //dont forget thatoutRect will be saving setted margins so if you have else block you can set outRect.setEmpty() so all will be 0
                outRect.set(0, 8.dpToPx(parent.context).toInt(), 0, 0)
        }
    }

    private fun drawHeader(text: String, canvas: Canvas, view: View) {
        val lp = header.layoutParams as ViewGroup.MarginLayoutParams
        header.text = text
        
        //dont forget to re measure if set text to TextView
        
        val widthSpec = View.MeasureSpec.makeMeasureSpec(header.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        header.measure(widthSpec, heightSpec)

        val height = header.measuredHeight
        val top = view.top - height - lp.bottomMargin

        canvas.withTranslation(
            x = lp.leftMargin.toFloat(), //move start point of drawing on leftMargin of our view
            y = top.toFloat()
        ) { header.draw(this) }
    }
}
