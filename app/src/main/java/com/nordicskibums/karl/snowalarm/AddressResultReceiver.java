package com.nordicskibums.karl.snowalarm;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class AddressResultReceiver extends ResultReceiver {
    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        //displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            //showToast(getString(R.string.address_found));
        }

    }
}