package com.project6.ecommerce.domain.entity;

import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Product.status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ProductTest {

    @Test
    void equals_ShouldBeTrue_WhenIdsAreEqual() {
        UUID id = UUID.randomUUID();
        Product p1 = new Product(id, "P1", "D1", BigDecimal.TEN, 1, new HashSet<>(), null, status.AVAILABLE);
        Product p2 = new Product(id, "P2", "D2", BigDecimal.ONE, 2, new HashSet<>(), null, status.NOT_AVAILABLE); // diff content but same ID

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void equals_ShouldBeFalse_WhenIdsAreDifferent() {
        Product p1 = new Product(UUID.randomUUID(), "P1", "D1", BigDecimal.TEN, 1, new HashSet<>(), null, status.AVAILABLE);
        Product p2 = new Product(UUID.randomUUID(), "P1", "D1", BigDecimal.TEN, 1, new HashSet<>(), null, status.AVAILABLE);

        assertNotEquals(p1, p2);
    }
}
