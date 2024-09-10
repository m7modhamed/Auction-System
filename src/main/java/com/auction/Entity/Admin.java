package com.auction.Entity;


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
