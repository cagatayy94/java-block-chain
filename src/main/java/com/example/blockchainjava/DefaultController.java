package com.example.blockchainjava;

import com.example.blockchainjava.entity.Block;
import com.example.blockchainjava.entity.Blockchain;
import com.example.blockchainjava.entity.response.ChainReplaceResponse;
import com.example.blockchainjava.entity.response.ChainResponse;
import com.example.blockchainjava.entity.Transaction;
import com.example.blockchainjava.entity.response.ConnectNodeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.*;

@RequestMapping("/api/")
@RestController
public class DefaultController extends ControllerAdvice{

    Blockchain blockchain = new Blockchain();
    UUID nodeAddress = UUID.randomUUID();

    @GetMapping("mine_block")
    public ResponseEntity<?> mineBlock(){
        Block previousBlock = blockchain.getPreviousBlock();
        int previousProof = previousBlock.getProof();
        int proof = blockchain.proofOfWork(previousProof);
        String previousHash = blockchain.hash(previousBlock);
        Transaction transaction = new Transaction();
        transaction.setSender(nodeAddress.toString());
        transaction.setReceiver("Cagatay");
        transaction.setAmount(10);

        blockchain.addTransaction(transaction);

        List<Transaction> transactions = previousBlock.getTransactions();
        Block block = blockchain.createBlock(proof, previousHash, transactions);
        return ResponseEntity.ok(block.mapToJson());
    }

    @GetMapping("chain")
    public ResponseEntity<ChainResponse> getChain(){
        ChainResponse chainResponse = new ChainResponse();
        chainResponse.setChain(blockchain.chain);
        chainResponse.setLength((long) blockchain.chain.size());
        return ResponseEntity.ok(chainResponse);
    }

    @GetMapping("is_valid")
    public ResponseEntity<?> isValid(){
        boolean isValid = blockchain.isChainValid(blockchain.chain);

        if (isValid){
            return ResponseEntity.ok("is valid");
        }else {
            return ResponseEntity.ok("not valid");
        }
    }

    @PostMapping("add_transaction")
    public ResponseEntity<?> addTransaction(@RequestBody @Valid Transaction transaction){
        int index = blockchain.addTransaction(transaction);
        String message = "This transaction will be added to Block: " + index;
        return ResponseEntity.ok(message);
    }

    @PostMapping("connect_node")
    public ResponseEntity<ConnectNodeResponse> connectNode(@RequestBody @Valid List<URL> nodes){
        ConnectNodeResponse response = new ConnectNodeResponse();

        if (nodes == null){
            response.setMessage("No node found on this request");
            return ResponseEntity.badRequest().body(response);
        }

        for (URL node: nodes) {
            blockchain.addNode(node);
        }

        response.setMessage("All nodes are now connected The Cacoin now contains the following nodes: ");
        response.setNodes(nodes);

        return ResponseEntity.ok(response) ;
    }

    @GetMapping("replace_chain")
    public ResponseEntity<ChainReplaceResponse> replaceChain(){
        boolean isReplaced = blockchain.replaceChain();
        ChainReplaceResponse chainReplaceResponse = new ChainReplaceResponse();
        if (isReplaced){
            chainReplaceResponse.setMessage("The nodes had different chain so the chain was replaced by longest chain.");
            chainReplaceResponse.setChain(blockchain.chain);
        }else {
            chainReplaceResponse.setMessage("All good chain is the largest one.");
        }
        return ResponseEntity.ok().body(chainReplaceResponse);
    }
}
