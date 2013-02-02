package com.wsu.droidd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Proc detail screen. This fragment is either
 * contained in a {@link ProcListActivity} in two-pane mode (on tablets) or a
 * {@link ProcDetailActivity} on handsets.
 */
public class ProcDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	private static final String TAG = "ProcDetailFragment";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Proc mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ProcDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = ProcListFragment.procs.get(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_proc_detail, container, false);

		// Show the content as text in a TextView.
		if (mItem != null) {
			String text = "Name: ";
			text += mItem.getName();
			text += "\nIPs: " + mItem.getIps().replace(" ", "\n");
			((TextView) rootView.findViewById(R.id.proc_detail)).setText(text);
		}

		return rootView;
	}
}
