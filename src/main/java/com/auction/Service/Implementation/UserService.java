package com.auction.Service.Implementation;

import com.auction.Entity.User;
import com.auction.Repository.UserRepository;
import com.auction.Service.Interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);

    }
}
