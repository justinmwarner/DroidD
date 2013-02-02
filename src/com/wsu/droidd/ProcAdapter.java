package com.wsu.droidd;

import java.util.ArrayList;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ProcAdapter implements ListAdapter {

	ArrayList<Proc> list;

	ProcAdapter(ArrayList<Proc> l) {
		list = l;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Proc getItem(int pos) {
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return list.get(pos).hashCode();
	}

	@Override
	public int getItemViewType(int pos) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

	@Override
	public View getView(int pos, View cv, ViewGroup parent) {
		TextView tv;
		if (cv == null) {
			if (parent == null) {
				return null;
			} else {
				tv = new TextView(parent.getContext());
			}
		} else {
			tv = new TextView(cv.getContext());
		}
		tv.setText(list.get(pos).getName());
		return tv;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dso) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver dso) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
