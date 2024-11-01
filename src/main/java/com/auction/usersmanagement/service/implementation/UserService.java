package com.auction.usersmanagement.service.implementation;

import com.auction.usersmanagement.model.User;
import com.auction.usersmanagement.repository.UserRepository;
import com.auction.usersmanagement.service.interfaces.IUserService;
import com.auction.common.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);

    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new AppException("User Not Found", HttpStatus.NOT_FOUND);
        }

        return user.get();
    }

    public List<User> findAllByAuctionId(long auctionId){
        return userRepository.findAllByAuctionId(auctionId);
    }
}
