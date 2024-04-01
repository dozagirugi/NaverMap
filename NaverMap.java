package gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class NaverMap {
    static String clientID = "21jznim92m";
    static String secretKey = "HuHCiFO9IJ32ZKQ39rlmKAwGrSxttY3TFhTS3XRd";

    MainFrame mainFrame; // 윈도우 창

    // 생성자에 윈도우 창 객체를 입력함
    public NaverMap(MainFrame mainFrame) throws IOException, ParseException {
        this.mainFrame = mainFrame;

        String address = mainFrame.address.getText();
        System.out.println("네이버 맵에서 주소: " + address);

        StringBuilder urlBuilder = new StringBuilder("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=");
        urlBuilder.append(URLEncoder.encode(address, "UTF-8"));
        // 요청주소에 입력받은 주소가 추가된 문자열 (http 요청은 기본, UTF-8로 인코딩 됨.)

        System.out.println(urlBuilder.toString());

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET"); // get으로 요청
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "21jznim92m"); // <- 요청 헤더에 들어갈 나의 ID
        conn.setRequestProperty("X-NCP-APIGW-API-KEY", "HuHCiFO9IJ32ZKQ39rlmKAwGrSxttY3TFhTS3XRd"); // <- 요청 헤더에 들어갈 나의 KEY

        // HTTP 요청 후 응답코드 (정상, 비정상, 에러 등 코드로 리턴 됨)
        System.out.println("Response Code: " + conn.getResponseCode());
        BufferedReader br;

        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String result = br.readLine(); // 한번에 위도 경도 결과받기

        br.close(); // 읽기 객체 닫기
        conn.disconnect(); // 연결 객체 닫기

        System.out.println(result); // 결과값을 받아왔다 !
        ////

        // 제이슨 심플 라이브러리로 파싱 (받은 문자열에서 필요한 데이터 뽑아내기)
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        JSONArray arr = (JSONArray) jsonObject.get("addresses");

        for (Object one : arr) {
            JSONObject ob = (JSONObject) one;
            System.out.println("도로명 주소: " + ob.get("roadAddress"));
            System.out.println("지번 주소: " + ob.get("jibunAddress"));
            System.out.println("경도: " + ob.get("x"));
            System.out.println("위도: " + ob.get("y"));

            AddressVO vo = new AddressVO(); // 어드레스 객체 생성
            vo.setRoadAddress((String) ob.get("roadAddress")); // 도로명 주소 입력
            vo.setRoadAddress((String) ob.get("jibunAddress")); // 지번 주소 입력
            vo.setX((String) ob.get("x")); // 경도 입력
            vo.setY((String) ob.get("y")); // 위도 입력


            mapService(vo); // 경도, 위도, 주소를 입력하여 지도 이미지를 가져옴
        }
    }

    private void mapService(AddressVO vo) throws IOException {
        // 네이버 staticMap 요청 주소
        String mapUrl = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";
        String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");

        mapUrl += "center=" + vo.getX() + "," + vo.getY();
        mapUrl += "&level=16&w=700&h=500";
        mapUrl += "&markers=type:t|size:mid|pos:" + pos + "|label:" + URLEncoder.encode(vo.getRoadAddress(), "UTF-8");
        URL url = new URL(mapUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "21jznim92m");
        conn.setRequestProperty("X-NCP-APIGW-API-KEY", "HuHCiFO9IJ32ZKQ39rlmKAwGrSxttY3TFhTS3XRd");

        int responseCode = conn.getResponseCode(); // 상태코드

        BufferedReader br;

        if (responseCode == 200) {
            InputStream is = conn.getInputStream();
            Image image = ImageIO.read(is);
            is.close();
            ImageIcon imageIcon = new ImageIcon(image);
            mainFrame.imageLabel.setIcon(imageIcon);
            mainFrame.resAddress.setText(vo.getRoadAddress());
            mainFrame.jibunAddress.setText(vo.getJibunAddress());
            mainFrame.resX.setText(vo.getX());
            mainFrame.resY.setText(vo.getY());
        } else { // 에러발생시
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            conn.disconnect();
            System.out.println(response.toString());
        }
    }
}
