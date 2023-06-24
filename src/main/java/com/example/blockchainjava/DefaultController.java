package com.example.blockchainjava;

import com.example.blockchainjava.entity.Block;
import com.example.blockchainjava.entity.Blockchain;
import com.example.blockchainjava.entity.ChainResponse;
import com.example.blockchainjava.entity.Transaction;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("transaction")
    public ResponseEntity<?> add(@RequestBody @Valid Transaction transaction){
        int index = blockchain.addTransaction(transaction);
        String message = "This transaction will be added to Block: " + index;
        return ResponseEntity.ok(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
