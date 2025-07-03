package ma.PFA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TranslationController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/translate")
    public Map<String, String> translate(@RequestBody Map<String, String> request) {
        String originalText = request.get("text");

        // Appel de LibreTranslate API
        String translatedText = translateWithLibreTranslate(originalText);

        Map<String, String> response = new HashMap<>();
        response.put("translatedText", translatedText);
        return response;
    }

    private String translateWithLibreTranslate(String text) {
        String url = "https://libretranslate.de/translate";

        Map<String, Object> payload = new HashMap<>();
        payload.put("q", text);
        payload.put("source", "fr");
        payload.put("target", "en");
        payload.put("format", "text");

        Map response = restTemplate.postForObject(url, payload, Map.class);

        System.out.println("LibreTranslate response: " + response);

        return (String) response.get("translatedText");
    }

}
