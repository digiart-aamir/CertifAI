package com.asbots.certifai.ui.roadmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asbots.certifai.R
import com.asbots.certifai.data.models.NodeStatus
import com.asbots.certifai.data.models.RoadmapNode
import com.asbots.certifai.databinding.ItemRoadmapNodeBinding

class RoadmapNodeAdapter(
    private val nodes: List<RoadmapNode>,
    private val onNodeClick: (RoadmapNode) -> Unit
) : RecyclerView.Adapter<RoadmapNodeAdapter.NodeViewHolder>() {

    class NodeViewHolder(val binding: ItemRoadmapNodeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = ItemRoadmapNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val node = nodes[position]
        val context = holder.itemView.context

        holder.binding.tvNodeStage.text = "STAGE ${node.stageNumber}"
        holder.binding.tvNodeTitle.text = node.title
        holder.binding.tvNodeDescription.text = node.description
        holder.binding.tvNodeReward.text = "🪙 +${node.asCoinsReward}"
        holder.binding.tvNodeLessonsCount.text = "${node.completedLessons} / ${node.totalLessons} Lessons"

        val percent = if (node.totalLessons > 0) (node.completedLessons * 100) / node.totalLessons else 0
        holder.binding.pbNodeProgress.progress = percent

        when (node.status) {
            NodeStatus.COMPLETED -> {
                holder.binding.tvNodeStatus.text = "✓ COMPLETED"
                holder.binding.tvNodeStatus.setTextColor(ContextCompat.getColor(context, R.color.success_green))
                holder.binding.tvNodeAction.text = "Review Node →"
            }
            NodeStatus.IN_PROGRESS -> {
                holder.binding.tvNodeStatus.text = "⚡ IN PROGRESS"
                holder.binding.tvNodeStatus.setTextColor(ContextCompat.getColor(context, R.color.electric_blue))
                holder.binding.tvNodeAction.text = "Continue Stage →"
            }
            NodeStatus.LOCKED -> {
                holder.binding.tvNodeStatus.text = "🔒 LOCKED"
                holder.binding.tvNodeStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary_light))
                holder.binding.tvNodeAction.text = "Unlock with Coins"
            }
        }

        holder.itemView.setOnClickListener {
            onNodeClick(node)
        }
    }

    override fun getItemCount(): Int = nodes.size
}
