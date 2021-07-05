package com.fchwpo.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.fchwpo.mymemory.models.BoardSize
import com.fchwpo.mymemory.models.MemoryCard

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val setOnViewClick: SetOnViewClick
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object {
        // use to define static constants
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface SetOnViewClick {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - 2 * MARGIN_SIZE
        val cardHeight = parent.height / boardSize.getHeight() - 2 * MARGIN_SIZE
        val cardSizeLength = cardHeight.coerceAtMost(cardWidth)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams =
            view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSizeLength
        layoutParams.height = cardSizeLength
        layoutParams.setMargins(MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun getItemCount() = boardSize.numOfCards

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memoryCard: MemoryCard = cards[position]
            val image = if(memoryCard.isFlipped) memoryCard.id else R.drawable.ic_launcher_background
            imageButton.setImageResource(image)
            imageButton.alpha = if(memoryCard.isMatched) 0.4f else 1f
            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on ViewHolder/ CardView at position $position")
                setOnViewClick.onClick(position)
            }
        }

    }
}
