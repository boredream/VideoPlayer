package com.boredream.videoplayer.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boredream.videoplayer.R;
import com.boredream.videoplayer.video.bean.VideoDetailInfo;

import java.util.List;

public class VideoRatioDialog extends FrameLayout {

    private ListView lv_ratio;
    private VideoRatioAdapter adapter;

    public VideoRatioDialog(Context context) {
        super(context);
        init();
    }

    public VideoRatioDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoRatioDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoRatioDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_ratio_dialog, this);

        findViewById(R.id.v_mask).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        // TODO: 2017/6/7 点 more 验证下
//        findViewById(R.id.video_ratio_content).setOnClickListener(v -> {/*nothing*/});
        lv_ratio = (ListView) findViewById(R.id.lv_ratio);
    }

    public void setVideo(VideoDetailInfo video) {
        adapter = new VideoRatioAdapter(getContext(), video, onVideoControlListener);
        lv_ratio.setAdapter(adapter);
    }

    public void show() {
        setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    public void hide() {
        setVisibility(GONE);
    }

    private OnVideoControlListener onVideoControlListener;

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.onVideoControlListener = onVideoControlListener;
    }

    public static class VideoRatioAdapter extends BaseAdapter {

        private Context context;
        private VideoDetailInfo video;
        private OnVideoControlListener onVideoControlListener;
        private List<String> fluencyList;

        public VideoRatioAdapter(Context context, VideoDetailInfo video, OnVideoControlListener onVideoControlListener) {
            this.context = context;
            this.video = video;
            this.onVideoControlListener = onVideoControlListener;
            this.fluencyList = video.getDefinitionList();
            VideoFluencyConstants.sortFluListByPriority(fluencyList);
        }

        @Override
        public int getCount() {
            return fluencyList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.video_item_ratio, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String fluency = fluencyList.get(position);

            holder.tv_title.setTextColor(fluency.equals(video.getCurDefinition())
                    ? 0xFFFD7D6F : 0xFFFFFFFF);
            holder.tv_title.setText(VideoFluencyConstants.getFluencyTitle(fluency));

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onVideoControlListener != null) {
                        onVideoControlListener.onRatioSelected(fluency);
                    }
                    video.setCurDefinition(fluency);
                }
            });

            return convertView;
        }

        public static class ViewHolder {
            public TextView tv_title;

            public ViewHolder(final View itemView) {
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }

    }

}
