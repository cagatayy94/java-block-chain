package com.example.blockchainjava.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    @NotBlank(message = "Sender is mandatory")
    String sender;
    @NotBlank(message = "Receiver is mandatory")
    String receiver;
    @NotBlank(message = "Amount is mandatory")
    String amount;
}
