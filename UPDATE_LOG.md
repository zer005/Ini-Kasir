# Update Log

## v1.1.0 - 2026-04-15

### ✨ Fitur Baru

#### 1. Sistem Produk dengan Varian
- **Deskripsi**: Produk sekarang bisa memiliki varian (contoh: Pop Ice dengan berbagai rasa)
- **Cara Kerja**: 
  - Produk utama memiliki `parentId = null` dan `variantName = null`
  - Varian memiliki `parentId = <id_produk_utama>` dan `variantName = <nama_varian>`
  - Satu produk utama bisa memiliki banyak varian
- **UI**: Badge "Varian" ditampilkan pada produk yang merupakan varian
- **Files Changed**:
  - `data/local/entity/ProductEntity.kt` - Sudah ada fields: `parentId`, `variantName`
  - `data/local/dao/ProductDao.kt` - Queries: `getMainProducts()`, `getVariants(parentId)`
  - `presentation/admin/ProductManagementFragment.kt` - Dialog untuk tambah produk dengan varian
  - `presentation/admin/ProductManagementAdapter.kt` - Display variant badge
  - `domain/usecase/product/AddProductUseCase.kt` - Support `parentId` & `variantName` params
  - `domain/usecase/product/UpdateProductUseCase.kt` - Support variant updates

#### 2. Manajemen Stok Produk
- **Deskripsi**: Setiap produk/varian memiliki stok yang dikelola terpisah
- **Fitur**:
  - Field stok ditampilkan di product list dan management
  - Produk dengan stok = 0 ditampilkan disabled (abu-abu, tidak bisa diklik)
  - Stok otomatis berkurang saat transaksi berhasil
  - Validasi stok sebelum transaksi diproses
- **Visual**: 
  - Stock badge: "Stok: X" (normal) atau "Habis" (merah)
  - Alpha 0.5f untuk produk dengan stok habis
- **Files Changed**:
  - `data/local/dao/ProductDao.kt` - `decreaseStock()`, `increaseStock()`
  - `data/repository/ProductRepository.kt` - Stock management methods
  - `presentation/kasir/ProductGridAdapter.kt` - Stock indicator & disabled state
  - `res/layout/item_product_grid.xml` - Stock TextView
  - `res/layout/item_product_management.xml` - Stock display

#### 3. Rekap Harian Manual dengan History
- **Deskripsi**: Admin bisa melakukan rekap transaksi secara manual, dengan history yang bisa dilihat kembali
- **Fitur**:
  - **Rekap Manual**: Tap tombol "📊 Rekap" untuk merekap semua transaksi yang belum direkap
  - **Reset Otomatis**: Setelah rekap, transaksi yang sudah direkap ditandai dan list dimulai dari nol
  - **History Rekap**: Tap "📊 Riwayat Rekap" untuk melihat semua rekap sebelumnya
  - **Detail Rekap**: Periode, jumlah transaksi, total pendapatan
- **Database**: 
  - `transactions.isRecapped` flag untuk tandai transaksi yang sudah direkap
  - `transactions.recapId` FK ke `recaps.id`
  - `recaps` table menyimpan summary setiap rekap
- **Files Changed**:
  - `data/local/dao/TransactionDao.kt` - `markAsRecapped()`, `getUnrecappedTransactions()`, `getTransactionsByRecapId()`
  - `data/local/dao/RecapDao.kt` - `getAllRecaps()`, `getRecapById()`
  - `data/repository/TransactionRepository.kt` - `createRecap()`, `getAllRecaps()`
  - `domain/usecase/transaction/GetRecapsUseCase.kt` - **BARU** - Use case untuk ambil history rekap
  - `presentation/admin/AdminViewModel.kt` - `recaps` LiveData, `createRecap()`
  - `presentation/admin/TransactionHistoryFragment.kt` - `showRecapHistoryDialog()`, `showRecapDetailDialog()`
  - `presentation/admin/AdminViewModelFactory.kt` - Inject `GetRecapsUseCase`

#### 4. Detail Transaksi dengan Varian
- **Deskripsi**: Detail transaksi sekarang menampilkan nama produk dengan varian (jika ada)
- **Format**: 
  - Produk tunggal: "Nasi Goreng"
  - Produk dengan varian: "Pop Ice (Rasa Strawberry)"
