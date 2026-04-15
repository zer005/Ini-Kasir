# Ini-Kasir

Aplikasi **Point of Sale (POS)** Android offline-first untuk kafe dan restoran kecil. Dibangun dengan Kotlin dan dirancang untuk penggunaan single-device dengan performa ringan.

## ✨ Fitur Utama

### 🔐 Role-Based Access
- **Kasir**: Transaksi penjualan, keranjang belanja, pencarian produk
- **Admin**: Manajemen produk, riwayat transaksi, rekap harian, backup data

### 📦 Manajemen Produk dengan Varian
- **Produk Tunggal**: Produk tanpa varian (contoh: Nasi Goreng, Matcha Latte)
- **Produk dengan Varian**: Satu produk dengan banyak pilihan (contoh: Pop Ice dengan berbagai rasa)
- **Manajemen Stok**: Setiap produk/varian memiliki stok terpisah
- **Visual Indicator**: Produk dengan stok habis ditampilkan dalam keadaan disabled (abu-abu)

### 💳 Sistem Transaksi
- **Keranjang Belanja**: Tambah, hapus, update quantity item
- **Detail Transaksi**: Rincian lengkap setiap transaksi (produk, quantity, harga, subtotal)
- **Stock Auto-Update**: Stok otomatis berkurang saat transaksi berhasil
- **Transaction Safety**: Semua operasi transaksi menggunakan atomic transaction untuk data integrity

### 📊 Rekap Harian Manual
- **Rekap Manual**: User menentukan kapan akan melakukan rekap
- **Reset Otomatis**: Setelah rekap, daftar transaksi yang belum direkap dimulai dari nol
- **Riwayat Rekap**: Semua rekap sebelumnya tetap bisa dilihat dengan detail lengkap
- **Detail Rekap**: Periode, total transaksi, total pendapatan

### 💾 Backup & Restore
- **Export CSV**: Backup produk, transaksi, dan detail transaksi ke file CSV
- **Import CSV**: Restore data dari file CSV
- **Local Storage**: Backup disimpan di folder lokal aplikasi

## 🏗️ Arsitektur

### Tech Stack
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34
- **Architecture**: Clean Architecture dengan layered design
  - **Data Layer**: Room Database, DAOs, Repositories
  - **Domain Layer**: Use Cases, Domain Models
  - **Presentation Layer**: Fragments, ViewModels, Adapters
- **UI**: ViewBinding, Material Design Components
- **Async**: Kotlin Coroutines & Flow
- **DI**: Manual Dependency Injection

### Database Schema

```
┌─────────────────────┐
│      products        │
├─────────────────────┤
│ id (PK)             │
│ name                │
│ price               │
│ stock               │
│ parentId (FK)       │ ← null = produk utama
│ variantName         │ ← null = bukan varian
└─────────────────────┘

┌─────────────────────┐
│    transactions      │
├─────────────────────┤
│ id (PK)             │
│ total               │
│ date                │
│ isRecapped          │
│ recapId (FK)        │
└─────────────────────┘

┌─────────────────────┐
│  transaction_details │
├─────────────────────┤
│ id (PK)             │
│ transactionId (FK)  │
│ productId (FK)      │
│ quantity            │
│ price               │
│ subtotal            │
└─────────────────────┘

┌─────────────────────┐
│       recaps         │
├─────────────────────┤
│ id (PK)             │
│ startDate           │
│ endDate             │
│ totalRevenue        │
│ transactionCount    │
│ createdAt           │
└─────────────────────┘
```

## 📱 Cara Penggunaan

### Menambah Produk dengan Varian

1. Buka **Admin** → Tap tombol **+** (FAB)
2. Isi nama produk, harga, dan stok
3. Sistem akan menanyakan apakah produk memiliki varian:
   - **Ya, tambah varian**: Masukkan nama varian (contoh: "Rasa Strawberry", "Rasa Melon")
   - **Tidak, produk tunggal**: Produk langsung disimpan tanpa varian
4. Untuk menambah varian lain, tap **Ya** pada dialog "Tambah varian lain?"

### Melakukan Transaksi (Kasir)

