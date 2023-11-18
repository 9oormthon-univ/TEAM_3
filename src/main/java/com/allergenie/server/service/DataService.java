package com.allergenie.server.service;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.repository.MedicineRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@Transactional
@RequiredArgsConstructor
public class DataService {
    @Value("${api.key.encoded}")
    private String encodedKey;

    @Value("${api.baseURL}")
    private String baseURL;

    private final MedicineRepository medicineRepository;

    public String getDataList(String pageNo) throws IOException {
        StringBuilder reqURL = new StringBuilder(baseURL);
        reqURL.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + encodedKey);


        reqURL.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8")); /*페이지번호*/
        reqURL.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/

//        reqURL.append("&" + URLEncoder.encode("entpName","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체명*/
//        reqURL.append("&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*제품명*/
//        reqURL.append("&" + URLEncoder.encode("itemSeq","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*품목기준코드*/
//        reqURL.append("&" + URLEncoder.encode("efcyQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약의 효능은 무엇입니까?*/
//        reqURL.append("&" + URLEncoder.encode("useMethodQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떻게 사용합니까?*/
//        reqURL.append("&" + URLEncoder.encode("atpnWarnQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약을 사용하기 전에 반드시 알아야 할 내용은 무엇입니까?*/
//        reqURL.append("&" + URLEncoder.encode("atpnQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약의 사용상 주의사항은 무엇입니까?*/
//        reqURL.append("&" + URLEncoder.encode("intrcQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약을 사용하는 동안 주의해야 할 약 또는 음식은 무엇입니까?*/
//        reqURL.append("&" + URLEncoder.encode("seQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떤 이상반응이 나타날 수 있습니까?*/
//        reqURL.append("&" + URLEncoder.encode("depositMethodQesitm","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*이 약은 어떻게 보관해야 합니까?*/
//        reqURL.append("&" + URLEncoder.encode("openDe","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*공개일자*/
//        reqURL.append("&" + URLEncoder.encode("updateDe","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*수정일자*/

        reqURL.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*응답데이터 형식(xml/json) Default:xml*/


        URL url = new URL(reqURL.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        int responseCode = conn.getResponseCode();
        System.out.println("Response code: " + responseCode);

        BufferedReader br;
        if (responseCode >= 200 && responseCode <= 300) { // 정상적인 응답인 경우
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else { // 에러 응답인 경우
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        String line = "";
        String result = "";
        while ((line = br.readLine()) != null) {
            result += line;
        }
        System.out.println(result);

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(result);

        System.out.println(jsonElement);

        JsonObject body = jsonElement.getAsJsonObject().getAsJsonObject("body");
        System.out.println(body);

        JsonArray items = body.getAsJsonArray("items");
        System.out.println(items);

        for (JsonElement item : items) {
            //이름
            String name = item.getAsJsonObject().get("itemName").getAsString();
            System.out.println(name);

            //효능
            JsonElement effectElement = item.getAsJsonObject().get("efcyQesitm");
            String effect = this.refineJsonElement(effectElement);

            //주의사항
            JsonElement cautionElement = item.getAsJsonObject().get("atpnQesitm");
            String caution = this.refineJsonElement(cautionElement);

            //약 사진
            JsonElement imageElement = item.getAsJsonObject().get("itemImage");
            String image = !imageElement.isJsonNull()
                    ? imageElement.getAsString()
                    : null;

            //부작용
            JsonElement sideEffectElement = item.getAsJsonObject().get("seQesitm");
            String sideEffect = this.refineJsonElement(sideEffectElement);

            medicineRepository.save(Medicine.builder()
                    .name(name).effect(effect).caution(caution).image(image)
                    .build());
        }

        br.close();
        conn.disconnect();

        return result;
    }

    public String refineJsonElement(JsonElement jsonElement) {
        String jsonAsString = !jsonElement.isJsonNull()
                ? jsonElement.getAsString()
                : null;
        if (jsonAsString != null) {
            jsonAsString = jsonAsString.replaceAll("\\n\\n", " ");
            jsonAsString = jsonAsString.replaceAll("\\n", " ");
            if (jsonAsString.length() > 256) {
                jsonAsString = jsonAsString.substring(0, 256); // 256자로 자르기
                // 마지막 '.' 이후 부분 제거
                int lastDotIndex = jsonAsString.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    jsonAsString = jsonAsString.substring(0, lastDotIndex);
                }
            }
        }
        return jsonAsString;
    }
}
