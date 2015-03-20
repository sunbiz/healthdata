package edu.iupui.soic.healthdata;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Manika
 */
public class DataScanner {

    final String ALL_DATA_URL = "http://hub.healthdata.gov/api/2/rest/dataset";

    public DataScanner() {
        BufferedWriter bw = null;
        try {
            String data = getData(ALL_DATA_URL);
            JSONArray json = new JSONArray(data);
            for (int i = 0; i < 3; i++) {
                String uid = json.getString(i);
                String dataDesc = getData(ALL_DATA_URL + "/" + uid);
                System.out.println("=============================");
                JSONObject obj = new JSONObject(dataDesc);
                String myContent = ("TITLE = " + obj.get("title")) + ("DESCRIPTION = " + obj.get("notes"));
                File file = new File("C:/myfile.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(myContent);
                System.out.println("File written Successfully");

                JSONArray jsonArray = obj.getJSONArray("resources");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                System.out.println("data= " + getData(jsonObject.getString("url")));
                System.out.println("=============================");

            }

        } catch (IOException ex) {
            Logger.getLogger(DataScanner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public final static void main(String[] args) throws Exception {
        DataScanner ds = new DataScanner();
    }

    private String getData(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        System.out.println("Executing request " + httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        String responseBody = httpclient.execute(httpget, responseHandler);
        return responseBody;
    }

}
