package com.blocko.coinstack.tutorial;

import java.io.IOException;

import org.apache.commons.codec.binary.Hex;

import io.blocko.coinstack.*;
import io.blocko.coinstack.exception.CoinStackException;
import io.blocko.coinstack.model.*;

/**
 * Blocko - Coinstack Tutorial!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Coinstack Tutorial!" );
        
        CoinStackClient client = new CoinStackClient(new CredentialsProvider() {
        	  @Override
        	  public String getAccessKey() {
        	    return "YOUR_COINSTACK_ACCESS_KEY";
        	  }
        	  @Override
        	  public String getSecretKey() {
        	    return "YOUR_COINSTACK_SECRET_KEY";
        	  }
        	}, Endpoint.MAINNET);
        
        BlockchainStatus status;
        
		try {
			status = client.getBlockchainStatus();
	        System.out.println("bestHeight: "+ status.getBestHeight());
	        System.out.println("bestBlockHash: "+ status.getBestBlockHash());
	        
	        String hash = status.getBestBlockHash();
	        
	        Block block = client.getBlock(hash);
	        
	        System.out.println("blockId: " + block.getBlockId());
	        System.out.println("parentId: " + block.getParentId());
	        System.out.println("height: " + block.getHeight());
	        System.out.println("time: " + block.getBlockConfirmationTime());
	        
	        // create a new private key
	        String newPrivateKeyWIF = ECKey.createNewPrivateKey();
	        System.out.println("private key: " + newPrivateKeyWIF);
	        
	        // derive a public key
	        boolean check = false;
	        String newPublicKey = Hex.encodeHexString(ECKey.derivePubKey(newPrivateKeyWIF, check));
	        System.out.println("public key: " + newPublicKey);
	        
	        // derive an address
	        String your_wallet_address = ECKey.deriveAddress(newPrivateKeyWIF);
	        System.out.println("address: " + your_wallet_address);
	        
	        // get a remaining balance
	        long balance = client.getBalance(your_wallet_address);
	        System.out.println("balance: " + balance);
	        
	        // print all transactions of a given wallet address
	        String[] transactionIds = client.getTransactions(your_wallet_address);
	        System.out.println("transactions");
	        for (String txId : transactionIds) {
	          System.out.println("txIds[]: " + txId);
	        }
	        
	        // print all utxos
	        Output[] outputs = client.getUnspentOutputs(your_wallet_address);
	        System.out.println("unspent outputs"); 
	        for (Output utxo: outputs) { 
	            System.out.println(utxo.getValue());
	        }
	        
	        // create a target address to send
	        String toPrivateKeyWIF = ECKey.createNewPrivateKey();
	        String toAddress = ECKey.deriveAddress(toPrivateKeyWIF);
	        
	        // create a transaction
	        long amount = io.blocko.coinstack.Math.convertToSatoshi("0.0002");
	        long fee = io.blocko.coinstack.Math.convertToSatoshi("0.0001");
	        
	        TransactionBuilder builder = new TransactionBuilder();
	        builder.addOutput(toAddress, amount);
	        builder.setFee(fee);
	        
	        // sign the transaction using the private key
	        String rawSignedTx = client.createSignedTransaction(builder, newPrivateKeyWIF);
	        System.out.println(rawSignedTx);
	        
	        // send the signed transaction
	        client.sendTransaction(rawSignedTx);
	        
	        String transactionId = TransactionUtil.getTransactionHash(rawSignedTx);
	        // print transaction
	        Transaction tx = client.getTransaction(transactionId);
	        System.out.println(tx.getConfirmationTime());
	        
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoinStackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        client.close();
    }
}
