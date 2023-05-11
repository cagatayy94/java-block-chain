package com.example.blockchainjava;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class DefaultController {

    Blockchain blockchain = new Blockchain();

    @GetMapping("mine_block")
    public ResponseEntity<?> mineBlock(){
        Block previousBlock = blockchain.getPreviousBlock();
        int previousProof = previousBlock.getProof();
        int proof = blockchain.proofOfWork(previousProof);
        String previousHash = blockchain.hash(previousBlock);
        Block block = blockchain.createBlock(proof, previousHash);
        return ResponseEntity.ok(block.mapToJson());
    }

    @GetMapping("get_chain")
    public ResponseEntity<?> getChain(){
        return ResponseEntity.ok(blockchain.chain);
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
}