1. Buka **Kasir**
2. Cari produk menggunakan search bar
3. Tap produk untuk menambahkan ke keranjang
   - Jika produk memiliki varian, pilih varian yang diinginkan
   - Produk dengan stok = 0 tidak bisa ditambahkan (disabled, abu-abu)
4. Atur quantity dengan tombol +/- di keranjang
5. Tap **Checkout** untuk memproses transaksi
6. Stok otomatis berkurang setelah transaksi berhasil

### Rekap Harian

1. Buka **Admin** → Tab **Transaksi**
2. Tap tombol **📊 Rekap**
3. Konfirmasi rekap (akan ditampilkan jumlah transaksi dan total)
4. Setelah rekap berhasil:
   - Daftar transaksi yang belum direkap menjadi kosong
   - Rekap disimpan dengan detail periode dan total
5. Untuk melihat riwayat rekap sebelumnya:
   - Tap tombol **📊 Riwayat Rekap**
   - Pilih rekap yang ingin dilihat detailnya

### Backup & Restore Data

1. Buka **Admin** → Tab **Backup**
2. **Export**: Tap tombol **Export CSV** untuk backup semua data
3. **Import**: 
   - Pilih file CSV untuk produk, transaksi, dan detail
   - Tap **Import** untuk restore data

## 🛠️ Struktur Project

```
app/src/main/java/com/inikasir/
├── data/
│   ├── local/
│   │   ├── dao/           # Data Access Objects
│   │   ├── entity/        # Room Entities
│   │   └── AppDatabase.kt
│   └── repository/        # Data Repositories
├── domain/
│   ├── model/             # Domain Models
│   └── usecase/           # Use Cases (Product & Transaction)
└── presentation/
    ├── common/            # Base classes
    ├── kasir/             # Kasir screens (Fragment, ViewModel, Adapters)
    └── admin/             # Admin screens (Product, Transaction, Backup)
```

## 🚀 Build & Run

```bash
# Clone repository
git clone https://github.com/yourusername/Ini-Kasir.git

# Open di Android Studio
# Sync Gradle
# Run ke emulator/device

# Atau via command line:
./gradlew assembleDebug
./gradlew installDebug
```

## 📋 Requirements

- **Android Studio**: Arctic Fox atau lebih baru
- **Gradle**: 8.2
- **Kotlin**: 1.9.22
- **JDK**: 17
- **Min SDK**: 24 (Android 7.0 Nougat)

## 📝 Dependencies Utama

- **Material Design**: 1.11.0
- **Room Database**: 2.6.1
- **Lifecycle Components**: 2.7.0
- **Coroutines**: 1.7.3
- **AppCompat**: 1.6.1
- **ConstraintLayout**: 2.1.4

## 🔧 Fitur yang Sudah Diimplementasi

✅ Sistem produk dengan variant support  
✅ Manajemen stok per produk/varian  
✅ Visual indicator untuk stok habis  
✅ Transaction atomicity (all-or-nothing)  
✅ Stock auto-decrease setelah transaksi  
✅ Cart management dengan quantity control  
✅ Product search  
✅ Manual daily recap  
✅ Recap history dengan detail  
✅ Transaction detail view  
✅ CSV backup & restore  
✅ Role-based UI (Kasir & Admin)  
✅ Responsive design (tablet support)  

## 🐛 Known Issues & Future Improvements

### Perlu Perbaikan
- [ ] Migrasi database yang proper (saat ini `fallbackToDestructiveMigration`)
- [ ] Gunakan `Long` (cents) atau `BigDecimal` untuk monetary values (saat ini `Double`)
- [ ] Implementasi Dependency Injection framework (Hilt/Koin)
- [ ] SingleLiveEvent pattern untuk one-shot events (toast)
- [ ] Persist cart state (saat ini hilang saat process kill)

### Fitur yang Bisa Ditambahkan
- [ ] Thermal printer support untuk struk
- [ ] Barcode scanner integration
- [ ] Multiple payment methods (cash, card, QRIS)
- [ ] Discount & tax management
- [ ] Sales analytics dashboard
- [ ] Multi-user support dengan login
- [ ] Cloud sync untuk backup

## 📄 License

MIT License - lihat file [LICENSE](LICENSE) untuk detail.

## 👥 Credits

Dibuat untuk kebutuhan kasir café dengan fokus pada kesederhanaan dan performa.

