/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain_transactions;

import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import model.*;

/**
 *
 * @author thinh.huynh
 */
public class Blockchain_Transactions {

    // public static ArrayList<Block> blockchain = new ArrayList<Block>();
    // public static int difficulty = 4;
    // public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // List of all unspent transactions.

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        BlockChain blockchain = new BlockChain(4);
        /*
        // Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        // Test public and private keys
        System.out.println("*Private and public keys: \n");
        System.out.println("Private key: " + StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println("Public key: " + StringUtil.getStringFromKey(walletA.publicKey));
        // Create a test transaction from WalletA to walletB 
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);
        // Verify the signature works and verify it from the public key
        System.out.println("\n*Is signature verified: " + transaction.verifiySignature());
         */
        
        // Add our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup Bouncey castle as a Security Provider

        // Create wallets:
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction, which sends 100 Coin to walletA: 
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey); // Manually sign the genesis transaction	
        genesisTransaction.transactionId = "0"; // Manually set the transaction id by 0
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); // Manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // Its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("Genesis");
        genesis.addTransaction(genesisTransaction);
        blockchain.addBlock(genesis);

        // Testing
        Block block1 = new Block();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        blockchain.addBlock(block1);
        showBalance();

        Block block2 = new Block();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        System.out.println("\nWalletA is Attempting to send funds (10) to WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 10f));
        blockchain.addBlock(block2);
        showBalance();

        Block block3 = new Block();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        blockchain.addBlock(block3);
        showBalance();

        blockchain.isChainValid();
        
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }
    
    public static void showBalance() {
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
    }
    
}
