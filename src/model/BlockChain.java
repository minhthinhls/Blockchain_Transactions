/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static blockchain_transactions.Blockchain_Transactions.genesisTransaction;
import java.util.ArrayList;
import java.util.HashMap;

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
            this.generateGenesisBlock(addedBlock);
        } else {
            addedBlock.previousHash = this.getCurrentBlock().hash;
            System.out.println("Trying to mine Block number: " + chain.size());
            try {
                addedBlock.mineBlock(this.difficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.chain.add(addedBlock);
            System.out.println("Success adding Block number: " + (chain.size() - 1) + "\n");
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

    public void generateGenesisBlock(Block genesisBlock) throws Exception {
        if (this.chain.isEmpty()) {
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

    /*
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
     */
    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // A temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // Loop through blockchain to check hashes:
        for (int i = 1; i < chain.size(); i++) {

            currentBlock = chain.get(i);
            previousBlock = chain.get(i - 1);
            // Compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            // Compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            // Check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            // Loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid: true");
        return true;
    }

}
