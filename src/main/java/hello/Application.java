package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        DataController dataController = new DataController();
        try {
            dataController.calcFlight("MSQ","GIG");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
