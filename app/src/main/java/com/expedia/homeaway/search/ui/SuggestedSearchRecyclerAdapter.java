package com.expedia.homeaway.search.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.expedia.homeaway.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SuggestedSearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_DATA = 1;

    private List<String> suggestedSearchResults;
    private ViewGroup searchHeader;
    private SuggestedSearchOnClickListener suggestedSearchItemClickListener;
    private View.OnTouchListener backgroundTouchListener;
    private RecyclerView recyclerView;
    private String searchInput;

    public SuggestedSearchRecyclerAdapter(RecyclerView recyclerView, List<String> suggestedSearchResults, ViewGroup searchHeader) {
        this.recyclerView = recyclerView;
        this.suggestedSearchResults = suggestedSearchResults;
        this.searchHeader = searchHeader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suggested_search_header, viewGroup, false);
                return new SuggestedSearchHeaderViewHolder(view);

            case ITEM_TYPE_DATA:
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suggested_search_item, viewGroup, false);
                final SuggestedSearchItemViewHolder suggestedSearchItemViewHolder = new SuggestedSearchItemViewHolder(view);

                suggestedSearchItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPos = suggestedSearchItemViewHolder.getAdapterPosition();
                        if (adapterPos != RecyclerView.NO_POSITION) {
                            suggestedSearchItemClickListener.onSuggestedSearchItemClick(suggestedSearchResults.get(adapterPos));
                        }
                    }
                });

                return suggestedSearchItemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SuggestedSearchItemViewHolder) {
            setText(viewHolder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return suggestedSearchResults.size();
    }

    public void setClickListeners(SuggestedSearchOnClickListener listener, View.OnTouchListener backgroundTouchListener) {
        this.suggestedSearchItemClickListener = listener;
        this.backgroundTouchListener = backgroundTouchListener;
    }

    public void clearSearchResults() {
        suggestedSearchResults.clear();
        notifyDataSetChanged();
    }

    public boolean isSearchResultsEmpty() {
        return suggestedSearchResults.isEmpty();
    }

    public void setSearchResults(List<String> newSearchResults, String newSearchInput) {

        if (newSearchInput != null && searchInput != null &&
                newSearchInput.contains(searchInput) &&
                newSearchResults.size() < suggestedSearchResults.size()) {

            this.searchInput = newSearchInput;

            int i;
            do {
                for (i = 0; i < suggestedSearchResults.size(); i++) {
                    String searchTerm = suggestedSearchResults.get(i);
                    if (!newSearchResults.contains(searchTerm)) {
                        suggestedSearchResults.remove(i);
                        notifyItemRemoved(i);
                        break;
                    } else {
                        setText(recyclerView.findViewHolderForAdapterPosition(i));
                    }
                }
            } while (i < suggestedSearchResults.size());

            for (i = 0; i < newSearchResults.size(); i++) {
                if (!suggestedSearchResults.contains(newSearchResults.get(i))) {
                    suggestedSearchResults.add(newSearchResults.get(i));
                    notifyItemInserted(suggestedSearchResults.size() - 1);
                }
            }

        } else {

            this.searchInput = newSearchInput;

            suggestedSearchResults.clear();
            if (newSearchResults != null) {
                suggestedSearchResults.addAll(newSearchResults);
            }
            notifyDataSetChanged();
        }
    }


    private void setText(RecyclerView.ViewHolder viewHolder) {

        if (viewHolder == null) {
            return;
        }

        int adapterPos = viewHolder.getAdapterPosition();
        if (adapterPos != RecyclerView.NO_POSITION) {
            String suggestedSearch = suggestedSearchResults.get(adapterPos);
            ((SuggestedSearchItemViewHolder) viewHolder).suggestedSearchItemTextView.setText(suggestedSearch);
        }
    }

    public interface SuggestedSearchOnClickListener {
        void onSuggestedSearchItemClick(String item);
    }

    public static class SuggestedSearchItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.suggested_search_item_text)
        public TextView suggestedSearchItemTextView;

        public SuggestedSearchItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class SuggestedSearchHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.suggested_search_header_text)
        public TextView suggestedSearchHeaderText;

        public SuggestedSearchHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
