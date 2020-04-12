package com.example.playlisttest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {

    public interface dataListener{
    void onClickListener(String title,String name ,String path );
}
    List<Songs> songsList;
    final List<Songs> songsListAll;
    Context context;
    dataListener listener;


    public CustomAdapter(List<Songs> songsList, Context context,MusicListFragment fragment) {
        this.songsList = songsList;
        this.context = context;
        listener = fragment;
        songsListAll = songsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyViewHolder recycleView = new MyViewHolder(view);
        return recycleView;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tvTitle.setText(songsList.get(position).getArtist());
        holder.tvName.setText(songsList.get(position).getTitle());


        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Songs songs = songsList.get(position);
                String path = songs.getPath();


                String title = songs.getTitle();
                String name = songs.getArtist();
                listener.onClickListener(title,name,path);


            }
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvTitle;
        ImageView btnPlay;
        ImageView btnStop;
        ImageView tvPlay;
        TextView tvSongTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tv_song_title);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTitle = itemView.findViewById(R.id.tv_artist);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnStop = itemView.findViewById(R.id.btn_stop);
            tvPlay = itemView.findViewById(R.id.tv_play);
        }
    }
    @Override
    public Filter getFilter() {
    return songSFilter;
    }
    private Filter songSFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Songs> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filterList.addAll(songsListAll);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Songs item : songsList){
                    if (item.getTitle().toLowerCase().contains(filterPattern)){
                        filterList.add(item);
                    }
                }
            }

            FilterResults results =new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                songsList.clear();
                songsList.addAll((List) results.values);
                notifyDataSetChanged();
        }
    };
}
