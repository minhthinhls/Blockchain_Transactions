/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import blockchain_transactions.Blockchain_Transactions;

/**
 *
 * @author thinh.huynh
 */
public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // Only UTXOs owned by this wallet.

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   // 256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : Blockchain_Transactions.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(this.publicKey)) { // If output belongs to me ( if coins belong to me )
                this.UTXOs.put(UTXO.id, UTXO); // Add it to our list of unspent transactions.
                total += UTXO.value;
            }
        }
        return total;
    }

    // Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient, float value) {
        if (getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        // Create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : this.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue(); // Get each unspend Transaction-Output that this wallet owned.
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id)); // Add each transactionOutputId into a whole new list of Transaction-Input.
            if (total > value) {
                break;
            } // Stop immediately if we reach enough money from wallet's unspent Transactions-Output.
        }

        Transaction newTransaction = new Transaction(this.publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(this.privateKey);

        for (TransactionInput input : inputs) {
            this.UTXOs.remove(input.transactionOutputId);
        } // Delete all spended Transaction-Output based on ID in the wallet.

        return newTransaction;
    }
}
