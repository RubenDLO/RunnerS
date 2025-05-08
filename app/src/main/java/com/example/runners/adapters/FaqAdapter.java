package com.example.runners.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runners.R;
import com.example.runners.models.FaqItem;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FaqItem> faqList;

    public FaqAdapter(List<FaqItem> faqList) {
        this.faqList = faqList;
    }

    @Override
    public int getItemViewType(int position) {
        return faqList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == FaqItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_faq_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_faq, parent, false);
            return new FaqViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FaqItem item = faqList.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder header = (HeaderViewHolder) holder;

            // Establecemos el texto directamente, sin confiar en item.getQuestion()
            switch (position) {
                case 0:
                    header.tvHeader.setText("1. FUNCIONAMIENTO DE LA APP");
                    break;
                case 11:
                    header.tvHeader.setText("2. ANTES DE CORRER");
                    break;
                case 22:
                    header.tvHeader.setText("3. DESPUÉS DE CORRER");
                    break;
                case 33:
                    header.tvHeader.setText("4. DESCANSO ACTIVO");
                    break;
            }

        } else if (holder instanceof FaqViewHolder) {
            FaqViewHolder faqHolder = (FaqViewHolder) holder;
            faqHolder.tvQuestion.setText(item.getQuestion());
            faqHolder.tvAnswer.setText(item.getAnswer());
            faqHolder.tvAnswer.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);

            faqHolder.itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());
                notifyItemChanged(position);
            });
        }
    }
    @Override
    public int getItemCount() {
        return faqList.size();
    }

    static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvFaqHeader);
        }
    }
}
