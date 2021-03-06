/*
 * Copyright (c) 2017. Javier Salas
 */

//This class is only to be used to send to the server when you are expecting no response


package com.salas.javiert.magicmirror.Objects.helperObjects;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.salas.javiert.magicmirror.Objects.SingletonObjects.myQueueClasses.myQueueItem;
import com.salas.javiert.magicmirror.Objects.SingletonObjects.myQueueClasses.myQueueTask;
import com.salas.javiert.magicmirror.Resources.DatabaseRestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by javi6 on 6/10/2017.
 */

public class sendToServerObject {
    private String url;
    private StringEntity myEntity;

    // Constructor that takes a myQueueTask as a argument
    public sendToServerObject(myQueueTask Task) {
        try {
            CreateStringEntitiy(Task);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Constructor that takes a myQueueItem as a argument
    public sendToServerObject(myQueueItem Item) {
        try {
            CreateStringEntitiy(Item);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Setters and Getters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;

    }

    public StringEntity getMyEntity() {
        return myEntity;
    }

    public void setMyEntity(StringEntity myEntity) {
        this.myEntity = myEntity;
    }

    // Populates the actions list with a sendToServerObject.java class that is unique (Due to the nature of myQueueTask) in action and table
    // The sendToServerObject class contains the url that it should be sent to as well as the StringEntity
    private void CreateStringEntitiy(myQueueTask Task) throws UnsupportedEncodingException, JSONException {
        String QueueURLDestination, QueueAction;
        QueueURLDestination = null;
        QueueAction = null;
        if (Task.getMyTable() == myQueueItem.TABLE.TODO)
            QueueURLDestination = "interfacetodo.php";
        if (Task.getMyTable() == myQueueItem.TABLE.ASSIGNMENTS)
            QueueURLDestination = "interfaceassignments.php";
        if (Task.getMyTable() == myQueueItem.TABLE.OCCUR)
            QueueURLDestination = "interfaceoccur.php";

        setUrl(QueueURLDestination);

        if (Task.getMyMode() == myQueueItem.MODE.ADD)
            QueueAction = "ADD";
        if (Task.getMyMode() == myQueueItem.MODE.REMOVE)
            QueueAction = "REMOVE";
        if (Task.getMyMode() == myQueueItem.MODE.EDIT)
            QueueAction = "EDIT";
        if (Task.getMyMode() == myQueueItem.MODE.SET_DONE)
            QueueAction = "SET_DONE";


        JSONObject ParamObject = new JSONObject();


        ParamObject.put(QueueAction, Task.getJSONArray());
        StringEntity myEntitiy = new StringEntity(ParamObject.toString());
        myEntitiy.setContentEncoding("UTF-8");
        myEntitiy.setContentType("application/json");


        setMyEntity(myEntitiy);

    }

    // Populates the actions list with a sendToServerObject.java class that is unique (Due to the nature of myQueueTask) in action and table
    // The sendToServerObject class contains the url that it should be sent to as well as the StringEntity
    private void CreateStringEntitiy(myQueueItem item) throws UnsupportedEncodingException, JSONException {
        String QueueURLDestination, QueueAction;
        QueueURLDestination = null;
        QueueAction = null;
        if (item.getMyTable() == myQueueItem.TABLE.TODO)
            QueueURLDestination = "interfacetodo.php";
        if (item.getMyTable() == myQueueItem.TABLE.ASSIGNMENTS)
            QueueURLDestination = "interfaceassignments.php";
        if (item.getMyTable() == myQueueItem.TABLE.OCCUR)
            QueueURLDestination = "interfaceoccur.php";

        setUrl(QueueURLDestination);

        if (item.getMyMode() == myQueueItem.MODE.ADD)
            QueueAction = "ADD";
        if (item.getMyMode() == myQueueItem.MODE.REMOVE)
            QueueAction = "REMOVE";
        if (item.getMyMode() == myQueueItem.MODE.EDIT)
            QueueAction = "EDIT";
        if (item.getMyMode() == myQueueItem.MODE.SET_DONE)
            QueueAction = "SET_DONE";

        JSONObject ParamObject = new JSONObject();
        ParamObject.put(QueueAction, item.getMyJSONObject());
        StringEntity myEntitiy = new StringEntity(ParamObject.toString());
        myEntitiy.setContentEncoding("UTF-8");
        myEntitiy.setContentType("application/json");
        setMyEntity(myEntitiy);

    }


    // This function runs an AsyncTask to send the information
    public void send(Context context) {
        Context[] myTaskParams = {context, null, null};
        //This has do be done in a AsyncTask as to not block the main thread.
        new SendTask().execute(myTaskParams);
    }

    // This function actually send it
    private void sendToServer(Context context) {
        //Define our response handler
        TextHttpResponseHandler response = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Log.d("Failure: ", responseString);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                Log.d("Response: ", responseString);
                Log.d("StatusCode: ", String.valueOf(statusCode));
                for (cz.msebera.android.httpclient.Header header : headers)
                    Log.d("Headers:", header.toString());

            }
        };
        DatabaseRestClient.post(context, getUrl(), getMyEntity(), "application/x-www-form-urlencoded", response);
    }

    // This Task allows us to send the information to the server.
    private class SendTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {
            Log.d("serverAddressItem: ", "Attempting to connect to server...");
            sendToServer(params[0]);
            return null;
        }
    }
}
