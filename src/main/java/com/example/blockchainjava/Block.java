package com.example.blockchainjava;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Block {
    public int index;
    public String timestamp;
    public int proof;
    public String previousHash;
}
