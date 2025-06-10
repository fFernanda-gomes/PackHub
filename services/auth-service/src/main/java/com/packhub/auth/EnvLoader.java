package com.packhub.auth;
import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./infra/env") // ou onde estiver seu .env
                .filename(".env")
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}