package com.example.blockchainjava.entity;

import java.net.URL;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.blockchainjava.entity.response.ChainResponse;
import com.google.common.io.BaseEncoding;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Blockchain {

    public List<Block> chain;
    public List<Transaction> transactions;
    public Collection<URL> nodes;
    public String miner;

    public void setMiner(String miner) {
        this.miner = miner;
    }



    UUID nodeAddress = UUID.randomUUID();

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.nodes = new ArrayList<>();
        createBlock(1, "0", transactions);
    }

    public Block createBlock(int proof, String previousHash, List<Transaction> transactions) {
        Block block = new Block();
        block.setIndex(chain.size() + 1);
        block.setTimestamp(new Date().toString());
        block.setPreviousHash(previousHash);
        block.setProof(proof);
        block.setTransactions(new ArrayList(transactions));
        chain.add(block);
        return block;
    }

    public Block getPreviousBlock() {
        return chain.get(chain.size() - 1);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public int proofOfWork(int previousProof) {
        int newProof = 1;
        boolean checkProof = false;
        while (!checkProof) {

            MessageDigest md = getMessageDigest();
            byte[] digest = md.digest(String.valueOf(newProof * newProof - previousProof * previousProof).getBytes());
            String hashOperation = BaseEncoding.base16().lowerCase().encode(digest);

            if (hashOperation.startsWith("0000")) {
                checkProof = true;
            } else {
                newProof += 1;
            }
        }

        return newProof;
    }

    public MessageDigest getMessageDigest(){
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String hash(Block block) {
        MessageDigest md = getMessageDigest();
        byte[] digest = md.digest(String.valueOf(block.mapToJson()).getBytes());
        String encodedBlock = BaseEncoding.base16().lowerCase().encode(digest);
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


            MessageDigest md = getMessageDigest();
            byte[] digest = md.digest(String.valueOf(proof * proof - previousProof * previousProof).getBytes());
            String hashOperation = BaseEncoding.base16().lowerCase().encode(digest);

            if (!hashOperation.startsWith("0000")) {
                return false;
            }
            previousBlock = block;
            blockIndex += 1;
        }
        return true;
    }

    public int addTransaction(Transaction transaction){
        transactions.add(transaction);
        return getPreviousBlock().getIndex()+1;
    }

    public void addNode(URL url){
        if (this.nodes.isEmpty() || this.nodes.stream().noneMatch(x -> x == url)){
            this.nodes.add(url);
        }
    }

    public boolean replaceChain(){
        Collection<URL> nodes = this.nodes;
        List<Block> longestChain = null;
        Long maxLength = (long) this.chain.size();
        Long length;

        for (URL url:nodes) {
            String address = url.toString()+"/api/chain";

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ChainResponse> response = restTemplate.getForEntity(address, ChainResponse.class);

            if (response.getStatusCode() == HttpStatusCode.valueOf(200)){
                length = response.getBody().length;
                chain = response.getBody().chain;
                if (length > maxLength && isChainValid(chain)){
                    maxLength = length;
                    this.chain = response.getBody().chain;
                }
            }
        }

        if (longestChain != null){
            this.chain = longestChain;
            return true;
        }

        return false;
    }

    public void setMinerFee(List<Transaction> transactions){
        Transaction transaction = new Transaction();
        transaction.setSender(nodeAddress.toString());
        transaction.setReceiver(this.miner);
        transaction.setAmount(10);
        transactions.add(transaction);
    }

}
