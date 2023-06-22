package com.example.blockchainjava.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.BaseEncoding;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Blockchain {

    public List<Block> chain;
    public List<Transaction> transactions;
    public Collection<URL> nodes;

    public Blockchain() {
        chain = new ArrayList<>();
        createBlock(1, "0");

        if (this.transactions != null){
            this.transactions.clear();
        }
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

    public int addTransactions(String sender, String receiver, String amount){
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setReceiver(receiver);
        transaction.setSender(sender);
        transactions.add(transaction);
        return getPreviousBlock().getIndex()+1;
    }

    public void addNode(String address){
        try {
            URL url = new URL(address);
            if (this.nodes.stream().noneMatch(x -> x == url)){
                this.nodes.add(url);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void replaceChain(){
        Collection<URL> nodes = this.nodes;
        Object longestChain = null;
        Long maxLength = (long) this.chain.size();

        nodes.forEach(url -> {
            String address = url.getAuthority()+"/api/chain";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(address, String.class);

            if (response.getStatusCode() == HttpStatusCode.valueOf(200)){
                System.out.println(response.getBody());
            }

        });


        
    }
}
