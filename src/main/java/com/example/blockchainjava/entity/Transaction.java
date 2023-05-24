package com.example.blockchainjava.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    String sender;
    String receiver;
    String amount;
}
