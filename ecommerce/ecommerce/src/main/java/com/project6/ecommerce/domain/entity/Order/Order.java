package com.project6.ecommerce.domain.entity.Order;

import com.project6.ecommerce.domain.entity.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private BigDecimal totalAmount;
    
    // Address fields
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String zipCode;
    
    // Status
    @Enumerated(EnumType.STRING)
    private status status; // reusing existing status or new enum? Let's use a new OrderStatus

    private LocalDateTime createdAt;
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
