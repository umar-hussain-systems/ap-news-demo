package com.systems.demo.apnewsdemo.rest.client;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Pathtesting {

    public static void main(String[] args) {
        Path path = Paths.get(System.getProperty("user.dir"));
        try(DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {

            for(Path path1 : paths) {
                log.info(path1.toString());
            }

            StringBuilder downloadlink = new StringBuilder()
                .append("/document")
                .append("/download")
                .append("/")
                .append("") // documentType
                .append("/company") // documentType
                .append("/") // documentType
                .append("") // companyId
                .append("/user")
                .append("/")
                .append(""); //userid

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
