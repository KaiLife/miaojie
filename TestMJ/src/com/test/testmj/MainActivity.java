package com.test.testmj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.TypedValue;

import com.test.testmj.MyAdapter.ItemViewHolder;

public class MainActivity extends Activity implements OnRefreshListener{
	
    private int item_normal_height;
    private int item_max_height;
    private float item_normal_alpha;
    private float item_max_alpha;
    private float alpha_d;
    private float item_normal_font_size;
    private float item_max_font_size;
    private float font_size_d;

    private List<Integer> imgs = Arrays.asList(R.drawable.wall01,
            R.drawable.wall02, R.drawable.wall03, R.drawable.wall04, R.drawable.wall05,
            R.drawable.wall06, R.drawable.wall07, R.drawable.wall08, R.drawable.wall09,
            R.drawable.wall10);
    
    private List<Integer> cacheList = new ArrayList<Integer>();

    private SwipeRefreshLayout mSwipeRefreshWidget;
    
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter adapter;
    
    private ItemViewHolder viewHolder;
    private RecyclerView.ViewHolder firstViewHolder,secondViewHolder,threeViewHolder,lastViewHolder;
    
    private int lastVisibleItem;
    
    public static final int DATA_REFRESH = 0;
    public static final int DATA_LOAD_MORE = 1;
    
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DATA_REFRESH:
                mSwipeRefreshWidget.setRefreshing(false);

                adapter.clear();
                adapter.setFirst(true);
                cacheList.clear();
                updateData();
                break;
                
            case DATA_LOAD_MORE:
            	updateData();
                break;
                
            default:
                break;
            }
        }		
    };
    
    private void updateData() {
		cacheList.addAll(imgs);
		adapter.updateData(cacheList);
		adapter.notifyDataSetChanged();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item_max_height = (int) getResources().getDimension(R.dimen.item_max_height);
        item_normal_height = (int) getResources().getDimension(R.dimen.item_normal_height);
        item_normal_font_size = getResources().getDimension(R.dimen.item_normal_font_size);
        item_max_font_size = getResources().getDimension(R.dimen.item_max_font_size);
        item_normal_alpha = getResources().getFraction(R.fraction.item_normal_mask_alpha, 1, 1);
        item_max_alpha = getResources().getFraction(R.fraction.item_max_mask_alpha, 1, 1);

        font_size_d = item_max_font_size - item_normal_font_size;
        alpha_d = item_max_alpha - item_normal_alpha;

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshWidget.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        mSwipeRefreshWidget.setOnRefreshListener(this);  
        
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        adapter = new MyAdapter(this, cacheList);
        mRecyclerView.setAdapter(adapter);
//        View moreView = getLayoutInflater().inflate(R.layout.footer_more, null);
//        TextView more = (TextView) moreView.findViewById(R.id.more);
//        more.getLayoutParams().height = DeviceUtils.getScreenHeight(this) -
//                item_max_height;
//        RecyclerViewUtils.setFooterView(mRecyclerView, moreView); 
        
        mRecyclerView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    handler.sendEmptyMessageDelayed(DATA_LOAD_MORE, 2000);
                }
				
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
                firstViewHolder = recyclerView
                        .findViewHolderForPosition(layoutManager.findFirstVisibleItemPosition());
                secondViewHolder = recyclerView
                        .findViewHolderForPosition(layoutManager.findFirstCompletelyVisibleItemPosition());
                threeViewHolder = recyclerView
                        .findViewHolderForPosition(layoutManager.findFirstCompletelyVisibleItemPosition() + 1);
                lastViewHolder = recyclerView
                        .findViewHolderForPosition(layoutManager.findLastVisibleItemPosition());
                
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                
                if (firstViewHolder != null && firstViewHolder instanceof ItemViewHolder) {
                    viewHolder = (ItemViewHolder) firstViewHolder;
                    if (viewHolder.itemView.getLayoutParams().height - dy <= item_max_height
                            && viewHolder.itemView.getLayoutParams().height - dy >= item_normal_height) {
                        viewHolder.itemView.getLayoutParams().height = viewHolder.itemView.getLayoutParams().height - dy;
                        viewHolder.mark.setAlpha(viewHolder.mark.getAlpha() - dy * alpha_d / item_normal_height);
                        viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                viewHolder.text.getTextSize() - dy * font_size_d / item_normal_height);
                        viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());
                    }
                }
                
                if (secondViewHolder != null && secondViewHolder instanceof ItemViewHolder) {
                    viewHolder = (ItemViewHolder) secondViewHolder;
                    if (viewHolder.itemView.getLayoutParams().height + dy <= item_max_height
                            && viewHolder.itemView.getLayoutParams().height + dy >= item_normal_height) {
                        viewHolder.itemView.getLayoutParams().height = viewHolder.itemView.getLayoutParams().height + dy;
                        viewHolder.mark.setAlpha(viewHolder.mark.getAlpha() + dy * alpha_d / item_normal_height);
                        viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                viewHolder.text.getTextSize() + dy * font_size_d / item_normal_height);
                        viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());
                    }
                }

                if (threeViewHolder != null && threeViewHolder instanceof ItemViewHolder) {
                    viewHolder = (ItemViewHolder) threeViewHolder;
                    viewHolder.mark.setAlpha(item_normal_alpha);
                    viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, item_normal_font_size);
                    viewHolder.itemView.getLayoutParams().height = item_normal_height;
                    viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());
                }
                
                if (lastViewHolder != null && lastViewHolder instanceof ItemViewHolder) {
                    viewHolder = (ItemViewHolder) lastViewHolder;
                    viewHolder.mark.setAlpha(item_normal_alpha);
                    viewHolder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, item_normal_font_size);
                    viewHolder.itemView.getLayoutParams().height = item_normal_height;
                    viewHolder.itemView.setLayoutParams(viewHolder.itemView.getLayoutParams());
                }
			}  
		});
        
//        mSwipeRefreshWidget.setRefreshing(true);
        handler.sendEmptyMessageDelayed(DATA_REFRESH, 2000);
    }    

	@Override
	public void onRefresh() {		
		handler.sendEmptyMessageDelayed(DATA_REFRESH, 2000);
	}
}
