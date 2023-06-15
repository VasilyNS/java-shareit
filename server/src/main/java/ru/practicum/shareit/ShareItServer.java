package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {

    public static void main(String[] args) {

        if (args.length != 0 && args[0].equals("test")) {
            System.out.println("ShareItServer test");
        } else {
            SpringApplication.run(ShareItServer.class, args);
        }

    }

}
