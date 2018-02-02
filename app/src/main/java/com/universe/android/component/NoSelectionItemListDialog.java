package com.universe.android.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import com.universe.android.R;
import com.universe.android.model.SpinnerList;
import com.universe.android.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class contains methods required in creation, opening and closing of Dialog.
 * It displays the gender list or state list in  registration screen
 */
public class NoSelectionItemListDialog extends Dialog {
    final ArrayList<SpinnerList> arrSearlist = new ArrayList<>();
    SelectionItemAdapter adapter;
    ListView listView;
    private String strSelectedItem = null;
    private String strPreSelectedItem = null;
    private String searchText = "";

    public NoSelectionItemListDialog(final Context context, String defaultMsg, String selectedChoice, final List<SpinnerList> questions, int resId, final ItemPickerListner dtp) {
        super(context);
        dismiss();

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.app_custom_dialog_theme);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(resId, null);
        LinearLayout llDone = (LinearLayout) dialogView.findViewById(R.id.llDone);
        llDone.setVisibility(View.VISIBLE);
        Button tvDone = (Button) dialogView.findViewById(R.id.tvDone);
        tvDone.setVisibility(View.GONE);
        Button tvCancel = (Button) dialogView.findViewById(R.id.tvCancel);
        tvCancel.setVisibility(View.VISIBLE);
        tvCancel.setText(context.getString(R.string.close));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        SearchView searchView = (SearchView) dialogView.findViewById(R.id.searchView);
        listView = (ListView) dialogView.findViewById(R.id.list_popup);
        ((TextView) dialogView.findViewById(R.id.tvDialogTitle)).setText(defaultMsg);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (listView != null) {
            adapter = new SelectionItemAdapter(context, R.layout.dialog_single_row_normal, questions);
            listView.setAdapter(adapter);
            if (questions.size() > 10) {
                ViewGroup.LayoutParams lp = listView.getLayoutParams();
                lp.height = Utility.dpToPx(context, 350);
                listView.setLayoutParams(lp);
            } else {
                ViewGroup.LayoutParams lp = listView.getLayoutParams();
                lp.height = Utility.dpToPx(context, 300);
                listView.setLayoutParams(lp);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Utility.animateView(view);
                    Utility.hideSoftKeyboard((Activity) context);
                    if (searchText.isEmpty()) {
                        strSelectedItem = questions.get(position).getName();
                    } else {
                        strSelectedItem = arrSearlist.get(position).getName();
                    }

                    adapter.setSelectedIndex(position);
                    if (searchText.isEmpty()) {
                        dtp.OnDoneButton(NoSelectionItemListDialog.this, strSelectedItem, questions.get(position));

                    } else {
                        dtp.OnDoneButton(NoSelectionItemListDialog.this, strSelectedItem, arrSearlist.get(position));

                    }
                    adapter.notifyDataSetChanged();
                }
            });

            if (Utility.validateString(selectedChoice)) {
                for (int i = 0; i < questions.size(); i++) {
                    if (selectedChoice.equalsIgnoreCase(questions.get(i).getName())) {
                        adapter.setSelectedIndex(i);
                        strPreSelectedItem = questions.get(i).getName();
                        strSelectedItem = questions.get(i).getName();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
        setupSearchView(searchView, context, questions, selectedChoice);
        setContentView(dialogView);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(contextThemeWrapper);
        alertDialog.setView(dialogView);

        alertDialog.create();
        setCanceledOnTouchOutside(false);


        if (isShowing()) {
            dismiss();
        } else {
            show();
        }

    }

    private void setupSearchView(SearchView searchView, final Context context, final List<SpinnerList> questions, final String selectedChoice) {
        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(((Activity) context).getComponentName());
        if (searchView != null) {
            searchView.setSearchableInfo(searchableInfo);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    filter(newText, questions, context, selectedChoice);
                    return false;
                }
            });
        }

    }

    /**
     * Filter.
     *
     * @param charText the char text
     */
    private void filter(String charText, List<SpinnerList> questions, Context context, String selectedChoice) {
        if (arrSearlist != null && questions != null) {
            charText = charText.toLowerCase(Locale.getDefault());
            arrSearlist.clear();
            if (charText.length() == 0) {
                arrSearlist.addAll(questions);
            } else {
                for (SpinnerList wp : questions) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        arrSearlist.add(wp);
                    }
                }


            }
        }


        if (charText.length() > 0) {
            adapter = new SelectionItemAdapter(context, R.layout.dialog_single_row_normal, arrSearlist);

        } else {
            adapter = new SelectionItemAdapter(context, R.layout.dialog_single_row_normal, questions);
        }

        listView.setAdapter(adapter);
        if (Utility.validateString(selectedChoice)) {
            assert arrSearlist != null;
            for (int i = 0; i < arrSearlist.size(); i++) {
                if (selectedChoice.equalsIgnoreCase(arrSearlist.get(i).getName())) {
                    adapter.setSelectedIndex(i);
                    strPreSelectedItem = arrSearlist.get(i).getName();
                    strSelectedItem = arrSearlist.get(i).getName();
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

    //    /**
//     * ItemPickerListner interface used to allow the creator of a dialog to run some code when the dialog is canceled.
//     */
    public interface ItemPickerListner {
        void OnDoneButton(Dialog ansPopup, String strAns, SpinnerList spinnerItem);

//        void OnCancelButton(Dialog ansPopup, String strAns);
    }

    /**
     * This adapter being used to show list of gender or state which required in registration
     */
    private class SelectionItemAdapter extends ArrayAdapter<SpinnerList> {
        private int rowResourceId;
        private List<SpinnerList> list;
        private int index = -1;

        public SelectionItemAdapter(Context context, int rowResourceId,
                                    List<SpinnerList> list) {
            super(context, rowResourceId, list);
            this.rowResourceId = rowResourceId;
            this.list = list;
        }

        public void setSelectedIndex(int index) {
            this.index = index;
        }

        @Override
        public SpinnerList getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View v = view;
            ViewHolder viewHolder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) this.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(rowResourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (CheckedTextView) v.findViewById(R.id.tvCheckedItem);

                // store the holder with the view.
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }

            String quest = list.get(position).getName();
            final CheckedTextView txtTitle = viewHolder.title;

            txtTitle.setTag(quest);


            if (index != -1) {
                if (index == position) {
                    txtTitle.setChecked(true);
                } else {
                    txtTitle.setChecked(false);
                }
            }

            txtTitle.setText(quest);
            return v;
        }

        /**
         * A ViewHolder object stores each of the component views inside the tag field of the Layout,
         * so can immediately access them without the need to look them up repeatedly.
         */
        private class ViewHolder {
            CheckedTextView title;

        }

    }
}
