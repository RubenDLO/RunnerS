package com.example.runners;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

    private final List<FAQModel> faqList;
    private Context context = null;

    public FAQAdapter(List<FAQModel> faqList, Context context) {
        this.faqList = faqList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FAQModel faq = faqList.get(position);

        holder.question.setText(faq.getQuestion());
        holder.answer.setText(faq.getAnswer());

        boolean isExpanded = faq.isExpanded();
        holder.answer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(context, R.color.white));
            border.setStroke(3, ContextCompat.getColor(context, R.color.orange));
            holder.container.setBackground(border);
            holder.question.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.answer.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
            holder.question.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.answer.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            faq.setExpanded(!faq.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.tvPregunta);
            answer = itemView.findViewById(R.id.tvRespuesta);
            container = itemView.findViewById(R.id.faqContainer);
        }
    }
}
