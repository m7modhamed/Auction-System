package com.auction.common.utility;

import com.auction.auctionmanagement.model.Category;
import com.auction.auctionmanagement.repository.CategoryRepository;
import com.auction.usersmanagement.model.Moderator;
import com.auction.usersmanagement.model.Role;
import com.auction.usersmanagement.repository.ModeratorRepository;
import com.auction.usersmanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ModeratorRepository moderatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args)  {
        insertRolesInDatabase();
        insertCategoriesInDatabase();
    }

    private void insertCategoriesInDatabase() {
        Category mobileCategory;
        Category electronicsCategory;
        Category booksCategory;

        // Check if Mobile category exists, create if not
        Optional<Category> mobileOptional = categoryRepository.findByName("Mobile");
        if (mobileOptional.isEmpty()) {
            mobileCategory = new Category();
            mobileCategory.setName("Mobile");
            mobileCategory.setDescription("Category for mobile phones and accessories.");
            mobileCategory.setAttributes(List.of("Brand", "Model", "Screen Size", "Battery Life"));
            mobileCategory.setImageUrl("https://images.pexels.com/photos/3756879/pexels-photo-3756879.jpeg?auto=compress&cs=tinysrgb&w=600");
            categoryRepository.save(mobileCategory);
        }

        // Check if Electronics category exists, create if not
        Optional<Category> electronicsOptional = categoryRepository.findByName("Electronics");
        if (electronicsOptional.isEmpty()) {
            electronicsCategory = new Category();
            electronicsCategory.setName("Electronics");
            electronicsCategory.setDescription("Category for general electronic devices.");
            electronicsCategory.setAttributes(List.of("Brand", "Warranty", "Power Source", "Connectivity"));
            categoryRepository.save(electronicsCategory);
        }

        // Check if Books category exists, create if not
        Optional<Category> booksOptional = categoryRepository.findByName("Books");
        if (booksOptional.isEmpty()) {
            booksCategory = new Category();
            booksCategory.setName("Books");
            booksCategory.setDescription("Category for books across various genres.");
            booksCategory.setAttributes(List.of("Author", "Genre", "Publisher", "ISBN"));
            categoryRepository.save(booksCategory);
        }

    }



    private void insertRolesInDatabase(){

        Role moderatorRole;
        Role adminRole;
        Role userRole;

        Optional<Role> roleModeratorOptional = roleRepository.findByName("ROLE_MODERATOR");
        if (roleModeratorOptional.isEmpty()) {
            moderatorRole = new Role();
            moderatorRole.setName("ROLE_MODERATOR");
            moderatorRole.setDescription("Moderator role");
            roleRepository.save(moderatorRole);
        }else{
            moderatorRole=roleModeratorOptional.get();
        }

        Optional<Role> roleAdminOptional = roleRepository.findByName("ROLE_ADMIN");
        if (roleAdminOptional.isEmpty()) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("admin role");
            roleRepository.save(adminRole);
        }

        Optional<Role> roleUserOptional = roleRepository.findByName("ROLE_USER");
        if (roleUserOptional.isEmpty()) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("user role");
            roleRepository.save(userRole);
        }

        Optional<Moderator> moderatorOptional=moderatorRepository.findByEmail("m7modm42@gmail.com");
        if(moderatorOptional.isEmpty()) {
            Moderator moderator = new Moderator();
            moderator.setEmail("m7modm42@gmail.com");
            moderator.setMyPassword(passwordEncoder.encode(CharBuffer.wrap("123")));
            moderator.setFirstName("mahmoud");
            moderator.setLastName("hamed");
            moderator.setIsActive(true);
            moderator.setIsBlocked(false);
            moderator.getRoles().add(moderatorRole);
            moderatorRepository.save(moderator);
        }
    }
}

