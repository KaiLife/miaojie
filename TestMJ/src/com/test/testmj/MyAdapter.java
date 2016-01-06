package com.test.testmj;

import java.util.List;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dongjunkun on 2015/12/1.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
	
    private boolean isFirst = true;
    private List<Integer> walls;
    
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private int item_normal_height;
    private int item_max_height;
    private float item_normal_alpha;
    private float item_max_alpha;
    private float item_normal_font_size;
    private float item_max_font_size;

    public MyAdapter(Context context, List<Integer> walls) {
        this.walls = walls;
        item_max_height = (int) context.getResources().getDimension(R.dimen.item_max_height);
        item_normal_height = (int) context.getResources().getDimension(R.dimen.item_normal_height);
        item_normal_font_size = context.getResources().getDimension(R.dimen.item_normal_font_size);
        item_max_font_size = context.getResources().getDimension(R.dimen.item_max_font_size);
        item_normal_alpha = context.getResources().getFraction(R.fraction.item_normal_mask_alpha, 1, 1);
        item_max_alpha = context.getResources().getFraction(R.fraction.item_max_mask_alpha, 1, 1);
    }
    
    public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public void clear(){
    	walls = null;
    }
    
    public void updateData(List<Integer> data){
		if(data!=null){
			walls = data;
		}
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wall, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footerview, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }        
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    	if (holder instanceof ItemViewHolder) {
    		if (isFirst && position == 0) {
                isFirst = false;
                ((ItemViewHolder) holder).mark.setAlpha(item_max_alpha);
                ((ItemViewHolder) holder).text.setTextSize(TypedValue.COMPLEX_UNIT_PX, item_max_font_size);
                ((ItemViewHolder) holder).itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, item_max_height));
            } else {
            	((ItemViewHolder) holder).mark.setAlpha(item_normal_alpha);
            	((ItemViewHolder) holder).text.setTextSize(TypedValue.COMPLEX_UNIT_PX, item_normal_font_size);
            	((ItemViewHolder) holder).itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, item_normal_height));
            }
    		((ItemViewHolder) holder).text.setText(String.format("光谷天地%d", position+1));
    		((ItemViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            Glide.with(holder.imageView.getContext()).load(walls.get(position)).into(holder.imageView);
    		if(walls!=null&&walls.size()>position){
    			((ItemViewHolder) holder).imageView.setImageResource(walls.get(position));
    		}    		
    	}        
    }

    @Override
    public int getItemCount() {
    	if(null == walls){
    		return 0;
    	}
        return walls.size()+1;
    }
    
    @Override
    public int getItemViewType(int position) {
    	if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }    	
    }

    public static class FooterViewHolder extends ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }
    
    public static class ItemViewHolder extends ViewHolder {
        public ImageView imageView;
        public View mark;
        public TextView text;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            mark = itemView.findViewById(R.id.mark);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
