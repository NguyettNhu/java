package com.yukihira.bookstore.cart;

import com.yukihira.bookstore.common.domain.BaseEntity;
import com.yukihira.bookstore.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", uniqueConstraints =
        @UniqueConstraint(name = "uk_carts_user", columnNames = "user_id"))
public class Cart extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

    public void clear() {
        items.clear();
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    public User getUser() { return user; }
    public List<CartItem> getItems() { return items; }
}
