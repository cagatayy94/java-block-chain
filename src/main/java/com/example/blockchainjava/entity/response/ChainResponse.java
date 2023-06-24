package com.example.blockchainjava.entity.response;

import com.example.blockchainjava.entity.Block;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChainResponse {
    public List<Block> chain;
    public Long length;
}
