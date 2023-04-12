package com.example.blockchainjava;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.common.io.BaseEncoding;

public class Blockchain {

    public List<Block> chain;

    public Blockchain() {
        chain = new ArrayList<>();
        createBlock(1, "0");
    }

    public Block createBlock(int proof, String previousHash) {
        Block block = new Block();
        block.setIndex(chain.size() + 1);
        block.setTimestamp(new Date().toString());
        block.setPreviousHash(previousHash);
        block.setProof(proof);
        chain.add(block);
        return block;
    }

    public Block getPreviousBlock() {
        return chain.get(chain.size() - 1);
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
            byte[] digest = md.digest(String.valueOf(block.mapToJson()).getBytes());
            encodedBlock = BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
