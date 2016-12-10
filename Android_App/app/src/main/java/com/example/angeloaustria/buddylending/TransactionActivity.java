package com.example.angeloaustria.buddylending;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionActivity extends AppCompatActivity implements RequesterFragment.OnMoneyRequestedListener,
        NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback,
        ReceiverFragment.OnTransactionDecisionListener {

    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();
    private Fragment currentFrag;
    private String lenderName, borrowerName;
    private float amountRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpRequest request = new HttpRequest(this);
        int mode = getIntent().getIntExtra("Mode", 1);
        if (mode == 1) {
            insertFragment(currentFrag = RequesterFragment.newInstance());
            request.getAccountBalance(getIntent().getStringExtra("username"), new ResultCallback() {
                @Override
                public void run() {
                    if (isSuccess()) {
                        double money = (double) getData();
                        ((RequesterFragment) currentFrag).getBalanceView().setText("$" +
                                Double.toString(money));
                    } else {
                        ((RequesterFragment) currentFrag).getBalanceView().setText("Undefined");
                        Log.d("DEBUG", getErr());
                    }
                }
            });
            borrowerName = getIntent().getStringExtra("username");
        } else {
            insertFragment(currentFrag = ReceiverFragment.newInstance());
            request.getAccountBalance(getIntent().getStringExtra("username"), new ResultCallback() {
                @Override
                public void run() {
                    if (isSuccess()) {
                        double money = (double) getData();
                        ((ReceiverFragment) currentFrag).getBalanceView().setText("$" +
                                Double.toString(money));
                    } else {
                        ((RequesterFragment) currentFrag).getBalanceView().setText("Undefined");
                        Log.d("DEBUG", getErr());
                    }
                }
            });
            lenderName = getIntent().getStringExtra("username");
        }
        //Check if NFC is available on device
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        } else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestClicked(View view) {
        String amount = ((EditText) currentFrag.getView().findViewById(R.id.editText_transaction_amount)).
                getText().toString();
        messagesToSendArray.add(amount);
        messagesToSendArray.add(borrowerName);
        Toast.makeText(this, "Amount requested: $" + amount + "\nby " + borrowerName,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfirm(View view) {
        view.setVisibility(View.INVISIBLE);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse("22/12/2016");
        } catch (java.text.ParseException e) {
            Log.d("DEBUG", e.getMessage());
        }
        final Activity activity = this;
        Timestamp ts = new Timestamp(date.getTime());
        this.completeTransaction(lenderName, borrowerName, "gift", ts.getTime(),
                amountRequested, new ResultCallback() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            JSONObject response = (JSONObject) getData();
                            double balance = -1;
                            try {
                                balance = (double) response.get("lenderbalance");
                            } catch (JSONException e) {
                                Log.d("DEBUG", e.getMessage());
                            }
                            if (balance != -1)
                                ((ReceiverFragment) currentFrag).getBalanceView().
                                        setText("$" + balance);
                            else
                                ((ReceiverFragment) currentFrag).getBalanceView().setText("Undefined");
                            activity.finish();
                            /*HttpRequest request = new HttpRequest(activity);
                            request.getAccountBalance(getIntent().getStringExtra("username"), new ResultCallback() {
                                @Override
                                public void run() {
                                    if (isSuccess()) {
                                        double money = (double) getData();
                                        ((RequesterFragment) currentFrag).getBalanceView().
                                                setText("$" + Double.toString(money));
                                    } else {
                                        ((RequesterFragment) currentFrag).getBalanceView().setText("Undefined");
                                        Log.d("DEBUG", getErr());
                                    }
                                    activity.finish();
                                }
                            });*/
                            Log.d("DEBUG", "Payment sent");
                        } else {
                            Log.d("DEBUG", "Error! Payment didn`t go through");
                        }
                    }
                });
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        messagesToSendArray.clear();
        //This is called when the system detects that our NdefMessage was
        //Successfully sent
    }

    public NdefRecord[] createRecords() {
        NdefRecord[] records = new NdefRecord[messagesToSendArray.size() + 1];
        //To Create Messages Manually if API is less than
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < messagesToSendArray.size(); i++) {
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,            //Description of our payload
                        new byte[0],                    //The optional id for our Record
                        payload);                       //Our payload for the Record

                records[i] = record;
            }
        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            for (int i = 0; i < messagesToSendArray.size(); i++) {
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain", payload);
                records[i] = record;
            }
        }
        records[messagesToSendArray.size()] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        if (messagesToSendArray.size() == 0) {
            return null;
        }
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record : attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure we don't pass along our AAR (Android Applicatoin Record)
                    if (string.equals(getPackageName())) {
                        continue;
                    }
                    messagesReceivedArray.add(string);
                }
                amountRequested = Float.parseFloat(messagesReceivedArray.get(0));
                borrowerName = messagesReceivedArray.get(1);
                //Toast.makeText(this, "Requested $" + amountRequested, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Requested by " + borrowerName, Toast.LENGTH_SHORT).show();
                ((TextView) currentFrag.getView().findViewById(R.id.textView_amount_requested)).
                        setText("$" + amountRequested);
                ((TextView) currentFrag.getView().findViewById(R.id.textView_user_karma)).
                        setText(borrowerName + "`s karma");
                currentFrag.getView().findViewById(R.id.button_confirm_transaction).setVisibility(View.VISIBLE);
                displayUpVotes(borrowerName);
                displayDownVotes(borrowerName);

            } else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("messagesToSend", messagesToSendArray);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messagesToSendArray = savedInstanceState.getStringArrayList("messagesToSend");
    }

    private void insertFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private void displayUpVotes(String username) {
        final TextView upVotes = (TextView) currentFrag.getView().findViewById(R.id.upVotesRequest);
        upVotes.setText("0");
        HttpRequest request = new HttpRequest(this);
        request.getUpvotes(username, new ResultCallback() {
            @Override
            public void run() {
                if (isSuccess()) {
                    double totalUpVotes = (double) getData();
                    upVotes.setText(Double.toString(totalUpVotes));
                } else {
                    upVotes.setText("Undefined");
                    Log.d("DEBUG", getErr());
                }
            }
        });

    }

    private void displayDownVotes(String username) {
        final TextView downVotes = (TextView) currentFrag.getView().findViewById(R.id.downVotesRequest);
        downVotes.setText("0");
        HttpRequest request = new HttpRequest(this);
        request.getDownvotes(username, new ResultCallback() {
            @Override
            public void run() {
                if (isSuccess()) {
                    double totalDownVotes = (double) getData();
                    downVotes.setText(Double.toString(totalDownVotes));
                } else {
                    downVotes.setText("Undefined");
                    Log.d("DEBUG", getErr());
                }
            }
        });

    }

    public void completeTransaction(String userSender, String userReceiver,
                                    String type, long timestamp, float amount, final ResultCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        RequestQueue queue = Volley.newRequestQueue(this);
        final JSONObject jsonBody;
        jsonBody = new JSONObject();
        try {
            jsonBody.put("userToName", userReceiver);
            jsonBody.put("userFromName", userSender);
            jsonBody.put("amount", amount);
            jsonBody.put("type", type);
            jsonBody.put("expectedReturnDate", timestamp);
        } catch (JSONException e) {
            Log.d("DEBUG", e.getMessage());
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest("http://138.197.132.23/transaction",
                jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean status = false;
                try{
                    status = (boolean) response.get("status");
                }
                catch(JSONException e){
                    callback.setSuccess(false);
                }
                if(status) {
                    callback.setSuccess(true);
                    callback.setData(response);
                }
                else{
                    callback.setSuccess(false);
                }
                handler.post(callback);
                Log.d("DEBUG", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.setSuccess(false);
                callback.setErr(error.getMessage());
                handler.post(callback);
                Log.d("DEBUG", error.getMessage());
            }
        });
        queue.add(jsonRequest);
    }
}