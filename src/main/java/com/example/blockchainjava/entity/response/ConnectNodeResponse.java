package com.example.blockchainjava.entity.response;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.List;

@Getter
@Setter
public class ConnectNodeResponse {
    List<URL> nodes;
    String message;
}
