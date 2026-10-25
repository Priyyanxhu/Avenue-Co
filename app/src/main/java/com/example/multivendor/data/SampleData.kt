package com.example.multivendor.data

object SampleData {
    val categories = listOf(
        Category("all", "All Collections", "✨"),
        Category("electronics", "Tech & Audio", "🎧"),
        Category("home", "Artisan Decor", "🏺"),
        Category("beauty", "Organic Wellness", "🌿"),
        Category("fashion", "Apparel & Linen", "👕"),
        Category("books", "Books & Prints", "📚")
    )

    val vendors = listOf(
        Vendor(
            id = "techhub",
            name = "TechHub Store",
            description = "Bengaluru • Urban Electronics & Maker Hardware",
            rating = 4.8,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2019,
            responseTime = "1hr response time",
            totalSales = 45200
        ),
        Vendor(
            id = "stylenest",
            name = "StyleNest Studio",
            description = "Sustainable apparel & organic linen handcrafted with zero plastic.",
            rating = 4.4,
            location = "Mumbai, India",
            isVerified = true,
            joinedYear = 2020,
            responseTime = "2hr response time",
            totalSales = 28100
        ),
        Vendor(
            id = "homeess",
            name = "HomeEssentials Living",
            description = "Generational brassware, studio stoneware & hand-carved homeware.",
            rating = 4.7,
            location = "Delhi, India",
            isVerified = true,
            joinedYear = 2018,
            responseTime = "3hr response time",
            totalSales = 31900
        ),
        Vendor(
            id = "beautybloom",
            name = "BeautyBloom Organics",
            description = "Small Batch Botanical Apothecary • Cold-pressed cold-filtered oils & balms.",
            rating = 4.9,
            location = "Hyderabad, India",
            isVerified = true,
            joinedYear = 2021,
            responseTime = "2hr response time",
            totalSales = 12400
        ),
        Vendor(
            id = "sportify",
            name = "Sportify Performance",
            description = "Technical athletic apparel, gym gear and precision performance equipment.",
            rating = 4.6,
            location = "Chennai, India",
            isVerified = false,
            joinedYear = 2020,
            responseTime = "Fast Dispatch",
            totalSales = 15800
        ),
        Vendor(
            id = "bookworm",
            name = "Bookworm Corner",
            description = "Collector editions, independent literary publishers & art prints.",
            rating = 4.9,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2017,
            responseTime = "1hr response time",
            totalSales = 19800
        ),
        Vendor(
            id = "chrono",
            name = "Chrono Studio",
            description = "Minimalist sapphire horology & vegetable-tanned leather bands.",
            rating = 4.8,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2021,
            responseTime = "1hr response time",
            totalSales = 14300
        ),
        Vendor(
            id = "urbankicks",
            name = "Urban Kicks",
            description = "Handcrafted suede sneakers & bespoke small-batch leather footwear.",
            rating = 4.6,
            location = "Mumbai, India",
            isVerified = true,
            joinedYear = 2022,
            responseTime = "2hr response time",
            totalSales = 11200
        ),
        Vendor(
            id = "terrene",
            name = "Terrene Studio",
            description = "Small-batch hand-poured ceramic candle vessels & botanical wax.",
            rating = 4.8,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2022,
            responseTime = "1hr response time",
            totalSales = 9400
        ),
        Vendor(
            id = "fjord",
            name = "Fjord Co-op",
            description = "Direct-trade single-origin specialty roast coffees.",
            rating = 4.9,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2021,
            responseTime = "1hr response time",
            totalSales = 16800
        ),
        Vendor(
            id = "botanica",
            name = "Botanica Candle Co",
            description = "Artisanal hand-crafted scented amber candles from Bengaluru studios.",
            rating = 4.9,
            location = "Bengaluru, India",
            isVerified = true,
            joinedYear = 2023,
            responseTime = "1hr response time",
            totalSales = 8200
        )
    )

