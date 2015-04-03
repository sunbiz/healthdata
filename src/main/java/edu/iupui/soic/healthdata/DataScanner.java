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
import org.apache.http.entity.ContentType;
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
    static String mimeType = new String();
    
    public DataScanner() {
        try {
            String data = getData(ALL_DATA_URL);
            JSONArray json = new JSONArray(data);
            for (int i = 0; i < json.length(); i++) {
                String uid = json.getString(i);
                String dataDesc = getData(ALL_DATA_URL + "/" + uid);
                System.out.println("=============================");
                System.out.println("DATA DESC = " + dataDesc);
                JSONObject obj = new JSONObject(dataDesc);
                System.out.println("TITLE = " + obj.get("title") + "DESCRIPTION = " + obj.get("notes"));
                
                JSONArray jsonArray = obj.getJSONArray("resources");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String str = getData(jsonObject.getString("url"));
                System.out.println("data= " + str);
                if (this.mimeType.equals("text/csv") || this.mimeType.equals("text/plain")) {
                    writeToFile(str, new File("D:\\dataFile" + i + ".txt"));
                }
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
        
        System.out.println("URL = " + url);
        System.out.println("Executing request " + httpget.getRequestLine());

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            
            @Override
            public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    ContentType contentType = null;
                    if (entity != null) {
                        contentType = ContentType.get(entity);
                    }
                    DataScanner.mimeType = contentType.getMimeType();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        String responseBody = httpclient.execute(httpget, responseHandler);
        return responseBody;
    }
    
    private void writeToFile(String str, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            file.getParentFile().mkdirs();
            
        }
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(str);
        bw.close();
    }
}
