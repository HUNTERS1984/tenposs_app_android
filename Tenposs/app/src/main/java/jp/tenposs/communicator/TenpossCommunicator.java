package jp.tenposs.communicator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jp.tenposs.datamodel.Key;
import jp.tenposs.tenposs.MainApplication;
import jp.tenposs.utils.SharedPreferencesHelper;

/**
 * Created by ambient on 7/25/16.
 */
public abstract class TenpossCommunicator extends AsyncTask<Bundle, Integer, Bundle> {

    public final static String API_ADDRESS_DEV = "http://dev.tenposs.jp/api";
    //public final static String API_ADDRESS = "http://ec2-54-204-210-230.compute-1.amazonaws.com/tenposs/api/public/index.php/api/v1";
    public final static String API_ADDRESS = "http://54.153.78.127/api/v1";
    public final static String API_LOGIN = "/login?";
    public final static String API_LOGOUT = "/logout?";
    public final static String API_TOP = "/top?";
    public final static String API_MENU = "/menu?";
    public final static String API_APPINFO = "/appinfo?";
    public final static String API_ITEMS = "/items?";
    public final static String API_ITEMS_DETAIL = "/items/detail?";
    public final static String API_ITEMS_RELATE = "/items/related?";
    public final static String API_PHOTO = "/photo?";
    public final static String API_NEWS = "/news?";
    public final static String API_NEWS_DETAIL = "/news/detail?";
    public final static String API_RESERVE = "/reserve?";
    public final static String API_COUPON = "/coupon?";
    public final static String API_COUPON_DETAIL = "/coupon/detail?";
    public final static String API_APPUSER = "/appuser?";
    public final static String API_SETPUSHKEY = "/setpushkey?";

    public interface TenpossCommunicatorListener {
        void completed(TenpossCommunicator request, Bundle responseParams);
    }

    TenpossCommunicatorListener listener;

    public TenpossCommunicator(TenpossCommunicatorListener listener) {
        this.listener = listener;
    }

    static CookieManager cookieManager;
    static KeyStore trustStore;
    static MySSLSocketFactory sf;
    static HostnameVerifier hostnameVerifier;

    protected HttpURLConnection m_pUrlConnection;

