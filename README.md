# Avenue & Co 🛍️

A modern, feature-rich **Multi-Vendor E-Commerce Android Application** built with **Jetpack Compose**, **Material 3**, and **Room Database** following clean architecture principles.

---

## ✨ Features

### 🛒 Customer Experience
- **Interactive Marketplace**: Browse and discover curated products across multiple vendor storefronts.
- **Advanced Search & Filtering**: Instant search, category catalogs, price sorting, and rating filters.
- **Cart & Wishlist**: Real-time price calculation, stock validation, promo codes, and persistent wishlist.
- **Order Lifecycle & Tracking**: Visual step-by-step order tracking from payment confirmation to door delivery.
- **Reviews & Ratings**: Verified customer ratings, feedback, and vendor trust scores.
- **Notification Hub**: Order status notifications, seller updates, and special discounts.

### 🏪 Vendor & Seller Dashboard
- **Product Management**: Add, update, and manage inventory with prices, categories, and image URLs.
- **Order Processing**: Real-time view of customer orders and dispatch status updates.
- **Seller Analytics**: Overview of store performance, total revenue, and active listings.

### 🔒 Security & Data
- **Offline-First Persistence**: Room Database for robust local caching of products, categories, cart, and orders.
- **Authentication**: Secure registration, login, input validation, and salted password hashing.
- **Preferences**: Jetpack DataStore for user sessions and app settings.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose & Material 3
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Database**: Room (Entities, DAOs, Type Converters)
- **State Management**: Kotlin Coroutines & StateFlow
- **Image Loading**: Coil Compose
- **Serialization**: Kotlinx Serialization JSON
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`)

---

## 📂 Project Structure

```
MultiVendor/
├── app/
│   ├── src/main/java/com/example/multivendor/
│   │   ├── data/
│   │   │   ├── auth/          # Authentication & Security
│   │   │   ├── local/         # Room Database, DAOs & Entities
│   │   │   └── Models.kt      # Core Domain Models
│   │   ├── theme/             # Material 3 Color Schemes & Typography
│   │   └── ui/
│   │       ├── auth/          # Login & Registration Screens
│   │       ├── cart/          # Cart & Checkout Screens
│   │       ├── checkout/      # Multi-step Checkout
│   │       ├── components/    # Reusable Compose Components
│   │       ├── orders/        # Orders & Real-time Tracking
│   │       ├── product/       # Product Details & Reviews
│   │       ├── profile/       # User Profile & Address Management
│   │       ├── seller/        # Vendor Dashboard & Management
│   │       └── shop/          # Marketplace & Catalog
└── gradle/                    # Gradle Wrapper & Version Catalog
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2+ (or newer)
- Android SDK 36
- JDK 17+

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Priyyanxhu/Avenue-Co.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync project dependencies.
4. Run on an Android Emulator or physical device (API 24+).

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
