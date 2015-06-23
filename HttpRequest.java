package com.example.urbanclouds.airsenseiv2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;

public class HttpRequest extends Thread {

    private final String GET  = "GET";
    private final String POST = "POST";

    private String method, ip;
    private HashMap<String,String> headers;
    private String[] KEYS;
    private URL url;
    private String postParameters;

    public String user, pass;

    private JSONObject response;

    private int postMethod;

    public JSONObject getResponse() {
        return response;
    }

    /*

    kind METHOD:
        0 -> GET
        1 -> POST WITH HEADERS
        2 -> POST WITH QUERY-STRING
     */

    public HttpRequest(String url, String ip) throws MalformedURLException {
        this.url = new URL(url);
        this.ip  = ip;
    }

    public boolean setMethod( String method, int kind) {
        this.method = method;
        postMethod = kind;
        return method_type(this.method);
    }

    public boolean setCredentials( String user, String pass) {
        this.user = user;
        this.pass = pass;

        return this.user != null && this.pass != null;
    }

    public boolean setHeaders(String[] keys, HashMap<String,String> heads) {
        headers = heads;
        KEYS = keys;
        return headers != null;
    }
    public boolean setQuery(String query) {
        this.postParameters = query;
        return this.postParameters != null;
    }

    private boolean method_type( String input) {
        return input.equalsIgnoreCase(GET) || input.equalsIgnoreCase(POST);
    }

    @Override
    public void run() {
        if (method.equalsIgnoreCase(GET)) {
            response = GET();
        }
        else {
            switch (postMethod) {
                case 1:
                    try {
                        response = POST();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        response = POST2();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private JSONObject GET() {
        JSONObject response = null;
        BufferedReader bufferedReader = null;
        InputStreamReader in = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) this.url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();

            in = new InputStreamReader(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(in);
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\r');
            }

            bufferedReader.close();

            in.close();
            response = new JSONObject(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private JSONObject POST2() throws JSONException {
        JSONObject response = null;

        BufferedReader bufferedReader = null;
        InputStreamReader in = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) this.url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(POST);
            urlConnection.setFixedLengthStreamingMode(
                    postParameters.getBytes().length);

            for (String KEY : KEYS) urlConnection.setRequestProperty(KEY, headers.get(KEY));

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new InputStreamReader(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(in);
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append('\r');
                }

                bufferedReader.close();

                in.close();
                response = new JSONObject(buffer.toString());
            }
            else Log.d("RQUEST-->","ALGO HA PASADO " + String.valueOf(urlConnection.getResponseCode()));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public String makeQueryRequest(HashMap<String,String> data) {
        final char PARAMETER_DELIMITER = '&';
        final char PARAMETER_EQUALS    = '=';

        StringBuilder parametersAsQueryString = new StringBuilder();

        if (data != null) {
            boolean firstParameter = true;

            for (String parameterName : data.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS)
                        .append(data.get(parameterName));

                firstParameter = false;
            }
        }
        return String.valueOf(parametersAsQueryString);
    }

    private JSONObject POST() throws JSONException {
        JSONObject response = null;

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("5588031263bf4457a7641c07", "5588031263bf4457a7641c08".toCharArray());
            }
        });

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(GET);
            conn.connect();

            int status = conn.getResponseCode();
            InputStream is;

            if(status >= HttpURLConnection.HTTP_BAD_REQUEST)
                is = conn.getErrorStream();
            else
                is = conn.getInputStream();

            Log.d("RespuestaHTTP",String.valueOf(status));

            byte[] buffer = new byte[8196];
            int readCount;
            StringBuilder builder = new StringBuilder();
            while ((readCount = is.read(buffer)) > -1) {
                builder.append(new String(buffer, 0, readCount));
            }
            response = new JSONObject(builder.toString());
            Log.d("Respuesta",response.toString());

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

}
