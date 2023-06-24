package com.example.blockchainjava.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    @NotBlank(message = "Sender is mandatory")
    String sender;
    @NotBlank(message = "Receiver is mandatory")
    String receiver;
    @Min(value = 0, message = "The value must be positive")
    @NotNull(message = "Amount is mandatory")
    Integer amount;
}
