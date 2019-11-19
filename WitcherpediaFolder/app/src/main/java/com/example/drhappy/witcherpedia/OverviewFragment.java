package com.example.drhappy.witcherpedia;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

	// Rename and change types of parameters
	private DBHelper witcherDB;

	public OverviewFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment OverviewFragment.
	 */
	public static OverviewFragment newInstance() {

		return new OverviewFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_overview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		witcherDB = DBHelper.getInstance(getActivity());

		setOverview(view);
	}

	void setOverview(View rootView) {
		String faction_selected = ((Witcherpedia) getActivity().getApplicationContext()).getFaction_selected();

		ScrollView desc_scroll = rootView.findViewById(R.id.oscroll);
		desc_scroll.scrollTo(0, 0);

		ImageView oicon = rootView.findViewById(R.id.oicon);
		TextView description = rootView.findViewById(R.id.description);

		try (Cursor resultSet = witcherDB.getOverview(faction_selected)) {
			for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
				String drawablen = resultSet.getString(resultSet.getColumnIndex("drawablen"));
				int resourceId = getActivity().getResources().getIdentifier(drawablen, "drawable", getActivity().getPackageName());
				oicon.setImageResource(resourceId);

				String overview = resultSet.getString(resultSet.getColumnIndex("overview"));
				description.setText(getHtml("-1", overview));
			}
		}

		Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle(faction_selected);

		((Witcherpedia) getActivity().getApplicationContext()).setCurrent_view("Overview");
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(((Witcherpedia) getActivity().getApplicationContext()).getCurrent_view());
	}

	private Spanned getHtml(String color, String stat) {
		String ifcolor;
		if (color == null || color.equals("-1")) {
			ifcolor = "";
		} else {
			ifcolor = "color='#" + color + "'";
		}

		Spanned html;
		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		html = HtmlCompat.fromHtml("<font " + ifcolor + ">" + stat + "</font>", HtmlCompat.FROM_HTML_MODE_COMPACT);
		/*} else {
			html = Html.fromHtml("<font " + ifcolor + ">" + stat + "</font>");
		}*/

		return html;
	}
}
