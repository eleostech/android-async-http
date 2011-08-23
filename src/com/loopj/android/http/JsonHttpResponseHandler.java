/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Used to intercept and handle the responses from requests made using 
 * {@link AsyncHttpClient}, with automatic parsing into a {@link JSONObject}
 * or {@link JSONArray}.
 * <p>
 * This class is designed to be passed to get, post, put and delete requests
 * with the {@link #onSuccess(JSONObject)} or {@link #onSuccess(JSONArray)}
 * methods anonymously overridden.
 * <p>
 * Additionally, you can override the other event methods from the 
 * parent class.
 */
public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(JSONObject response) {}


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(JSONArray response) {}

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param the actual response body parsed into a json array
     */
    public void onFailureWithContent(Throwable error, JSONArray content) { onFailure(error); }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param the actual response body parsed into a json object
     */
    public void onFailureWithContent(Throwable error, JSONObject content) { onFailure(error); }

    // Utility methods
    @Override
    protected void handleSuccessMessage(String responseBody) {
        super.handleSuccessMessage(responseBody);

        try {
            Object jsonResponse = parseResponse(responseBody);
            if(jsonResponse instanceof JSONObject) {
                onSuccess((JSONObject)jsonResponse);
            } else if(jsonResponse instanceof JSONArray) {
                onSuccess((JSONArray)jsonResponse);
            } else {
                onFailure(new Exception("Unexpected response."));
            }
        } catch(JSONException e) {
            onFailure(e);
        }
    }

    @Override
    protected void handleFailureMessage(String responseBody, Throwable error) {
        try {
            if (responseBody != null && responseBody.length() > 0) {
                Object jsonResponse = parseResponse(responseBody);
                if(jsonResponse instanceof JSONObject) {
                    onFailureWithContent(error, (JSONObject)jsonResponse);
                } else if(jsonResponse instanceof JSONArray) {
                    onFailureWithContent(error, (JSONArray)jsonResponse);
                } else {
                    onFailure(error);
                }
            } else {
                onFailure(error);
            }
        } catch(JSONException e) {
            onFailure(e);
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        return new JSONTokener(responseBody).nextValue();
    }
}
