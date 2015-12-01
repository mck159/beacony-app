package com.example.maciek.beacony.services;

import android.content.Context;

import com.example.maciek.beacony.dto.ContentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * Created by maciek on 2015-11-15.
 */
public class ReqService {
    public ArrayList<ContentDTO> requestForContent(Context ctx, String uuid, String major, String minor) throws SocketTimeoutException {
        Settings settings = SettingsHelper.loadSettings(ctx);
        String json = requestForContentString(settings.getServiceUrl(), uuid, major,minor);
        if(json == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ContentDTO[] contentDTO = objectMapper.readValue(json, ContentDTO[].class);
            ArrayList<ContentDTO> result = new ArrayList<>();
            for(ContentDTO contentDTO1 : contentDTO) {
                result.add(contentDTO1);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String requestForContentString(String servicesUrl, String uuid, String major, String minor) throws SocketTimeoutException {

        try {
            URL obj = new URL(String.format("%s/beacon/%s/content", servicesUrl, getSum(uuid, major, minor)));
//            obj = new URL("http://192.168.169.103:3000/dupa");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
                return response.toString();
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if(e instanceof SocketTimeoutException) {
                throw (SocketTimeoutException) e;
            }
        }
        return null;
    }

    private String getSum(String uuid, String major, String minor) {
        String toCode = String.format("%s#%s#%s", uuid, major, minor);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte utf8_bytes[] = toCode.getBytes();
            digest.update(utf8_bytes,0,utf8_bytes.length);
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
