package com.example.screenmatch;

import com.example.screenmatch.principal.Principal;
import com.example.screenmatch.repositorio.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

}
