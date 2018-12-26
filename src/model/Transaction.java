/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import blockchain_transactions.Blockchain_Transactions;
import java.security.*;
import java.util.ArrayList;
import util.StringUtil;

/**
 *
 * @author thinh.huynh
 */
public class Transaction {

    public String transactionId; // This is also the hash of the transaction.
    public PublicKey sender; // Senders address/public key.
    public PublicKey reciepient; // Recipients address/public key.
    public float value;
    public byte[] signature; // This is to prevent anybody else from spending funds in our wallet.
    public static float minimumTransaction = 0.1f;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // A rough count of how many transactions have been generated. 

    // Constructor: 
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calulateHash() {
        sequence++; // Increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender)
                + StringUtil.getStringFromKey(reciepient)
                + Float.toString(value) + sequence
        );
    }

    // Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data we signed hasnt been tampered with.
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // Returns true if new transaction could be created.	
    public boolean processTransaction() {

        if (verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : inputs) {
            i.UTXO = Blockchain_Transactions.UTXOs.get(i.transactionOutputId);
        }

        // Check if transaction is valid:
        if (getInputsValue() < minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs:
        float leftOver = getInputsValue() - value; // Get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // Send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // Send the left over 'change' back to sender		

        // Add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            Blockchain_Transactions.UTXOs.put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue; // If Transaction can't be found skip it 
            }
            Blockchain_Transactions.UTXOs.remove(i.UTXO.id);
        } // Delete all spended Transaction-Outputs based on ID in the block-chain network.

        return true;
    }

    // Returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                continue; // If Transaction can't be found skip it 
            }
            total += i.UTXO.value;
        }
        return total;
    }

    // Returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
