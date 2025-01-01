package com.auction.common.utility;

import com.auction.auctionmanagement.model.Category;
import com.auction.auctionmanagement.repository.CategoryRepository;
import com.auction.usersmanagement.model.Moderator;
import com.auction.usersmanagement.model.ProfileImage;
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
            mobileCategory.setImageUrl("https://img.freepik.com/premium-vector/colorful-mobile-phones-geometric-shapes-seamless-pattern_1362457-5115.jpg");
            categoryRepository.save(mobileCategory);
        }

        // Check if Electronics category exists, create if not
        Optional<Category> electronicsOptional = categoryRepository.findByName("Electronics");
        if (electronicsOptional.isEmpty()) {
            electronicsCategory = new Category();
            electronicsCategory.setName("Electronics");
            electronicsCategory.setDescription("Category for general electronic devices.");
            electronicsCategory.setAttributes(List.of("Brand", "Warranty", "Power Source", "Connectivity"));
            electronicsCategory.setImageUrl("https://thumbs.dreamstime.com/b/seamless-consumer-gadgets-electronics-pattern-television-smartphone-headset-earphones-speakers-laptop-camera-action-repetitive-195174818.jpg");
            categoryRepository.save(electronicsCategory);
        }

        // Check if Books category exists, create if not
        Optional<Category> booksOptional = categoryRepository.findByName("Books");
        if (booksOptional.isEmpty()) {
            booksCategory = new Category();
            booksCategory.setName("Books");
            booksCategory.setDescription("Category for books across various genres.");
            booksCategory.setAttributes(List.of("Author", "Genre", "Publisher", "ISBN"));
            booksCategory.setImageUrl("https://www.shutterstock.com/image-vector/seamless-book-pattern-background-wallpaper-600w-133478084.jpg");
            categoryRepository.save(booksCategory);
        }


        Optional<Category> carsOptional = categoryRepository.findByName("Cars");
        if (carsOptional.isEmpty()) {
            Category carsCategory = new Category();
            carsCategory.setName("Cars");
            carsCategory.setDescription("Category for cars of different makes, models, and features.");
            carsCategory.setAttributes(List.of("Make", "Model", "Year", "Engine Type", "Fuel Type", "Transmission"));
            carsCategory.setImageUrl("https://previews.123rf.com/images/seamartini/seamartini1811/seamartini181100239/128161859-old-car-or-vintage-retro-automobile-pattern-background-vector-seamless-design-of-auto-transport.jpg");
            categoryRepository.save(carsCategory);
        }

        Optional<Category> fashionOptional = categoryRepository.findByName("Fashion");
        if (fashionOptional.isEmpty()) {
            Category fashionCategory = new Category();
            fashionCategory.setName("Fashion");
            fashionCategory.setDescription("Category for fashion items including clothing, accessories, and more.");
            fashionCategory.setAttributes(List.of("Brand", "Size", "Color", "Material", "Style", "Gender"));
            fashionCategory.setImageUrl("https://static.vecteezy.com/system/resources/previews/000/163/508/non_2x/free-clothes-pattern-vectors.jpg");
            categoryRepository.save(fashionCategory);
        }


        Optional<Category> animalsOptional = categoryRepository.findByName("Animals");
        if (animalsOptional.isEmpty()) {
            Category animalsCategory = new Category();
            animalsCategory.setName("Animals");
            animalsCategory.setDescription("Category for animals including pets, wildlife, and farm animals.");
            animalsCategory.setAttributes(List.of("Species", "Breed", "Age", "Size", "Color", "Habitat"));
            animalsCategory.setImageUrl("https://t4.ftcdn.net/jpg/07/51/53/21/360_F_751532199_kefXjCdEWMa5QsrfdwmMZ9driif3aGTR.jpg");
            categoryRepository.save(animalsCategory);
        }

        Optional<Category> gamesOptional = categoryRepository.findByName("Games");
        if (gamesOptional.isEmpty()) {
            Category gamesCategory = new Category();
            gamesCategory.setName("Games");
            gamesCategory.setDescription("Category for games, including video games, board games, and more.");
            gamesCategory.setAttributes(List.of("Genre", "Platform", "Developer", "Publisher", "Release Date", "Age Rating"));
            gamesCategory.setImageUrl("https://static.vecteezy.com/system/resources/previews/002/279/007/non_2x/video-game-doodle-seamless-pattern-vector.jpg");
            categoryRepository.save(gamesCategory);
        }


        Optional<Category> otherOptional = categoryRepository.findByName("Other");
        if (otherOptional.isEmpty()) {
            Category otherCategory = new Category();
            otherCategory.setName("Other");
            otherCategory.setDescription("Category for items that do not fit into other specific categories.");
            otherCategory.setAttributes(List.of("Type", "Description", "Additional Details"));
            otherCategory.setImageUrl("https://thumbs.dreamstime.com/b/three-dots-icon-element-simple-icon-material-style-mobile-concept-web-apps-thin-line-icon-website-design-d-124226613.jpg");
            categoryRepository.save(otherCategory);
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

            ProfileImage profileImage = new ProfileImage();
            profileImage.setName("profile image");
            profileImage.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTUspugOXub65sbxVHOEaD-JEKC8NNWgkWhlg&s");
            moderator.setProfileImage(profileImage);
            moderatorRepository.save(moderator);
        }
    }
}

