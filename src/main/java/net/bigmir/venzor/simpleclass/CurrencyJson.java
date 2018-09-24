package net.bigmir.venzor.simpleclass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.bigmir.venzor.entities.Currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CurrencyJson {
    private HashMap<String, Double> rates;

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public static List<Currency> initCurrencies() {
        String request = "http://data.fixer.io/api/latest?access_key=key";
        String result = null;
        try {
            result = performRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        CurrencyJson cur = gson.fromJson(result, CurrencyJson.class);
        double curUAH = cur.getRates().get("UAH");
        List<Currency> resultList = new LinkedList<>();
        for (Map.Entry<String, Double> entry : cur.getRates().entrySet()) {
            double rateBuy = ((1 / entry.getValue()) * curUAH) * 0.98;
            double rateSale = ((1 / entry.getValue()) * curUAH) * 1.02;
            resultList.add(new Currency(entry.getKey(), rateBuy, rateSale));
        }
        return resultList;
    }




    public static String performRequest(String urlAddress) throws IOException {
        String result = "";
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String temp = "";
                for (; (temp = br.readLine()) != null; ) {
                    result += temp;
                    result += System.lineSeparator();
                }
            }
        } catch (IOException e) {
            throw e;
        }
        return result;
    }

}
