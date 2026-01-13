package com.ecommerce.service;

import com.ecommerce.dto.UserDto;
import com.ecommerce.dto.auth.RegisterRequest;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .role(User.Role.CUSTOMER)
                .build();
        
        return userRepository.save(user);
    }
    
    public User createAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.Role.ADMIN)
                .build();
        
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> getAllCustomers(Pageable pageable) {
        return userRepository.findByRole(User.Role.CUSTOMER, pageable)
                .map(UserDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> searchCustomers(String search, Pageable pageable) {
        return userRepository.searchCustomers(search, pageable)
                .map(UserDto::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getTopCustomersBySpending(int limit) {
        return userRepository.findTopCustomersBySpending(PageRequest.of(0, limit))
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getTopCustomersByOrderCount(int limit) {
        return userRepository.findTopCustomersByOrderCount(PageRequest.of(0, limit))
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = findById(id);
        
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getAddress() != null) user.setAddress(userDto.getAddress());
        if (userDto.getCity() != null) user.setCity(userDto.getCity());
        if (userDto.getState() != null) user.setState(userDto.getState());
        if (userDto.getZipCode() != null) user.setZipCode(userDto.getZipCode());
        if (userDto.getCountry() != null) user.setCountry(userDto.getCountry());
        
        return UserDto.fromEntity(userRepository.save(user));
    }
    
    public void updateCustomerStats(Long userId, BigDecimal orderAmount) {
        User user = findById(userId);
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setLifetimeSpent(user.getLifetimeSpent().add(orderAmount));
        userRepository.save(user);
    }
    
    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public long countCustomers() {
        return userRepository.countByRole(User.Role.CUSTOMER);
    }
}

