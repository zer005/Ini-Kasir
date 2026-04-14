# 🏪 Ini-Kasir

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple?logo=kotlin)
![Android](https://img.shields.io/badge/Android-8.0%2B-green?logo=android)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Build](https://img.shields.io/badge/Build-Gradle_CLI-orange)

**Ini-Kasir** adalah aplikasi Point of Sale (POS) untuk café kecil yang:
- ✅ Berjalan **100% offline**
- ✅ Ringan & cepat (build dengan Gradle CLI)
- ✅ Modular & maintainable (Clean Architecture + MVVM)
- ✅ Siap scaling ke backend (Laravel)

---

## 📱 **Fitur Utama**

| Fase | Fitur | Status |
|------|-------|--------|
| 1 | Manajemen Produk (CRUD) | ✅ Ready |
| 1 | Transaksi dengan Cart | ✅ Ready |
| 1 | Riwayat Transaksi | ✅ Ready |
| 2 | Backup CSV & Google Drive | 🚧 WIP |
| 3 | Bluetooth Thermal Printer | 📅 Planned |
| 4 | Backend Sync (Laravel) | 📅 Planned |

---

## 🛠️ **Tech Stack**

| Layer | Technology |
|-------|------------|
| UI | XML + ViewBinding |
| Architecture | MVVM + Clean Architecture |
| Local DB | Room Database (SQLite) |
| Async | Kotlin Coroutines + Flow |
| DI | Manual (no Dagger/Hilt for simplicity) |
| Backup | OpenCSV + Google Drive API |

---

## 📂 **Project Structure (Modular)**
