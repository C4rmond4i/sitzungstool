package com.integra.sitzungstool.general;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.integra.sitzungstool.model.Integraner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerCommunication {
   private static OkHttpClient client;
   
   public static boolean vorstandLogin(String username, String password) {
       try {
            ServerCommunication.client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthenticationInterceptor(username, password))
                    .build();
            Request request = new Request.Builder()
                    .url("https://integranet-dev.integra-ev.de/module/sitzungsanwesenheit/api/anwesenheit-api.php?method=login")
                    .build();
            Response response = ServerCommunication.client.newCall(request).execute();
            return response.body().string().equals("Verified");
       } catch (IOException e) {
           System.out.println(e.getMessage());
           return false;
       }
   }
   
   public static ArrayList<Integraner> getIntegraner() {
       if (ServerCommunication.client != null) {
            try {
                Request request = new Request.Builder()
                    .url("https://integranet-dev.integra-ev.de/module/sitzungsanwesenheit/api/anwesenheit-api.php?method=users")
                    .build();
                Response response = ServerCommunication.client.newCall(request).execute();
                String body = response.body().string();
                Gson gson = new Gson();
                ArrayList<Integraner> integraner = gson.fromJson(body, new TypeToken<List<Integraner>>(){}.getType());
                return integraner;
            } catch (JsonSyntaxException | IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return new ArrayList<>();
            }
       }
       return new ArrayList<>();
   }
   
   public static String getProfilePicture(String benutzerkennung) {
       if (ServerCommunication.client != null) {
           try {
                Request request = new Request.Builder()
                    .url("https://integranet-dev.integra-ev.de/module/sitzungsanwesenheit/api/anwesenheit-api.php?method=bild&id=" + benutzerkennung)
                    .build();
                Response response = ServerCommunication.client.newCall(request).execute();
                String body = response.body().string();
                if (body.startsWith("data:image/")) {
                    String[] imageStrings = body.split(",");
                    if (imageStrings.length > 1) {
                        return imageStrings[1];
                    }
                    return null;
                }
                System.out.println(body);
                return null;
           } catch (IOException e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return null;
            }
       }
       return null;
   }
}