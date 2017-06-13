package com.boredream.videoplayer.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;

public class VideoCatalogDialog extends FrameLayout {

    private RecyclerView rv_catalog;
    private VideoCatalogAdapter adapter;

    public VideoCatalogDialog(Context context) {
        super(context);
        init();
    }

    public VideoCatalogDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoCatalogDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoCatalogDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_catalog_dialog, this);
        findViewById(R.id.v_mask).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
//        findViewById(R.id.video_catalog_content).setOnClickListener(v -> {/*nothing*/}); // FIXME: 2017/6/7
        rv_catalog = (RecyclerView) findViewById(R.id.rv_catalog);
    }

    public void show(VideoDetailInfo video) {
        if (adapter == null) {
            adapter = new VideoCatalogAdapter(getContext(), video, onVideoControlListener);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
            rv_catalog.setLayoutManager(layoutManager);
            rv_catalog.setAdapter(adapter);
        } else {
            adapter.updateVideo(video);
        }

        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }

    public static class VideoCatalogAdapter extends RecyclerView.Adapter<VideoCatalogAdapter.ViewHolder> {

        private Context context;
        private VideoDetailInfo video;
        private OnVideoControlListener onVideoControlListener;

        public VideoCatalogAdapter(Context context, VideoDetailInfo video, OnVideoControlListener onVideoControlListener) {
            this.context = context;
            this.video = video;
            this.onVideoControlListener = onVideoControlListener;
        }

        @Override
        public int getItemCount() {
            return video.seriesNumberTotal;
        }

        public void updateVideo(VideoDetailInfo video) {
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
        public VideoCatalogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.video_item_catalog, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(VideoCatalogAdapter.ViewHolder holder, int position) {
            final int seriesNumber = position + 1;
            holder.tv_index.setText(String.valueOf(seriesNumber));
            holder.tv_index.setBackgroundResource(seriesNumber == video.seriesNumber ?
                    R.drawable.oval_red_stroke : R.drawable.oval_white_stroke);
            holder.tv_index.setTextColor(seriesNumber == video.seriesNumber ?
                    0xFFFF6E5E : 0xFFFFFFFF);

            holder.tv_index.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onVideoControlListener != null) {
                        onVideoControlListener.onCatalogItemSelected(seriesNumber);
                    }
                }
            });
        }
    }

}
