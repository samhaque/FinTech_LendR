package com.example.angeloaustria.buddylending;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ReceiverFragment extends Fragment {
    private OnTransactionDecisionListener mListener;
    public ReceiverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ReceiverFragment.
     */
    public static ReceiverFragment newInstance() {
        ReceiverFragment fragment = new ReceiverFragment();
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
        View view = inflater.inflate(R.layout.activity_receive, container, false);
        view.findViewById(R.id.button_confirm_transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.onConfirm(v);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReceiverFragment.OnTransactionDecisionListener) {
            mListener = (ReceiverFragment.OnTransactionDecisionListener) context;
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
            return (TextView)getView().findViewById(R.id.textView_balance_receiver);
        return null;
    }

    public interface OnTransactionDecisionListener{
        void onConfirm(View v);
    }
}
