package com.asbots.certifai.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asbots.certifai.R

data class OnboardingStep(
    val badge: String,
    val title: String,
    val subtitle: String,
    val chips: List<String>,
    val imageRes: Int,
    val actionText: String = "Continue"
)

class OnboardingAdapter(private val steps: List<OnboardingStep>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val ivIllustration: ImageView = view.findViewById(R.id.ivIllustration)
        val chip1: TextView = view.findViewById(R.id.chip1)
        val chip2: TextView = view.findViewById(R.id.chip2)
        val chip3: TextView = view.findViewById(R.id.chip3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_page, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val step = steps[position]
        holder.tvBadge.text = step.badge
        holder.tvTitle.text = step.title
        holder.tvSubtitle.text = step.subtitle
        holder.ivIllustration.setImageResource(step.imageRes)
        if (step.chips.isNotEmpty()) holder.chip1.text = "✓ ${step.chips[0]}"
        if (step.chips.size > 1) holder.chip2.text = "✓ ${step.chips[1]}"
        if (step.chips.size > 2) holder.chip3.text = "✓ ${step.chips[2]}"
    }

    override fun getItemCount(): Int = steps.size
}
