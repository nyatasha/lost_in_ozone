package hello;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class MainController {

    @RequestMapping("/")
    public String home(){
        return "home";
    }
    public double calcRadiation(double lat, double lon, double height){
        double radiation = 0;
        return radiation;
    }
    public String createJsonResult(double[][] coordsArray){
        JSONObject results = new JSONObject();
        try {
            JSONArray coords = new JSONArray ();
            for(int i = 0; i < coordsArray.length;i++){
                coords.put(String.valueOf(coordsArray[0][i]));
                coords.put(String.valueOf(coordsArray[1][i]));
                results.put(String.valueOf(calcRadiation(coordsArray[0][i],coordsArray[1][i],10)), coords);
            }
        } catch (Exception jse) {
            return "Error: " + jse.toString();
        }
        return results.toString();
    }
}