    val products = listOf(
        Product(
            id = "p9",
            name = "Atomic Habits (Collector's Edition)",
            description = "James Clear's #1 bestseller on building good habits & breaking bad ones. Collector's clothbound hardback.",
            price = 499.0,
            originalPrice = 799.0,
            rating = 4.9,
            reviews = 5421,
            categoryId = "books",
            vendorId = "bookworm",
            stock = 150,
            isFeatured = true,
            tags = listOf("books", "collector", "featured"),
            gradientSeed = 8,
            imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p1",
            name = "Aurora Wireless Headphones",
            description = "40h battery, dual ANC active noise cancellation, deep bass, foldable design with plush memory-foam cushions.",
            price = 2999.0,
            originalPrice = 4999.0,
            rating = 4.7,
            reviews = 1120,
            categoryId = "electronics",
            vendorId = "techhub",
            stock = 45,
            isFeatured = true,
            tags = listOf("audio", "anc", "featured"),
            gradientSeed = 0,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_watch",
            name = "Smart Minimalist Watch White",
            description = "316L brushed steel casing, sapphire crystal glass, genuine navy Italian leather strap with Swiss quartz movement.",
            price = 1499.0,
            originalPrice = 2540.0,
            rating = 4.8,
            reviews = 850,
            categoryId = "electronics",
            vendorId = "chrono",
            stock = 40,
            isFeatured = true,
            tags = listOf("watch", "minimalist", "featured"),
            gradientSeed = 1,
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_sneaker",
            name = "Urban Suede Sneaker",
            description = "Handcrafted deep navy Italian calf suede, cushioned vulcanized cupsole, waxed laces for all-day comfort.",
            price = 2199.0,
            originalPrice = 3140.0,
            rating = 4.6,
            reviews = 430,
            categoryId = "fashion",
            vendorId = "urbankicks",
            stock = 55,
            isFeatured = true,
            tags = listOf("shoes", "suede", "featured"),
            gradientSeed = 7,
            imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_aura",
            name = "Aura Wireless Studio",
            description = "Studio acoustics with real-time room calibration, 4 microphones, transparency mode and ultra-low latency.",
            price = 2999.0,
            originalPrice = 4999.0,
            rating = 4.8,
            reviews = 124,
            categoryId = "electronics",
            vendorId = "techhub",
            stock = 25,
            isFeatured = true,
            tags = listOf("audio", "studio", "featured"),
            gradientSeed = 0,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_candle_amber",
            name = "Amber Vanilla Candle",
            description = "Hand-poured pure soy & coconut wax candle with warm amber, Madagascar vanilla and charred cedarwood notes. 50hr burn.",
            price = 899.0,
            originalPrice = 1299.0,
            rating = 4.9,
            reviews = 86,
            categoryId = "home",
            vendorId = "botanica",
            stock = 75,
            isFeatured = true,
            tags = listOf("candle", "artisan", "featured"),
            gradientSeed = 4,
            imageUrl = "https://images.unsplash.com/photo-1602874801007-bd458bb1b8b8?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p11",
            name = "Mechanical Keyboards",
            description = "Custom mechanical keyboard with lubed tactile switches, PBT dye-sub keycaps, hot-swap PCB, and gasket mount.",
            price = 4299.0,
            originalPrice = 5499.0,
            rating = 4.8,
            reviews = 1120,
            categoryId = "electronics",
            vendorId = "techhub",
            stock = 25,
            isNew = true,
            tags = listOf("keyboard", "tech"),
            gradientSeed = 10,
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_aerofold",
            name = "AeroFold Stand",
            description = "Aerospace grade anodized aluminum foldable laptop stand. Portable, rigid and ergonomically angled.",
            price = 1750.0,
            originalPrice = 2499.0,
            rating = 4.7,
            reviews = 320,
            categoryId = "electronics",
            vendorId = "techhub",
            stock = 60,
            tags = listOf("stand", "aluminum"),
            gradientSeed = 1,
            imageUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p3",
            name = "Linen Shirts",
            description = "100% pure organic European flax linen relaxed casual shirt with natural shell buttons.",
            price = 2890.0,
            originalPrice = 3490.0,
            rating = 4.4,
            reviews = 840,
            categoryId = "fashion",
            vendorId = "stylenest",
            stock = 120,
            tags = listOf("linen", "artisan"),
            gradientSeed = 2,
            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_tote",
            name = "Artisan Tote",
            description = "Handcrafted unbleached heavy canvas carryall tote with vegetable-tanned bridle leather handles.",
            price = 1450.0,
            originalPrice = 1990.0,
            rating = 4.6,
            reviews = 210,
            categoryId = "fashion",
            vendorId = "stylenest",
            stock = 45,
            tags = listOf("tote", "bag"),
            gradientSeed = 3,
            imageUrl = "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_vase",
            name = "Ceramic Vase",
            description = "Hand-thrown stoneware sculptural centerpiece vase with speckled cream satin matte glaze.",
            price = 1850.0,
            originalPrice = 2450.0,
            rating = 4.7,
            reviews = 620,
            categoryId = "home",
            vendorId = "homeess",
            stock = 30,
            tags = listOf("ceramic", "decor"),
            gradientSeed = 5,
            imageUrl = "https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_platter",
            name = "Brass Platter",
            description = "Generational artisan-hammered pure brass decorative serving platter with antiqued brushed finish.",
            price = 3400.0,
            originalPrice = 4200.0,
            rating = 4.8,
            reviews = 95,
            categoryId = "home",
            vendorId = "homeess",
            stock = 18,
            tags = listOf("brass", "decor"),
            gradientSeed = 6,
            imageUrl = "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_candle_botanica",
            name = "Pure Botanica Candle",
            description = "Ceramic vessel botanical candle with natural wild lavender oils and cotton wick. Clean, non-toxic burn.",
            price = 899.0,
            originalPrice = 1199.0,
            rating = 4.8,
            reviews = 110,
            categoryId = "home",
            vendorId = "terrene",
            stock = 80,
            tags = listOf("candle", "organic"),
            gradientSeed = 11,
            imageUrl = "https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=800&q=80"
        ),
        Product(
            id = "p_coffee",
            name = "Nordic Roast Coffee",
            description = "Single-origin washed Ethiopian heirloom beans roasted to a light-medium profile. Freshly sealed valve bag.",
            price = 649.0,
            originalPrice = 799.0,
            rating = 4.9,
            reviews = 140,
            categoryId = "home",
            vendorId = "fjord",
            stock = 100,
            tags = listOf("coffee", "artisan"),
            gradientSeed = 12,
            imageUrl = "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&w=800&q=80"
        )
    )

