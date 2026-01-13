package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    // Find top customers by lifetime spent
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.lifetimeSpent DESC")
    List<User> findTopCustomersBySpending(Pageable pageable);
    
    // Find customers with most orders
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.totalOrders DESC")
    List<User> findTopCustomersByOrderCount(Pageable pageable);
    
    // Search customers by name or email
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchCustomers(@Param("search") String search, Pageable pageable);
    
    // Count customers
    long countByRole(User.Role role);
    
    // Find customers who spent more than a certain amount
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND u.lifetimeSpent >= :amount")
    List<User> findCustomersWithMinimumSpending(@Param("amount") BigDecimal amount);
    
    // Search by name or email (for dashboard search)
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    
    // Top customers by orders
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.totalOrders DESC NULLS LAST")
    List<User> findTopCustomersByOrders(Pageable pageable);
    
    // Top customers by spend
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.lifetimeSpent DESC NULLS LAST")
    List<User> findTopCustomersBySpend(Pageable pageable);
}

