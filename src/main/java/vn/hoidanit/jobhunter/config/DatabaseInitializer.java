package vn.hoidanit.jobhunter.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializer implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        System.out.println("run here");
    }
}