    static void loadCookies(Context context, URI uri) {
        try {
            SharedPreferences sharedPreferences = SharedPreferencesHelper.getSharedPreferences(context);
            Set<String> cookieArray = sharedPreferences.getStringSet(uri.getHost(), null);
            String str = uri.getHost();
            if (cookieArray != null) {
                CookieStore cookieStore = cookieManager.getCookieStore();
                for (String cookie : cookieArray) {
                    HttpCookie ck = HttpCookie.parse(cookie).get(0);
                    cookieArray.add(cookie);
                    List<HttpCookie> cookies = cookieStore.getCookies();
                    if (cookies.contains(ck) == false) {
                        try {
                            cookieStore.add(new URI(str), ck);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    static boolean generateOnce() {
        if (cookieManager != null) {
            return true;
        }
        try {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            sf = new MySSLSocketFactory(trustStore);
            //sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            sf.setHostnameVerifier(new AllowAllHostnameVerifier());
            hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    System.out.println("Accept : " + arg0);
                    return true;
                }
            };
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            cookieManager = null;
            return false;
        }
    }

    protected abstract boolean request(Bundle bundle);

    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }
            };
            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

        public javax.net.ssl.SSLSocketFactory getSocketFactoryEx() {
            // TODO Auto-generated method stub
            return sslContext.getSocketFactory();
        }
    }

    public enum CommunicationCode {

        GeneralError,//ko quan tam return code, chi can show message
        ConnectionFailed,
        ConnectionAborted,
        ConnectionInvalidInput, //
        ConnectionMalformedURL, // invalid URL format
        ConnectionInterrupt, // connection is interrupted
        ConnectionTimedOut, // connection is timed-out
        ConnectionErrorOpenInput, // cannot send data to the server.
        ConnectionErrorOpenOutput, // cannot open output.
        ConnectionErrorReadInput, // cannot read response from the server.
        ConnectionErrorWriteOutput, // cannot save to output

        ConnectionSuccess,

    }


    @Override
    protected Bundle doInBackground(Bundle... params) {
        Bundle bundle = params[0];
        request(bundle);
        return bundle;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(Bundle result) {
        if (this.listener != null) {
            this.listener.completed(this, result);
        }
    }


    void printRequestHeader(HttpURLConnection urlConnection) {
        Map<String, List<String>> headerMap = urlConnection.getRequestProperties();
        Set<String> headers = headerMap.keySet();
        for (String header : headers) {
            if (header != null)
                System.out.println("Request - " + header + " : " + urlConnection.getRequestProperty(header));
        }
    }

    HashMap<String, String> printResponseHeader(HttpURLConnection urlConnection) {
        HashMap<String, String> responseHeader = new HashMap<>();
        try {
            Map<String, List<String>> headerMap = urlConnection.getHeaderFields();
            Set<String> headers = headerMap.keySet();
            for (String header : headers) {
                if (header != null) {
                    responseHeader.put(header, urlConnection.getHeaderField(header));
                }
            }

            List<String> cookiesHeader = headerMap.get("Set-Cookie");
            if (cookiesHeader != null) {
                CookieStore cookieStore = cookieManager.getCookieStore();
                Set<String> cookieArray = new HashSet<>();
                for (String cookie : cookiesHeader) {
                    HttpCookie ck = HttpCookie.parse(cookie).get(0);
                    cookieArray.add(cookie);
                    List<HttpCookie> cookies = cookieStore.getCookies();
                    if (cookies.contains(ck) == false) {
                        cookieStore.add(urlConnection.getURL().toURI(), ck);
                    }
                }

                SharedPreferences sharedPreferences = SharedPreferencesHelper.getSharedPreferences(MainApplication.getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String uri = urlConnection.getURL().toURI().toString();
                editor.putStringSet(urlConnection.getURL().toURI().getHost(), cookieArray);
                editor.apply();
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Tenposs", e.getMessage());
        }
        return responseHeader;
    }

    HttpURLConnection getConnection(String strUrl, int timeout, boolean forceSSL, byte[] dataUpload, HashMap<String, String> headers) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            if (generateOnce() == false) {
                throw new NullPointerException("generateOnce");
            }

            URL url = new URL(strUrl);

            loadCookies(MainApplication.getContext(), url.toURI());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeout);

            if (headers != null) {
                Set<String> headerSet = headers.keySet();
                for (String header : headerSet) {

                    if (header != null) {
                        urlConnection.setRequestProperty(header, headers.get(header));
                    }
                }
            }
            if (dataUpload != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
                urlConnection.setRequestProperty("Content-Length", "" + dataUpload.length);
            }
            if (urlConnection instanceof HttpsURLConnection) {
                if (forceSSL == true) {
                    ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sf.getSocketFactoryEx());
                    ((HttpsURLConnection) urlConnection).setHostnameVerifier(hostnameVerifier);
                    try {
                        if (dataUpload != null) {
                            urlConnection.connect();
                            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                            out.write(dataUpload);
                            out.close();
                        }
                    } catch (Exception e) {
                        try {
                            urlConnection.disconnect();
                        } catch (Exception e2) {
                        }
                        throw e;
                    }
                } else {
                    try {
                        if (dataUpload != null) {
                            urlConnection.connect();
                            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                            out.write(dataUpload);
                            out.close();
                        } else {
                            int code = urlConnection.getResponseCode();
                            System.out.println("Response Code : " + code);
                        }
                    } catch (SSLHandshakeException handShake) {
                        urlConnection = getConnection(strUrl, timeout, true, dataUpload, headers);

                    } catch (Exception e) {
                        try {
                            urlConnection.disconnect();
                        } catch (Exception e2) {
                        }
                        throw e;
                    }
                }
            } else {
                try {
                    if (dataUpload != null) {
                        urlConnection.connect();
                        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                        out.write(dataUpload);
                        out.close();
                    }
                } catch (Exception e) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e2) {
                    }
                    throw e;
                }
            }
        } catch (SSLHandshakeException handShake) {
            urlConnection = getConnection(strUrl, timeout, true, dataUpload, headers);
        } catch (Exception e) {
            try {
                urlConnection.disconnect();
            } catch (Exception e2) {
            }
            throw e;
        }
        return urlConnection;
    }

    protected int request(String url, OutputStream output, byte[] dataUpload, Bundle bundle) {
        //int nTimeout = bundle.getInt(GammaKey.RequestTimeout) * 1000;

        System.out.println(url);
        int nTimeout = 0;
        if (nTimeout == 0)
            nTimeout = 10000;

        String strTemp = url.toLowerCase(Locale.US);

        if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
            url = "http://" + url;

        InputStream input = null;
        try {
            m_pUrlConnection = getConnection(url, nTimeout, false, dataUpload, (HashMap<String, String>) bundle.get(Key.RequestHeader));
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            System.out.println(e.toString());
            return CommunicationCode.ConnectionFailed.ordinal();
        }
//        try {
//            printRequestHeader(m_pUrlConnection);
//        } catch (Exception e) {
//
//        }
        try {
            int code = m_pUrlConnection.getResponseCode();

            try {
                HashMap<String, String> responseHeaders = printResponseHeader(m_pUrlConnection);
                bundle.putSerializable(Key.ResponseHeader, responseHeaders);

            } catch (Exception ex1) {

            }

            if (code < 200 || code > 300) {
                //if (code != 200) {
                bundle.putString(Key.ResponseMessage, m_pUrlConnection.getResponseMessage());
                bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
                System.out.println("HTTP Code: " + code);
                return CommunicationCode.ConnectionFailed.ordinal();
            }
        } catch (Exception e) {
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            System.out.println(e.toString());
            return CommunicationCode.ConnectionFailed.ordinal();
        }
        try {
            input = new BufferedInputStream(m_pUrlConnection.getInputStream());
            byte data[] = new byte[1024];
            int count = 0;
            try {
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString(Key.ResponseMessage, e.getMessage());
                bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
                System.out.println(e.toString());
                return CommunicationCode.ConnectionErrorReadInput.ordinal();
            }
            return CommunicationCode.ConnectionSuccess.ordinal();
        } catch (Exception e) {
            e.printStackTrace();
            bundle.putString(Key.ResponseMessage, e.getMessage());
            bundle.putInt(Key.ResponseResult, CommunicationCode.GeneralError.ordinal());
            System.out.println(e.toString());
            return CommunicationCode.ConnectionFailed.ordinal();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (isCancelled() == true)
                return CommunicationCode.ConnectionAborted.ordinal();
        }
    }

    public boolean cancelRequest(boolean mayInterruptIfRunning) {
        try {
            m_pUrlConnection.disconnect();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return this.cancel(mayInterruptIfRunning);
    }
}
