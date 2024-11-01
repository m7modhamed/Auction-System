package com.auction.usersmanagement.model;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "admin")
public class Admin extends SysAccount {


}
