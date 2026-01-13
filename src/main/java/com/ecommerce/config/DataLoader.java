package com.ecommerce.config;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;
    private final WarrantyRepository warrantyRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Loading sample data...");
            loadUsers();
            loadCategories();
            loadProducts();
            loadDiscounts();
            loadOrders();
            loadWarranties();
            loadItemCategories();
            loadInventoryItems();
            log.info("Sample data loaded successfully!");
        }
    }
    
    private void loadItemCategories() {
        List<ItemCategory> itemCategories = Arrays.asList(
            ItemCategory.builder()
                .name("Cooler")
                .description("Air coolers and desert coolers")
                .icon("bi-wind")
                .displayOrder(1)
                .build(),
            ItemCategory.builder()
                .name("Chimney")
                .description("Kitchen chimneys and exhaust")
                .icon("bi-cloud-haze")
                .displayOrder(2)
                .build(),
            ItemCategory.builder()
                .name("Mixer")
                .description("Mixer grinders and blenders")
                .icon("bi-cup-straw")
                .displayOrder(3)
                .build(),
            ItemCategory.builder()
                .name("Geyser")
                .description("Water heaters and geysers")
                .icon("bi-droplet-half")
                .displayOrder(4)
                .build(),
            ItemCategory.builder()
                .name("Atta Chakki")
                .description("Flour mills and atta makers")
                .icon("bi-gear")
                .displayOrder(5)
                .build(),
            ItemCategory.builder()
                .name("Fans")
                .description("Ceiling fans, table fans, and pedestal fans")
                .icon("bi-fan")
                .displayOrder(6)
                .build(),
            ItemCategory.builder()
                .name("Crockery")
                .description("Kitchen crockery and utensils")
                .icon("bi-cup-hot")
                .displayOrder(7)
                .build()
        );
        itemCategoryRepository.saveAll(itemCategories);
        log.info("Loaded {} item categories", itemCategoryRepository.count());
    }
    
    private void loadInventoryItems() {
        List<ItemCategory> categories = itemCategoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No item categories found to create inventory items");
            return;
        }
        
        ItemCategory coolerCat = categories.stream().filter(c -> c.getName().equals("Cooler")).findFirst().orElse(categories.get(0));
        ItemCategory chimneyCat = categories.stream().filter(c -> c.getName().equals("Chimney")).findFirst().orElse(categories.get(0));
        ItemCategory mixerCat = categories.stream().filter(c -> c.getName().equals("Mixer")).findFirst().orElse(categories.get(0));
        ItemCategory fanCat = categories.stream().filter(c -> c.getName().equals("Fans")).findFirst().orElse(categories.get(0));
        ItemCategory geyserCat = categories.stream().filter(c -> c.getName().equals("Geyser")).findFirst().orElse(categories.get(0));
        
        List<InventoryItem> items = Arrays.asList(
            // Cooler with both warranties
            InventoryItem.builder()
                .itemId("CLR-001")
                .name("Desert Air Cooler 55L")
                .category(coolerCat)
                .itemDetails("Powerful desert air cooler with honeycomb pads. Customer purchased for living room.")
                .dateOfPurchase(LocalDate.now().minusDays(30))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(12)
                .productWarrantyStartDate(LocalDate.now().minusDays(30))
                .productWarrantyEndDate(LocalDate.now().minusDays(30).plusMonths(12))
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(24)
                .motorWarrantyStartDate(LocalDate.now().minusDays(30))
                .motorWarrantyEndDate(LocalDate.now().minusDays(30).plusMonths(24))
                .customerName("Ramesh Kumar")
                .customerPhone("+91 98765 12345")
                .customerEmail("ramesh@gmail.com")
                .address("House No. 45, Sector 12")
                .city("Ajmer")
                .state("Rajasthan")
                .pinCode("305001")
                .brand("Kenway")
                .model("DAC-55L")
                .serialNumber("KW-CLR-2025-001")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Cooler with only motor warranty
            InventoryItem.builder()
                .itemId("CLR-002")
                .name("Tower Air Cooler 35L")
                .category(coolerCat)
                .itemDetails("Tower cooler with remote control. Installed in bedroom.")
                .dateOfPurchase(LocalDate.now().minusDays(180))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(12)
                .productWarrantyStartDate(LocalDate.now().minusDays(180))
                .productWarrantyEndDate(LocalDate.now().minusDays(180).plusMonths(12))
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(36)
                .motorWarrantyStartDate(LocalDate.now().minusDays(180))
                .motorWarrantyEndDate(LocalDate.now().minusDays(180).plusMonths(36))
                .customerName("Suresh Sharma")
                .customerPhone("+91 99887 65432")
                .customerEmail("suresh.sharma@yahoo.com")
                .address("B-23, Vaishali Nagar")
                .city("Jaipur")
                .state("Rajasthan")
                .pinCode("302012")
                .brand("Kenway")
                .model("TAC-35L")
                .serialNumber("KW-CLR-2024-089")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Chimney - 5 year product + 2 year motor
            InventoryItem.builder()
                .itemId("CHM-001")
                .name("Auto-Clean Chimney 90cm")
                .category(chimneyCat)
                .itemDetails("Premium auto-clean chimney with curved glass. Installed in modular kitchen.")
                .dateOfPurchase(LocalDate.now().minusDays(60))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(60)
                .productWarrantyStartDate(LocalDate.now().minusDays(60))
                .productWarrantyEndDate(LocalDate.now().minusDays(60).plusMonths(60))
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(24)
                .motorWarrantyStartDate(LocalDate.now().minusDays(60))
                .motorWarrantyEndDate(LocalDate.now().minusDays(60).plusMonths(24))
                .customerName("Priya Patel")
                .customerPhone("+91 88888 77777")
                .customerEmail("priya.patel@gmail.com")
                .address("Flat 301, Sunrise Apartments")
                .city("Ahmedabad")
                .state("Gujarat")
                .pinCode("380015")
                .brand("Kenway")
                .model("ACH-90")
                .serialNumber("KW-CHM-2025-045")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Mixer with expiring product warranty soon
            InventoryItem.builder()
                .itemId("MXR-001")
                .name("Mixer Grinder 750W")
                .category(mixerCat)
                .itemDetails("3-jar mixer grinder. Heavy duty motor.")
                .dateOfPurchase(LocalDate.now().minusMonths(11))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(12)
                .productWarrantyStartDate(LocalDate.now().minusMonths(11))
                .productWarrantyEndDate(LocalDate.now().plusDays(30)) // Expiring soon!
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(60)
                .motorWarrantyStartDate(LocalDate.now().minusMonths(11))
                .motorWarrantyEndDate(LocalDate.now().minusMonths(11).plusMonths(60))
                .customerName("Anju Devi")
                .customerPhone("+91 77777 66666")
                .customerEmail("anju.devi@gmail.com")
                .address("Plot 78, Industrial Area")
                .city("Ajmer")
                .state("Rajasthan")
                .pinCode("305004")
                .brand("Kenway")
                .model("MG-750W")
                .serialNumber("KW-MXR-2025-112")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Fan - expired product warranty, active motor warranty  
            InventoryItem.builder()
                .itemId("FAN-001")
                .name("Ceiling Fan 1400mm")
                .category(fanCat)
                .itemDetails("High-speed ceiling fan with decorative finish.")
                .dateOfPurchase(LocalDate.now().minusMonths(14))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(12)
                .productWarrantyStartDate(LocalDate.now().minusMonths(14))
                .productWarrantyEndDate(LocalDate.now().minusMonths(2)) // Already expired
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(36)
                .motorWarrantyStartDate(LocalDate.now().minusMonths(14))
                .motorWarrantyEndDate(LocalDate.now().minusMonths(14).plusMonths(36))
                .customerName("Vikram Singh")
                .customerPhone("+91 66666 55555")
                .customerEmail("vikram.singh@hotmail.com")
                .address("23, Raja Park")
                .city("Jaipur")
                .state("Rajasthan")
                .pinCode("302004")
                .brand("Kenway")
                .model("CF-1400")
                .serialNumber("KW-FAN-2024-078")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Geyser with both warranties
            InventoryItem.builder()
                .itemId("GYS-001")
                .name("Instant Water Geyser 15L")
                .category(geyserCat)
                .itemDetails("15L storage water heater with high-density insulation.")
                .dateOfPurchase(LocalDate.now().minusDays(15))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(24)
                .productWarrantyStartDate(LocalDate.now().minusDays(15))
                .productWarrantyEndDate(LocalDate.now().minusDays(15).plusMonths(24))
                .hasMotorWarranty(false) // Geyser has no motor, but may have element warranty
                .customerName("Meena Kumari")
                .customerPhone("+91 55555 44444")
                .customerEmail("meena.k@gmail.com")
                .address("12, Civil Lines")
                .city("Ajmer")
                .state("Rajasthan")
                .pinCode("305001")
                .brand("Kenway")
                .model("IWG-15L")
                .serialNumber("KW-GYS-2026-003")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .build(),
                
            // Item under service
            InventoryItem.builder()
                .itemId("CLR-003")
                .name("Personal Air Cooler 20L")
                .category(coolerCat)
                .itemDetails("Currently at service center for pump replacement.")
                .dateOfPurchase(LocalDate.now().minusMonths(8))
                .hasProductWarranty(true)
                .productWarrantyPeriodMonths(12)
                .productWarrantyStartDate(LocalDate.now().minusMonths(8))
                .productWarrantyEndDate(LocalDate.now().plusMonths(4))
                .hasMotorWarranty(true)
                .motorWarrantyPeriodMonths(24)
                .motorWarrantyStartDate(LocalDate.now().minusMonths(8))
                .motorWarrantyEndDate(LocalDate.now().plusMonths(16))
                .customerName("Rajan Gupta")
                .customerPhone("+91 44444 33333")
                .customerEmail("rajan.g@gmail.com")
                .address("56, Station Road")
                .city("Ajmer")
                .state("Rajasthan")
                .pinCode("305001")
                .brand("Kenway")
                .model("PAC-20L")
                .serialNumber("KW-CLR-2025-156")
                .status(InventoryItem.ItemStatus.UNDER_SERVICE)
                .notes("Customer reported water pump not working. Sent to service center on 10-Jan-2026.")
                .build()
        );
        
        inventoryItemRepository.saveAll(items);
        log.info("Loaded {} inventory items", inventoryItemRepository.count());
    }
    
    private void loadUsers() {
        // Admin User
        User admin = User.builder()
                .name("Admin User")
                .email("admin@kenway.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("+91 98765 43210")
                .role(User.Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);
        
        // Sample Customers
        List<User> customers = Arrays.asList(
            User.builder()
                .name("Rahul Sharma")
                .email("rahul@example.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 98765 43211")
                .address("123 MG Road")
                .city("Mumbai")
                .state("Maharashtra")
                .zipCode("400001")
                .country("India")
                .role(User.Role.CUSTOMER)
                .totalOrders(5)
                .lifetimeSpent(new BigDecimal("15499.00"))
                .build(),
            User.builder()
                .name("Priya Patel")
                .email("priya@example.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+91 98765 43212")
                .address("456 Anna Salai")
                .city("Chennai")
                .state("Tamil Nadu")
                .zipCode("600001")
                .country("India")
                .role(User.Role.CUSTOMER)
                .totalOrders(8)
                .lifetimeSpent(new BigDecimal("28999.00"))
                .build()
        );
        userRepository.saveAll(customers);
        
        log.info("Loaded {} users", userRepository.count());
    }
    
    private void loadCategories() {
        List<Category> categories = Arrays.asList(
            Category.builder()
                .name("Kitchen Appliances")
                .description("Modern kitchen appliances for your home")
                .imageUrl("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400")
                .displayOrder(1)
                .build(),
            Category.builder()
                .name("Household")
                .description("Essential household and grocery items")
                .imageUrl("https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=400")
                .displayOrder(2)
                .build()
        );
        categoryRepository.saveAll(categories);
        
        log.info("Loaded {} categories", categoryRepository.count());
    }
    
    private void loadProducts() {
        Category kitchen = categoryRepository.findByName("Kitchen Appliances").orElse(null);
        Category household = categoryRepository.findByName("Household").orElse(null);
        
        List<Product> products = Arrays.asList(
            // Kitchen Appliances - Coolers
            Product.builder()
                .name("Desert Air Cooler 55L")
                .description("Powerful desert air cooler with 55L water tank, honeycomb cooling pads, and 3-speed control. Perfect for large rooms up to 500 sq ft. Features ice chamber and castor wheels for easy mobility.")
                .sku("KITCH-DAC-001")
                .brand("Kenway")
                .price(new BigDecimal("8999.00"))
                .originalPrice(new BigDecimal("11999.00"))
                .stockQuantity(25)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600")
                .warrantyPeriodMonths(24)
                .featured(true)
                .rating(4.5)
                .reviewCount(342)
                .soldCount(890)
                .build(),
            Product.builder()
                .name("Personal Air Cooler 20L")
                .description("Compact personal air cooler ideal for small rooms and offices. 20L water tank with 4-way air deflection. Low power consumption and silent operation.")
                .sku("KITCH-PAC-002")
                .brand("Kenway")
                .price(new BigDecimal("4499.00"))
                .originalPrice(new BigDecimal("5999.00"))
                .stockQuantity(40)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600")
                .warrantyPeriodMonths(12)
                .rating(4.3)
                .reviewCount(256)
                .soldCount(678)
                .build(),
            Product.builder()
                .name("Tower Air Cooler 35L")
                .description("Sleek tower design air cooler with 35L capacity. Features remote control, timer function, and 4-speed settings. Ideal for medium-sized rooms.")
                .sku("KITCH-TAC-003")
                .brand("Kenway")
                .price(new BigDecimal("6999.00"))
                .stockQuantity(30)
                .category(kitchen)
                .color("Black")
                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600")
                .warrantyPeriodMonths(24)
                .featured(true)
                .rating(4.6)
                .reviewCount(189)
                .soldCount(456)
                .build(),
                
            // Kitchen Appliances - Chimney
            Product.builder()
                .name("Auto-Clean Kitchen Chimney 90cm")
                .description("Premium auto-clean chimney with 1200 m³/hr suction power, LED lights, and touch control panel. Filterless technology for hassle-free maintenance. Curved glass design.")
                .sku("KITCH-CHM-004")
                .brand("Kenway")
                .price(new BigDecimal("15999.00"))
                .originalPrice(new BigDecimal("22999.00"))
                .stockQuantity(15)
                .category(kitchen)
                .color("Black")
                .imageUrl("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600")
                .warrantyPeriodMonths(60)
                .featured(true)
                .rating(4.7)
                .reviewCount(189)
                .soldCount(456)
                .build(),
            Product.builder()
                .name("Wall Mount Chimney 60cm")
                .description("Stylish wall mount chimney with baffle filter. 1000 m³/hr suction, push button control, and stainless steel body. Perfect for Indian cooking.")
                .sku("KITCH-CHM-005")
                .brand("Kenway")
                .price(new BigDecimal("8999.00"))
                .originalPrice(new BigDecimal("12999.00"))
                .stockQuantity(20)
                .category(kitchen)
                .color("Silver")
                .imageUrl("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600")
                .warrantyPeriodMonths(36)
                .rating(4.4)
                .reviewCount(267)
                .soldCount(589)
                .build(),
                
            // Kitchen Appliances - Wet Grinder
            Product.builder()
                .name("Table Top Wet Grinder 2L")
                .description("Traditional stone grinding wet grinder with 2L capacity. Perfect for making idli and dosa batter. Energy efficient motor with overload protection. Stainless steel drum.")
                .sku("KITCH-WTG-006")
                .brand("Kenway")
                .price(new BigDecimal("4599.00"))
                .originalPrice(new BigDecimal("5999.00"))
                .stockQuantity(30)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600")
                .warrantyPeriodMonths(24)
                .featured(true)
                .rating(4.6)
                .reviewCount(567)
                .soldCount(1234)
                .build(),
            Product.builder()
                .name("Tilting Wet Grinder 2L")
                .description("Tilting wet grinder with convenient batter removal. 2L capacity with transparent lid. Suitable for idli, dosa, and vada batter preparation.")
                .sku("KITCH-WTG-007")
                .brand("Kenway")
                .price(new BigDecimal("5499.00"))
                .stockQuantity(25)
                .category(kitchen)
                .color("Red")
                .imageUrl("https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600")
                .warrantyPeriodMonths(24)
                .rating(4.5)
                .reviewCount(345)
                .soldCount(890)
                .build(),
                
            // Kitchen Appliances - Mixer
            Product.builder()
                .name("Mixer Grinder 750W")
                .description("Powerful 750W mixer grinder with 3 stainless steel jars. Features include overload protection, anti-slip feet, and ergonomic handles. Perfect for grinding, mixing, and blending.")
                .sku("KITCH-MXG-008")
                .brand("Kenway")
                .price(new BigDecimal("3499.00"))
                .originalPrice(new BigDecimal("4999.00"))
                .stockQuantity(50)
                .category(kitchen)
                .color("Red")
                .imageUrl("https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=600")
                .warrantyPeriodMonths(24)
                .featured(true)
                .rating(4.4)
                .reviewCount(890)
                .soldCount(2345)
                .build(),
            Product.builder()
                .name("Mixer Grinder 500W")
                .description("Compact 500W mixer grinder with 3 jars. Ideal for small families. Features rust-proof blades and shock-proof body.")
                .sku("KITCH-MXG-009")
                .brand("Kenway")
                .price(new BigDecimal("2499.00"))
                .stockQuantity(60)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=600")
                .warrantyPeriodMonths(24)
                .rating(4.2)
                .reviewCount(456)
                .soldCount(1567)
                .build(),
            Product.builder()
                .name("Juicer Mixer Grinder 500W")
                .description("Versatile juicer mixer grinder with fruit filter for fresh juice extraction. Includes 2 grinding jars and juicer attachment. Multi-purpose kitchen appliance.")
                .sku("KITCH-JMG-010")
                .brand("Kenway")
                .price(new BigDecimal("2899.00"))
                .originalPrice(new BigDecimal("3999.00"))
                .stockQuantity(40)
                .category(kitchen)
                .color("Purple")
                .imageUrl("https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=600")
                .warrantyPeriodMonths(24)
                .rating(4.5)
                .reviewCount(345)
                .soldCount(1567)
                .build(),
                
            // Kitchen Appliances - Fans
            Product.builder()
                .name("Ceiling Fan 1200mm")
                .description("High-speed ceiling fan with 1200mm sweep. Features include 3 blade design, double ball bearing, and rust-resistant coating. Energy efficient and silent operation.")
                .sku("KITCH-CFN-011")
                .brand("Kenway")
                .price(new BigDecimal("1899.00"))
                .originalPrice(new BigDecimal("2499.00"))
                .stockQuantity(75)
                .category(kitchen)
                .color("Brown")
                .imageUrl("https://images.unsplash.com/photo-1621873495914-845b6e5f4c87?w=600")
                .warrantyPeriodMonths(24)
                .rating(4.3)
                .reviewCount(456)
                .soldCount(1890)
                .build(),
            Product.builder()
                .name("Ceiling Fan 1400mm")
                .description("Extra-large ceiling fan with 1400mm sweep for bigger rooms. High air delivery with low power consumption. Decorative design with wooden finish.")
                .sku("KITCH-CFN-012")
                .brand("Kenway")
                .price(new BigDecimal("2499.00"))
                .stockQuantity(50)
                .category(kitchen)
                .color("Walnut")
                .imageUrl("https://images.unsplash.com/photo-1621873495914-845b6e5f4c87?w=600")
                .warrantyPeriodMonths(24)
                .featured(true)
                .rating(4.5)
                .reviewCount(234)
                .soldCount(890)
                .build(),
            Product.builder()
                .name("Pedestal Fan 400mm")
                .description("Oscillating pedestal fan with adjustable height and 3-speed settings. Features quiet motor and wide angle oscillation. Perfect for living room and bedroom.")
                .sku("KITCH-PFN-013")
                .brand("Kenway")
                .price(new BigDecimal("2299.00"))
                .stockQuantity(60)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1617375407361-9815f25e5225?w=600")
                .warrantyPeriodMonths(12)
                .rating(4.2)
                .reviewCount(234)
                .soldCount(890)
                .build(),
            Product.builder()
                .name("Table Fan 300mm")
                .description("Compact table fan with 300mm sweep. 3-speed control with powerful air throw. Lightweight and portable design.")
                .sku("KITCH-TFN-014")
                .brand("Kenway")
                .price(new BigDecimal("1299.00"))
                .originalPrice(new BigDecimal("1599.00"))
                .stockQuantity(80)
                .category(kitchen)
                .color("Blue")
                .imageUrl("https://images.unsplash.com/photo-1617375407361-9815f25e5225?w=600")
                .warrantyPeriodMonths(12)
                .rating(4.1)
                .reviewCount(345)
                .soldCount(1234)
                .build(),
            Product.builder()
                .name("Wall Mount Fan 450mm")
                .description("Space-saving wall mount fan with 450mm sweep. Remote control operation with timer function. Ideal for kitchens and small spaces.")
                .sku("KITCH-WFN-015")
                .brand("Kenway")
                .price(new BigDecimal("1799.00"))
                .stockQuantity(45)
                .category(kitchen)
                .color("White")
                .imageUrl("https://images.unsplash.com/photo-1617375407361-9815f25e5225?w=600")
                .warrantyPeriodMonths(12)
                .rating(4.3)
                .reviewCount(178)
                .soldCount(567)
                .build(),
            
            // Household Items - Bottles
            Product.builder()
                .name("Stainless Steel Water Bottle 1L")
                .description("Premium food-grade stainless steel water bottle. Double wall vacuum insulated keeps drinks cold 24hrs or hot 12hrs. Leak-proof lid with carrying loop.")
                .sku("HOUSE-WTB-001")
                .brand("Kenway")
                .price(new BigDecimal("599.00"))
                .originalPrice(new BigDecimal("899.00"))
                .stockQuantity(150)
                .category(household)
                .color("Silver")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600")
                .warrantyPeriodMonths(12)
                .featured(true)
                .rating(4.6)
                .reviewCount(890)
                .soldCount(4567)
                .build(),
            Product.builder()
                .name("Copper Water Bottle 1L")
                .description("Pure copper water bottle with health benefits. Ayurvedic drinking water storage for natural purification. Lacquer coated exterior for easy maintenance.")
                .sku("HOUSE-CWB-002")
                .brand("Kenway")
                .price(new BigDecimal("799.00"))
                .stockQuantity(100)
                .category(household)
                .color("Copper")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600")
                .warrantyPeriodMonths(6)
                .rating(4.7)
                .reviewCount(456)
                .soldCount(2345)
                .build(),
            Product.builder()
                .name("Fridge Water Bottle 1L (Set of 3)")
                .description("Set of 3 fridge water bottles with flip-top lid. Made from BPA-free food-grade plastic. Fits perfectly in refrigerator door. Easy to clean.")
                .sku("HOUSE-FWB-003")
                .brand("Kenway")
                .price(new BigDecimal("349.00"))
                .stockQuantity(200)
                .category(household)
                .color("Blue")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600")
                .warrantyPeriodMonths(3)
                .rating(4.2)
                .reviewCount(567)
                .soldCount(3456)
                .build(),
            Product.builder()
                .name("Kids Water Bottle 500ml")
                .description("Colorful kids water bottle with straw and carrying strap. Leak-proof design with fun cartoon prints. BPA-free and dishwasher safe.")
                .sku("HOUSE-KWB-004")
                .brand("Kenway")
                .price(new BigDecimal("249.00"))
                .stockQuantity(120)
                .category(household)
                .color("Pink")
                .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600")
                .warrantyPeriodMonths(3)
                .rating(4.4)
                .reviewCount(234)
                .soldCount(1890)
                .build(),
                
            // Household Items - Tiffin Boxes
            Product.builder()
                .name("Stainless Steel Tiffin Box 4 Tier")
                .description("Premium 4-tier stainless steel lunch box with insulated carry bag. Leak-proof containers perfect for office and school. Dishwasher safe and durable.")
                .sku("HOUSE-TFN-005")
                .brand("Kenway")
                .price(new BigDecimal("899.00"))
                .originalPrice(new BigDecimal("1299.00"))
                .stockQuantity(80)
                .category(household)
                .color("Silver")
                .imageUrl("https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600")
                .warrantyPeriodMonths(12)
                .featured(true)
                .rating(4.8)
                .reviewCount(678)
                .soldCount(3456)
                .build(),
            Product.builder()
                .name("Stainless Steel Tiffin Box 3 Tier")
                .description("Classic 3-tier stainless steel tiffin with locking clips. Ideal capacity for daily lunch. Comes with matching spoon and fork.")
                .sku("HOUSE-TFN-006")
                .brand("Kenway")
                .price(new BigDecimal("649.00"))
                .stockQuantity(100)
                .category(household)
                .color("Silver")
                .imageUrl("https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600")
                .warrantyPeriodMonths(12)
                .rating(4.5)
                .reviewCount(456)
                .soldCount(2567)
                .build(),
            Product.builder()
                .name("Insulated Lunch Box with Bag")
                .description("3-container insulated lunch box with thermal carry bag. Keeps food hot for 4+ hours. BPA-free plastic containers with steel inner.")
                .sku("HOUSE-TFN-007")
                .brand("Kenway")
                .price(new BigDecimal("699.00"))
                .stockQuantity(90)
                .category(household)
                .color("Blue")
                .imageUrl("https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600")
                .warrantyPeriodMonths(6)
                .rating(4.5)
                .reviewCount(345)
                .soldCount(2134)
                .build(),
            Product.builder()
                .name("Kids Lunch Box with Bottle")
                .description("Cute kids lunch box set with matching water bottle. Multiple compartments for different food items. Easy-grip handle and leak-proof design.")
                .sku("HOUSE-TFN-008")
                .brand("Kenway")
                .price(new BigDecimal("449.00"))
                .originalPrice(new BigDecimal("599.00"))
                .stockQuantity(110)
                .category(household)
                .color("Red")
                .imageUrl("https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600")
                .warrantyPeriodMonths(6)
                .rating(4.6)
                .reviewCount(289)
                .soldCount(1678)
                .build(),
                
            // Household Items - Buckets
            Product.builder()
                .name("Plastic Bucket 20L with Mug")
                .description("Durable plastic bucket with comfortable grip handle. Comes with matching mug. Perfect for bathroom and household use. Unbreakable material.")
                .sku("HOUSE-BKT-009")
                .brand("Kenway")
                .price(new BigDecimal("299.00"))
                .stockQuantity(200)
                .category(household)
                .color("Blue")
                .imageUrl("https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=600")
                .warrantyPeriodMonths(3)
                .rating(4.2)
                .reviewCount(234)
                .soldCount(5678)
                .build(),
            Product.builder()
                .name("Premium Bucket Set (3 Pieces)")
                .description("Set of 3 premium quality buckets - 25L, 18L, and 12L. Made from virgin plastic with sturdy handles. Available in vibrant colors.")
                .sku("HOUSE-BKT-010")
                .brand("Kenway")
                .price(new BigDecimal("599.00"))
                .originalPrice(new BigDecimal("799.00"))
                .stockQuantity(120)
                .category(household)
                .color("Green")
                .imageUrl("https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=600")
                .warrantyPeriodMonths(6)
                .featured(true)
                .rating(4.4)
                .reviewCount(156)
                .soldCount(1890)
                .build(),
            Product.builder()
                .name("Bathroom Bucket 16L with Stool")
                .description("Compact bathroom bucket with matching stool and mug. Space-saving design. Anti-skid base for safety.")
                .sku("HOUSE-BKT-011")
                .brand("Kenway")
                .price(new BigDecimal("449.00"))
                .stockQuantity(150)
                .category(household)
                .color("Pink")
                .imageUrl("https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=600")
                .warrantyPeriodMonths(3)
                .rating(4.3)
                .reviewCount(178)
                .soldCount(2345)
                .build(),
            Product.builder()
                .name("Mop Bucket with Wringer")
                .description("Floor cleaning mop bucket with squeeze wringer. 360° rotating mop head with microfiber pads. Includes 2 refill mop heads.")
                .sku("HOUSE-MOP-012")
                .brand("Kenway")
                .price(new BigDecimal("1199.00"))
                .originalPrice(new BigDecimal("1599.00"))
                .stockQuantity(70)
                .category(household)
                .color("Blue")
                .imageUrl("https://images.unsplash.com/photo-1584568694244-14fbdf83bd30?w=600")
                .warrantyPeriodMonths(6)
                .rating(4.5)
                .reviewCount(567)
                .soldCount(1456)
                .build()
        );
        
        productRepository.saveAll(products);
        log.info("Loaded {} products", productRepository.count());
    }
    
    private void loadDiscounts() {
        List<Discount> discounts = Arrays.asList(
            Discount.builder()
                .code("WELCOME10")
                .name("Welcome Discount")
                .description("10% off for new customers")
                .type(Discount.DiscountType.PERCENTAGE)
                .value(new BigDecimal("10"))
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(6))
                .customerSegment(Discount.CustomerSegment.NEW_CUSTOMERS)
                .active(true)
                .build(),
            Discount.builder()
                .code("SUMMER20")
                .name("Summer Sale")
                .description("20% off on orders over ₹2000")
                .type(Discount.DiscountType.PERCENTAGE)
                .value(new BigDecimal("20"))
                .minimumOrderAmount(new BigDecimal("2000"))
                .maximumDiscountAmount(new BigDecimal("500"))
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(1))
                .active(true)
                .build(),
            Discount.builder()
                .code("FLAT200")
                .name("Flat ₹200 Off")
                .description("₹200 off on orders over ₹1500")
                .type(Discount.DiscountType.FIXED_AMOUNT)
                .value(new BigDecimal("200"))
                .minimumOrderAmount(new BigDecimal("1500"))
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(3))
                .active(true)
                .build()
        );
        
        discountRepository.saveAll(discounts);
        log.info("Loaded {} discounts", discountRepository.count());
    }
    
    private void loadOrders() {
        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);
        List<Product> products = productRepository.findAll();
        
        if (customers.isEmpty() || products.isEmpty()) {
            log.warn("No customers or products found to create orders");
            return;
        }
        
        User rahul = customers.get(0);
        User priya = customers.size() > 1 ? customers.get(1) : customers.get(0);
        
        // Order 1 - Rahul - Desert Cooler + Mixer
        Order order1 = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(rahul)
                .status(Order.OrderStatus.DELIVERED)
                .paymentStatus(Order.PaymentStatus.PAID)
                .subtotal(new BigDecimal("12498.00"))
                .taxAmount(new BigDecimal("2249.64"))
                .shippingAmount(new BigDecimal("0"))
                .totalAmount(new BigDecimal("14747.64"))
                .shippingAddress(rahul.getAddress())
                .shippingCity(rahul.getCity())
                .shippingState(rahul.getState())
                .shippingZipCode(rahul.getZipCode())
                .shippingCountry(rahul.getCountry())
                .createdAt(LocalDateTime.now().minusDays(45))
                .build();
        orderRepository.save(order1);
        
        // Add order items for order1
        Product cooler = products.stream().filter(p -> p.getSku().equals("KITCH-DAC-001")).findFirst().orElse(products.get(0));
        Product mixer = products.stream().filter(p -> p.getSku().equals("KITCH-MXG-008")).findFirst().orElse(products.get(1));
        
        OrderItem item1_1 = OrderItem.builder()
                .order(order1)
                .product(cooler)
                .productName(cooler.getName())
                .productSku(cooler.getSku())
                .quantity(1)
                .unitPrice(cooler.getPrice())
                .totalPrice(cooler.getPrice())
                .build();
        order1.getItems().add(item1_1);
        
        OrderItem item1_2 = OrderItem.builder()
                .order(order1)
                .product(mixer)
                .productName(mixer.getName())
                .productSku(mixer.getSku())
                .quantity(1)
                .unitPrice(mixer.getPrice())
                .totalPrice(mixer.getPrice())
                .build();
        order1.getItems().add(item1_2);
        orderRepository.save(order1);
        
        // Order 2 - Priya - Chimney + Wet Grinder
        Order order2 = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(priya)
                .status(Order.OrderStatus.DELIVERED)
                .paymentStatus(Order.PaymentStatus.PAID)
                .subtotal(new BigDecimal("20598.00"))
                .taxAmount(new BigDecimal("3707.64"))
                .shippingAmount(new BigDecimal("0"))
                .totalAmount(new BigDecimal("24305.64"))
                .shippingAddress(priya.getAddress())
                .shippingCity(priya.getCity())
                .shippingState(priya.getState())
                .shippingZipCode(priya.getZipCode())
                .shippingCountry(priya.getCountry())
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        orderRepository.save(order2);
        
        Product chimney = products.stream().filter(p -> p.getSku().equals("KITCH-CHM-004")).findFirst().orElse(products.get(2));
        Product grinder = products.stream().filter(p -> p.getSku().equals("KITCH-WTG-006")).findFirst().orElse(products.get(3));
        
        OrderItem item2_1 = OrderItem.builder()
                .order(order2)
                .product(chimney)
                .productName(chimney.getName())
                .productSku(chimney.getSku())
                .quantity(1)
                .unitPrice(chimney.getPrice())
                .totalPrice(chimney.getPrice())
                .build();
        order2.getItems().add(item2_1);
        
        OrderItem item2_2 = OrderItem.builder()
                .order(order2)
                .product(grinder)
                .productName(grinder.getName())
                .productSku(grinder.getSku())
                .quantity(1)
                .unitPrice(grinder.getPrice())
                .totalPrice(grinder.getPrice())
                .build();
        order2.getItems().add(item2_2);
        orderRepository.save(order2);
        
        // Order 3 - Rahul - Fan + Tiffin Box + Bottles (Pending)
        Order order3 = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(rahul)
                .status(Order.OrderStatus.SHIPPED)
                .paymentStatus(Order.PaymentStatus.PAID)
                .subtotal(new BigDecimal("3397.00"))
                .taxAmount(new BigDecimal("611.46"))
                .shippingAmount(new BigDecimal("99.00"))
                .totalAmount(new BigDecimal("4107.46"))
                .shippingAddress(rahul.getAddress())
                .shippingCity(rahul.getCity())
                .shippingState(rahul.getState())
                .shippingZipCode(rahul.getZipCode())
                .shippingCountry(rahul.getCountry())
                .trackingNumber("TRK" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        orderRepository.save(order3);
        
        Product fan = products.stream().filter(p -> p.getSku().equals("KITCH-CFN-011")).findFirst().orElse(products.get(4));
        Product tiffin = products.stream().filter(p -> p.getSku().equals("HOUSE-TFN-005")).findFirst().orElse(products.get(5));
        Product bottle = products.stream().filter(p -> p.getSku().equals("HOUSE-WTB-001")).findFirst().orElse(products.get(6));
        
        order3.getItems().add(OrderItem.builder()
                .order(order3)
                .product(fan)
                .productName(fan.getName())
                .productSku(fan.getSku())
                .quantity(1)
                .unitPrice(fan.getPrice())
                .totalPrice(fan.getPrice())
                .build());
        order3.getItems().add(OrderItem.builder()
                .order(order3)
                .product(tiffin)
                .productName(tiffin.getName())
                .productSku(tiffin.getSku())
                .quantity(1)
                .unitPrice(tiffin.getPrice())
                .totalPrice(tiffin.getPrice())
                .build());
        order3.getItems().add(OrderItem.builder()
                .order(order3)
                .product(bottle)
                .productName(bottle.getName())
                .productSku(bottle.getSku())
                .quantity(1)
                .unitPrice(bottle.getPrice())
                .totalPrice(bottle.getPrice())
                .build());
        orderRepository.save(order3);
        
        // Order 4 - Priya - Recent order (today)
        Order order4 = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(priya)
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PAID)
                .subtotal(new BigDecimal("2499.00"))
                .taxAmount(new BigDecimal("449.82"))
                .shippingAmount(new BigDecimal("0"))
                .totalAmount(new BigDecimal("2948.82"))
                .shippingAddress(priya.getAddress())
                .shippingCity(priya.getCity())
                .shippingState(priya.getState())
                .shippingZipCode(priya.getZipCode())
                .shippingCountry(priya.getCountry())
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(order4);
        
        Product ceilingFan = products.stream().filter(p -> p.getSku().equals("KITCH-CFN-012")).findFirst().orElse(products.get(7));
        order4.getItems().add(OrderItem.builder()
                .order(order4)
                .product(ceilingFan)
                .productName(ceilingFan.getName())
                .productSku(ceilingFan.getSku())
                .quantity(1)
                .unitPrice(ceilingFan.getPrice())
                .totalPrice(ceilingFan.getPrice())
                .build());
        orderRepository.save(order4);
        
        log.info("Loaded {} orders", orderRepository.count());
    }
    
    private void loadWarranties() {
        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);
        List<Order> orders = orderRepository.findAll();
        List<Product> products = productRepository.findAll();
        
        if (customers.isEmpty() || orders.isEmpty()) {
            log.warn("No customers or orders found to create warranties");
            return;
        }
        
        User rahul = customers.get(0);
        User priya = customers.size() > 1 ? customers.get(1) : customers.get(0);
        Order order1 = orders.get(0);
        Order order2 = orders.size() > 1 ? orders.get(1) : orders.get(0);
        
        Product cooler = products.stream().filter(p -> p.getSku().equals("KITCH-DAC-001")).findFirst().orElse(products.get(0));
        Product mixer = products.stream().filter(p -> p.getSku().equals("KITCH-MXG-008")).findFirst().orElse(products.get(1));
        Product chimney = products.stream().filter(p -> p.getSku().equals("KITCH-CHM-004")).findFirst().orElse(products.get(2));
        Product grinder = products.stream().filter(p -> p.getSku().equals("KITCH-WTG-006")).findFirst().orElse(products.get(3));
        
        // Warranty 1 - Rahul's Cooler (Active, 24 months)
        Warranty warranty1 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(cooler)
                .user(rahul)
                .order(order1)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusDays(45))
                .warrantyStartDate(LocalDate.now().minusDays(45))
                .warrantyEndDate(LocalDate.now().minusDays(45).plusMonths(24))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty1);
        
        // Warranty 2 - Rahul's Mixer (Active, 24 months)
        Warranty warranty2 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(mixer)
                .user(rahul)
                .order(order1)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusDays(45))
                .warrantyStartDate(LocalDate.now().minusDays(45))
                .warrantyEndDate(LocalDate.now().minusDays(45).plusMonths(24))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty2);
        
        // Warranty 3 - Priya's Chimney (Active, 60 months - 5 years!)
        Warranty warranty3 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(chimney)
                .user(priya)
                .order(order2)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusDays(30))
                .warrantyStartDate(LocalDate.now().minusDays(30))
                .warrantyEndDate(LocalDate.now().minusDays(30).plusMonths(60))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty3);
        
        // Warranty 4 - Priya's Grinder (Active, 24 months)
        Warranty warranty4 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(grinder)
                .user(priya)
                .order(order2)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusDays(30))
                .warrantyStartDate(LocalDate.now().minusDays(30))
                .warrantyEndDate(LocalDate.now().minusDays(30).plusMonths(24))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty4);
        
        // Warranty 5 - Expiring soon (in 15 days) - simulating an old purchase
        Warranty warranty5 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(products.get(5))
                .user(rahul)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusMonths(12).plusDays(15))
                .warrantyStartDate(LocalDate.now().minusMonths(12).plusDays(15))
                .warrantyEndDate(LocalDate.now().plusDays(15))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty5);
        
        // Warranty 6 - Expiring very soon (in 5 days)
        Warranty warranty6 = Warranty.builder()
                .warrantyNumber("WRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .product(products.get(6))
                .user(priya)
                .serialNumber("SN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .purchaseDate(LocalDate.now().minusMonths(12).plusDays(5))
                .warrantyStartDate(LocalDate.now().minusMonths(12).plusDays(5))
                .warrantyEndDate(LocalDate.now().plusDays(5))
                .status(Warranty.WarrantyStatus.ACTIVE)
                .build();
        warrantyRepository.save(warranty6);
        
        log.info("Loaded {} warranties", warrantyRepository.count());
    }
}
