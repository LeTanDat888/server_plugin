package com.service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketService {
    private static final HttpClient client = HttpClient.newHttpClient();

    // Hàm lấy giá cổ phiếu (Ví dụ lấy từ một nguồn tin công khai)
    public static String getStockPrice(String symbol) {
        try {
            // Thay URL bằng API thực tế hoặc trang tin tài chính
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.example.com/stock/" + symbol)) 
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Dùng Regex đơn giản để bóc tách giá trị từ JSON/HTML nếu không muốn dùng thư viện JSON
            return parsePrice(response.body(), symbol); 
        } catch (Exception e) {
            return "N/A";
        }
    }

    // Hàm lấy giá vàng (Ví dụ lấy từ một nguồn tin công khai)
    public static String getGoldPrice(String symbol) {
        try {
            // Thay URL bằng API thực tế hoặc trang tin tài chính
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.example.com/stock/" + symbol))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Dùng Regex đơn giản để bóc tách giá trị từ JSON/HTML nếu không muốn dùng thư viện JSON
            return parsePrice(response.body(), symbol);
        } catch (Exception e) {
            return "N/A";
        }
    }

    private static String parsePrice(String body, String symbol) {
        // Logic bóc tách giá trị từ chuỗi trả về
        return "N/A"; // Giá giả định cho NKG
    }
}