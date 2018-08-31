/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.security.PublicKey;
import util.StringUtil;

/**
 *
 * @author thinh.huynh
 */
public class TransactionOutput {

    public String id;
    public PublicKey reciepient; // Also known as the new owner of these coins.
    public float value; // The amount of coins they own
    public String parentTransactionId; // The id of the transaction this output was created in

    // Constructor
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
    }

    // Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
