package com.example.cibi.messenger.GUI;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.cibi.messenger.Controller.EventHandler;
import com.example.cibi.messenger.Model.User;
import com.example.cibi.messenger.R;

/**
 * Fragment pre pisanie spravy na konkretny email.
 */
public class NewMessageFragment extends Fragment {
    private OnMessageFragmentInteractionListener mListener;

    public NewMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_new_message, container, false);
        Button sendMsg = (Button) frameLayout.findViewById(R.id.sendMsg);

        final EditText recieverField = (EditText) frameLayout.findViewById(R.id.reciever);
        final EditText msgTextField = (EditText) frameLayout.findViewById(R.id.msgText);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reciever = recieverField.getText().toString();
                String msgText = msgTextField.getText().toString();
                onButtonPressed(reciever,msgText);
            }
        });
        // Inflate the layout for this fragment
        return frameLayout;
    }

    // Metoda handluje event stlacenia buttonu send message.
    public void onButtonPressed(String reciever,String msgText) {
        if (mListener != null) {
            mListener.onMsgFragmentInteraction(reciever,msgText,this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageFragmentInteractionListener) {
            mListener = (OnMessageFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Umoznuje Mainscreenu reagovat na odoslanie spravy.
     */
    public interface OnMessageFragmentInteractionListener {
        void onMsgFragmentInteraction(String reciever,String msgText,NewMessageFragment fragment);
    }


}
