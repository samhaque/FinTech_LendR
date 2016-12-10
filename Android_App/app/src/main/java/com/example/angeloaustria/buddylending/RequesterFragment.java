package com.example.angeloaustria.buddylending;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMoneyRequestedListener} interface
 * to handle interaction events.
 * Use the {@link RequesterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequesterFragment extends Fragment {
    private OnMoneyRequestedListener mListener;

    public RequesterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment RequesterFragment.
     */
    public static RequesterFragment newInstance() {
        RequesterFragment fragment = new RequesterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_request, container, false);
        ((Button)view.findViewById(R.id.button_request_money)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRequestClicked(v);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoneyRequestedListener) {
            mListener = (OnMoneyRequestedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoneyRequestedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public TextView getBalanceView(){
        if(getView() != null)
            return (TextView) getView().findViewById(R.id.textView_current_balance_request);
        return null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMoneyRequestedListener {
        void onRequestClicked(View view);
    }
}
