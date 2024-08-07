package com.auction.security.entites;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "admin")
public class Admin extends Account{


}
