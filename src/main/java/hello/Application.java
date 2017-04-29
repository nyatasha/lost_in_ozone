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
            dataController.calcFlight("SVO","NCE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
