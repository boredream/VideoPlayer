package com.boredream.videoplayer.video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;

public class VideoDetailCatalogAdapter extends RecyclerView.Adapter<VideoDetailCatalogAdapter.ViewHolder> {

    private Context context;
    private VideoDetailInfo video;
    private int curSeriesNumber = 1;
    private OnVideoControlListener onVideoControlListener;

    public VideoDetailCatalogAdapter(Context context, VideoDetailInfo video, OnVideoControlListener onVideoControlListener) {
        this.context = context;
        this.video = video;
        this.onVideoControlListener = onVideoControlListener;
    }

    @Override
    public int getItemCount() {
        return video.seriesNumberTotal;
    }

    public void update(int curSeriesNumber, VideoDetailInfo video) {
        this.curSeriesNumber = curSeriesNumber;
        this.video = video;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_index;

        public ViewHolder(final View itemView) {
            super(itemView);
            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    @Override
    public VideoDetailCatalogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.video_detail_item_catalog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoDetailCatalogAdapter.ViewHolder holder, int position) {
        final int seriesNumber = position + 1;
        holder.tv_index.setBackgroundResource(seriesNumber == curSeriesNumber
                ? R.drawable.oval_lightred_solid
                : R.drawable.oval_gray_solid);
        holder.tv_index.setTextColor(seriesNumber == curSeriesNumber ?
                0xFFFD7D6F : 0xFF606060);
        holder.tv_index.setText(String.valueOf(seriesNumber));

        holder.tv_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoControlListener != null) {
                    onVideoControlListener.onCatalogItemSelected(seriesNumber);
                }
            }
        });
    }
}
