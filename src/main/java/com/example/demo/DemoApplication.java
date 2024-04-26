package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@SpringBootApplication
@RestController
public class DemoApplication {
    public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class, args);
    }

    private final Random random = new Random();

    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      int minSleepMillis = 1000; // 1 second
      int maxSleepMillis = 5000; // 5 seconds

      int sleepMillis = random.nextInt(maxSleepMillis - minSleepMillis + 1) + minSleepMillis;
      try {
        Thread.sleep(sleepMillis);
      }
      catch (InterruptedException e) {
          return "Sleep was interrupted";
      }

      return String.format("4.10 Hello %s!", name);
    }
}
