package com.auction.usersmanagement.service.interfaces;

import com.auction.usersmanagement.model.User;

import java.util.List;

public interface IUserService {
    User save(User user);

    User getUserById(Long userId);
    List<User> findAllByAuctionId(long auctionId);
}