    val promos = listOf(
        Promo("SAVE10", "10% off orders above ₹999", 0.10),
        Promo("FREESHIP", "Free shipping on any order", 0.0)
    )

    val initialOrders = listOf(
        Order(
            id = "ORD-78A1B2",
            lines = listOf(
                OrderLine("p1", "Aurora Wireless Headphones", 2999.0, 1, "techhub"),
                OrderLine("p11", "Mechanical Keyboard RGB", 2799.0, 1, "techhub")
            ),
            subtotal = 5798.0,
            discount = 579.8,
            shipping = 0.0,
            total = 5218.2,
            status = "Confirmed",
            date = "01 Oct 2026, 04:30 PM",
            address = "42, MG Road, Indiranagar, Bengaluru - 560038",
            paymentMethod = "UPI (Google Pay)",
            promoCode = "SAVE10"
        ),
        Order(
            id = "ORD-45F9C3",
            lines = listOf(
                OrderLine("p14", "Classic White Sneakers", 1799.0, 1, "stylenest"),
                OrderLine("p2", "Pulse Smart Watch Series 7", 3499.0, 1, "techhub")
            ),
            subtotal = 5298.0,
            discount = 0.0,
            shipping = 0.0,
            total = 5298.0,
            status = "Shipped",
            date = "30 Sep 2026, 11:15 AM",
            address = "Flat 304, Green Heights, Powai, Mumbai - 400076",
            paymentMethod = "Credit Card (Visa)"
        ),
        Order(
            id = "ORD-92D4E1",
            lines = listOf(
                OrderLine("p10", "Smart LED Desk Lamp", 1599.0, 2, "homeess"),
                OrderLine("p13", "Bamboo Bedsheet Set", 1899.0, 1, "homeess")
            ),
            subtotal = 5097.0,
            discount = 509.7,
            shipping = 0.0,
            total = 4587.3,
            status = "Delivered",
            date = "28 Sep 2026, 02:45 PM",
            address = "Sector 15, H-21, Noida, Uttar Pradesh - 201301",
            paymentMethod = "Cash on Delivery",
            promoCode = "SAVE10"
        )
    )

