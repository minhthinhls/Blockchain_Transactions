/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author thinh.huynh
 */
public class BlockChain {

    public ArrayList<Block> chain = new ArrayList<Block>();
    public int difficulty;
    public String invalidReason;
    
    public BlockChain() {
        this.difficulty = 0;
    }

    public BlockChain(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    public void setChain(ArrayList<Block> chain) {
        this.chain = chain;
    }
    
    public Block getGenesisBlock() {
        return chain.get(0);
    }
    
    public Block getCurrentBlock() {
        return chain.get(chain.size() - 1);
    }
    
    public Block getPreviousBlock() {
        return chain.get(chain.size() - 2);
    }
    
    public void addBlock(Block addedBlock) throws Exception {
        if (this.chain.isEmpty()) {
            this.generateGenesisBlock();
        } else {
            addedBlock.previousHash = this.getCurrentBlock().hash;
            System.out.println("Trying to mine Block number: " + chain.size());
            try {
                addedBlock.mineBlock(this.difficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.chain.add(addedBlock);
            System.out.println("Success adding Block number: " + (chain.size()-1) + "\n");
        }
    }
    
    public void generateGenesisBlock() throws Exception {
        if (this.chain.isEmpty()) {
            Block genesisBlock = new Block("This is Genesis Block");
            System.out.println("Trying to mine Genesis Block...");
            try {
                genesisBlock.mineBlock(this.difficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.chain.add(genesisBlock);
            System.out.println("Success adding Genesis Block !" + "\n");
        } else {
            throw new Exception("Genesis Block has already been created !!!");
        }
    }

    public Boolean isChainValid() {
        Block currentBlock = new Block();
        Block previousBlock = new Block();
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Loop through chain to check hashes:
        for (int i = 1; i < chain.size(); i++) {
            currentBlock = chain.get(i);
            previousBlock = chain.get(i - 1);
            // Compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                invalidReason = "Current Hashes not equal";
                return false;
            }
            // Compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                invalidReason = "Previous Hashes not equal";
                return false;
            }
            // Check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                invalidReason = "This block hasn't been mined";
                return false;
            }
        }
        return true;
    }
    
}