- **Files Changed**:
  - `data/local/dao/TransactionDetailDao.kt` - `getDetailsWithProduct()` includes `variantName`
  - `data/local/dao/TransactionDetailWithProduct.kt` - Data class dengan `variantName` field
  - `presentation/admin/TransactionDetailAdapter.kt` - Display logic untuk variant
  - `presentation/admin/TransactionHistoryFragment.kt` - Pass variant info ke dialog

### 🔧 Perbaikan Bug

#### 1. Transaction Atomicity (CRITICAL FIX)
- **Masalah**: `createTransaction()` tidak dalam transaction wrapper, partial failure bisa bikin data inconsistent (transaksi tercatat tapi stock tidak berkurang)
- **Solusi**: Wrap semua operasi dalam `database.runInTransaction {}` block
- **Files Changed**:
  - `data/repository/TransactionRepository.kt` - `createTransaction()` sekarang dalam `runInTransaction`
  - Validasi stock dilakukan SEBELUM transaction dimulai
  - Jika stock tidak cukup, exception dilempar dan semua operasi di-rollback

#### 2. Null Pointer Exception pada SUM Query
- **Masalah**: `getUnrecappedTotal()` mengembalikan `Double` yang bisa null saat tidak ada transaksi (SQLite SUM returns null on empty set)
- **Solusi**: Gunakan `COALESCE(SUM(total), 0.0)` untuk return 0.0 saat null
- **Files Changed**:
  - `data/local/dao/TransactionDao.kt` - Query updated dengan COALESCE

#### 3. Cart Clear Sebelum Transaksi Berhasil
- **Masalah**: `clearCart()` dipanggil sebelum transaction dikonfirmasi, jika transaction gagal cart sudah kosong
- **Solusi**: Pindah `clearCart()` ke dalam try block setelah transaction berhasil
- **Files Changed**:
  - `presentation/kasir/KasirViewModel.kt` - Urutan: transaction → clearCart → success result

### 🎨 UI/UX Improvements

1. **Product Display**:
   - Variant badge dengan background color
   - Stock indicator dengan color coding (gray = available, red = empty)
   - Disabled state untuk produk habis (alpha 0.5f)
   
2. **Dialog Enhancements**:
   - Dialog tambah produk sekarang menanyakan apakah produk punya varian
   - Dialog tambah varian dengan auto-fill harga dan stock dari produk utama
   - Option untuk tambah varian lain setelah menambah varian pertama

3. **String Resources**:
   - Semua hardcoded strings dipindah ke `strings.xml`
   - Tambah strings untuk product, transaction, recap, dan dialog messages

### 📦 Files Added
- `domain/usecase/transaction/GetRecapsUseCase.kt` - Use case untuk recap history
- `res/drawable/bg_variant_badge.xml` - Background drawable untuk variant badge

### 📝 Files Modified (Summary)
- **Data Layer**: 4 files (DAOs, Repository)
- **Domain Layer**: 3 files (Use Cases)
- **Presentation Layer**: 6 files (ViewModels, Fragments, Adapters)
- **Resources**: 3 files (layouts, strings, drawables)

### ⚠️ Breaking Changes
- **Database Version**: Tetap version 2 (tidak ada schema change, hanya logic update)
- **API Compatibility**: Semua changes backward compatible

### 🐛 Known Issues yang Masih Ada
- [ ] `fallbackToDestructiveMigration()` masih aktif - akan hilang data saat schema change
- [ ] Monetary values masih pakai `Double` - floating point precision risk
- [ ] No DI framework - manual dependency injection
- [ ] Cart state tidak persist - hilang saat process kill

### 📋 Testing Checklist
- [ ] Tambah produk tunggal dengan stock
- [ ] Tambah produk dengan varian (2+ varian)
- [ ] Edit produk dan varian
- [ ] Hapus produk dengan varian
- [ ] Transaksi dengan produk tunggal
- [ ] Transaksi dengan produk varian
- [ ] Transaksi dengan stock tidak cukup (harus fail)
- [ ] Transaksi dengan banyak item (atomicity test)
- [ ] Rekap transaksi kosong
- [ ] Rekap transaksi ada isi
- [ ] Lihat history rekap
- [ ] Lihat detail rekap
- [ ] Export CSV
- [ ] Import CSV

---

## v1.0.0 - Initial Release

### Fitur
- ✅ Basic product management (CRUD)
- ✅ Kasir dengan cart system
- ✅ Transaction recording
- ✅ Basic recap functionality
- ✅ CSV backup/restore
- ✅ Role-based UI (Admin & Kasir)
- ✅ Offline-first dengan Room Database
