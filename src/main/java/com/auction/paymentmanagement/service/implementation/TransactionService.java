package com.auction.paymentmanagement.service.implementation;

import com.auction.paymentmanagement.model.Transaction;
import com.auction.paymentmanagement.repository.TransactionRepository;
import com.auction.paymentmanagement.service.interfaces.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;


    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