    val initialReviews = listOf(
        Review(
            id = "rev-1",
            productId = "p1",
            userName = "Rahul Verma",
            rating = 5,
            title = "Best ANC headphones under ₹5,000! Hands down.",
            comment = "The active noise cancellation is shockingly good. Tested on Bengaluru metro and flights - blocks ambient rumble completely. Battery lasted 4 full days of work calls and music. Plush memory-foam cushions don't hurt even after 6 hours continuous wear.",
            date = "28 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 42
        ),
        Review(
            id = "rev-2",
            productId = "p1",
            userName = "Ananya Iyer",
            rating = 5,
            title = "Worth every single rupee! Pure clarity",
            comment = "Deep rich bass without overpowering vocals. Mic quality on Zoom and Google Meet is crystal clear. Seamless dual-pairing with both my MacBook and Android phone.",
            date = "22 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 18
        ),
        Review(
            id = "rev-3",
            productId = "p1",
            userName = "Vikram Malhotra",
            rating = 4,
            title = "Solid build & audio, slightly snug initial fit",
            comment = "Audio tuning is 10/10. Foldable case included in box is very premium. Clamping force was a bit tight on first day but loosened up nicely by day 3.",
            date = "15 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 9
        ),
        Review(
            id = "rev-4",
            productId = "p2",
            userName = "Pooja Hegde",
            rating = 5,
            title = "Accurate step & heart rate tracking",
            comment = "AMOLED screen is super bright even under harsh outdoor sunlight. Battery easily lasts 6-7 days with typical usage. Watch faces are stylish and modern.",
            date = "25 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 27
        ),
        Review(
            id = "rev-5",
            productId = "p2",
            userName = "Amitabh Roy",
            rating = 4,
            title = "Great value smartwatch with fast notifications",
            comment = "Calls and WhatsApp notifications sync immediately without lag. The silicone strap is soft and skin-friendly.",
            date = "18 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 12
        ),
        Review(
            id = "rev-6",
            productId = "p9",
            userName = "Deepak Joshi",
            rating = 5,
            title = "Life-changing read! Mint condition delivery",
            comment = "Must-read for anyone wanting to build solid daily habits. Book arrived in flawless hardcover condition with bubble wrap protection. Super fast dispatch.",
            date = "20 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 35
        ),
        Review(
            id = "rev-7",
            productId = "p14",
            userName = "Kavita Reddy",
            rating = 5,
            title = "Extremely stylish and all-day comfortable",
            comment = "Pairs effortlessly with jeans, chinos and dresses. Sole cushioning is generous, walked 12,000 steps without any fatigue or shoe bites.",
            date = "24 Sep 2026",
            isVerifiedBuyer = true,
            helpfulCount = 21
        )
    )
}

data class Promo(val code: String, val description: String, val discountRate: Double)
