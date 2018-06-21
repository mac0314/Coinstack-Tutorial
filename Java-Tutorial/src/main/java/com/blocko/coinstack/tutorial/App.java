package com.blocko.coinstack.tutorial;

import java.io.IOException;
import java.security.PublicKey;

import io.blocko.apache.commons.codec.binary.Hex;
import io.blocko.coinstack.AbstractEndpoint;
import io.blocko.coinstack.CoinStackClient;
import io.blocko.coinstack.ECKey;
import io.blocko.coinstack.TransactionBuilder;
import io.blocko.coinstack.TransactionUtil;
import io.blocko.coinstack.exception.CoinStackException;
import io.blocko.coinstack.model.Block;
import io.blocko.coinstack.model.BlockchainStatus;
import io.blocko.coinstack.model.CredentialsProvider;
import io.blocko.coinstack.model.Output;
import io.blocko.coinstack.model.Transaction;

/**
 * Blocko - Coinstack Tutorial!
 *
 */
public class App 
{
	public static CoinStackClient createNewClient(){
		CredentialsProvider credentials = null;
		
        AbstractEndpoint endpoint = new AbstractEndpoint() {
			
			public boolean mainnet() {
				return true;
			}
			
			public PublicKey getPublicKey() {
				return null;
			}
			
			public String endpoint() {
				return "http://172.16.113.131:3000";
			}

		};
		
		CoinStackClient client = new CoinStackClient(credentials, endpoint);
		
		return client;
	}
	
	
    public static void main( String[] args )
    {
        System.out.println( "Coinstack Tutorial!" );
       
        CoinStackClient client = createNewClient();
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
	        long balance = client.getBalance("1ND9HWARQRD88FtP4aVFF1N2B57JFgVBuN");
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
	        long amount = io.blocko.coinstack.Math.convertToSatoshi("0.0001");
	        long fee = io.blocko.coinstack.Math.convertToSatoshi("0.0001");
	        
	        TransactionBuilder builder = new TransactionBuilder();
	        builder.allowDustyOutput(true);
	        builder.shuffleOutputs(false);
	        builder.addOutput(toAddress, amount);
	        builder.setFee(fee);
	        
	        //sign the transaction using the private key
	        String rawSignedTx = builder.buildTransaction(client, "L4NiSb6YLfza4zYz2J13EBegYA8cF2bm9F9wZ2v1TDgTyvf4vaZS");
	        System.out.println("rawSignedTx:: "+rawSignedTx);
	  
	        // send the signed transaction
	        client.sendTransaction(rawSignedTx);
	        
	        String transactionId = TransactionUtil.getTransactionHash(rawSignedTx);
	        // print transaction
	        Transaction tx = client.getTransaction(transactionId);
	        System.out.println(tx.getConfirmationTime());
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoinStackException e) {
			e.printStackTrace();
		}
        
        client.close();
    }
}
