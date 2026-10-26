package com.asbots.certifai.ui.roadmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asbots.certifai.R
import com.asbots.certifai.data.models.MindmapNode
import com.asbots.certifai.databinding.ItemMindmapNodeBinding

class MindmapNodeAdapter(
    private val nodes: List<MindmapNode>,
    private val onNodeClick: (MindmapNode) -> Unit
) : RecyclerView.Adapter<MindmapNodeAdapter.MindmapViewHolder>() {

    class MindmapViewHolder(val binding: ItemMindmapNodeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MindmapViewHolder {
        val binding = ItemMindmapNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MindmapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MindmapViewHolder, position: Int) {
        val node = nodes[position]
        val context = holder.itemView.context

        holder.binding.tvNodeTitle.text = node.title
        holder.binding.tvNodeSubtitle.text = node.subtitle
        holder.binding.tvNodeBadge.text = node.badgeText
        holder.binding.tvNodeDuration.text = node.durationText

        if (node.isFree) {
            holder.binding.tvNodeBadge.text = "FREE"
            holder.binding.tvNodeBadge.setTextColor(ContextCompat.getColor(context, R.color.success_green))
            holder.binding.tvNodeBadge.setBackgroundColor(0x1210B981)
        } else {
            holder.binding.tvNodeBadge.text = node.badgeText
            holder.binding.tvNodeBadge.setTextColor(ContextCompat.getColor(context, R.color.electric_blue))
            holder.binding.tvNodeBadge.setBackgroundColor(0x122563EB)
        }

        holder.itemView.setOnClickListener {
            onNodeClick(node)
        }
    }

    override fun getItemCount(): Int = nodes.size
}
