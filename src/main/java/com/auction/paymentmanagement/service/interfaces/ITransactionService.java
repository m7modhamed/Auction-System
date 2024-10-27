package com.auction.paymentmanagement.service.interfaces;

import com.auction.paymentmanagement.model.Transaction;

public interface ITransactionService {

    Transaction saveTransaction(Transaction transaction);
}
