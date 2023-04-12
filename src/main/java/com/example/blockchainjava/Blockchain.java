package com.example.blockchainjava;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.BaseEncoding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Blockchain {

    private List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.createBlock(1, "0");
    }

    public Block createBlock(int proof, String previousHash) {
        Block block = new Block();
        block.setIndex(this.chain.size() + 1);
        block.setTimestamp(new Date().toString());
        block.setPreviousHash(previousHash);
        block.setProof(proof);
        this.chain.add(block);
        return block;
    }

    public Block getPreviousBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    public int proofOfWork(int previousProof) {
        int newProof = 1;
        boolean checkProof = false;
        while (!checkProof) {

            MessageDigest md = null;
            String encodedBlock = "";
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] digest = md.digest(String.valueOf(newProof * newProof - previousProof * previousProof).getBytes());

            String hashOperation = BaseEncoding.base16().lowerCase().encode(digest);
            if (hashOperation.substring(0, 4).equals("0000")) {
                checkProof = true;
            } else {
                newProof += 1;
            }
        }

        return newProof;
    }

    public String hash(Block block) {
        String encodedBlock = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonBlock = ow.writeValueAsString(block);
            byte[] digest = md.digest(String.valueOf(jsonBlock).getBytes());
            encodedBlock = BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return encodedBlock;
    }

    public boolean isChainValid(List<Block> chain) {
        Block previousBlock = chain.get(0);
        int blockIndex = 1;
        while (blockIndex < chain.size()) {
            Block block = chain.get(blockIndex);
            if (!block.getPreviousHash().equals(hash(previousBlock))) {
                return false;
            }
            int previousProof = previousBlock.getProof();
            int proof = block.getProof();


            MessageDigest md = null;
            String encodedBlock = "";
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] digest = md.digest(String.valueOf(proof * proof - previousProof * previousProof).getBytes());

            String hashOperation = BaseEncoding.base16().lowerCase().encode(digest);

            if (!hashOperation.substring(0, 4).equals("0000")) {
                return false;
            }
            previousBlock = block;
            blockIndex += 1;
        }
        return true;
    }
}
