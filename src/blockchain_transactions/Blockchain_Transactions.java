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
import model.Block;
import model.Transaction;
import model.Wallet;
import util.StringUtil;

/**
 *
 * @author thinh.huynh
 */
public class Blockchain_Transactions {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
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
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5/*, null*/);
        transaction.generateSignature(walletA.privateKey);
        // Verify the signature works and verify it from the public key
        System.out.println("\n*Is signature verified: " + transaction.verifiySignature());
    }

}
