# Aplikasi Story App - Submission Akhir (⭐⭐⭐⭐⭐)

## 📋 Checklist

- ✅ **Mempertahankan Fitur dari Submission Sebelumnya.**
- ✅ **Menampilkan daftar cerita dalam bentuk peta dengan benar.**
- ✅ **Menampilkan list story dengan menggunakan Paging 3 dengan benar.**
- ✅ **Menerapkan unit test.**

## 🎨 Fitur Utama

- **Fitur-Fitur Sebelumnya:** Semua fitur dari Submission Awal dipertahankan, termasuk autentikasi pengguna, manajemen sesi, daftar cerita, halaman detail, dan penambahan cerita baru.
- **Daftar Cerita di Peta:** Aplikasi menampilkan daftar cerita dalam bentuk peta, memanfaatkan Google Maps API atau library lainnya untuk memvisualisasikan lokasi dari cerita yang tersedia.
- **Paging 3:** Implementasi Paging 3 untuk menampilkan list story dengan lebih efisien, memungkinkan pengguna untuk memuat data secara bertahap saat mereka menggulir halaman.
- **Unit Testing:** Aplikasi telah dilengkapi dengan unit test untuk memastikan setiap komponen dan fitur berfungsi dengan benar dan stabil.

## 🖥️ Teknologi yang Digunakan

- **Kotlin/Java** untuk pengembangan Android.
- **RecyclerView dengan Paging 3** untuk menampilkan daftar cerita dengan pagination.
- **Retrofit** untuk integrasi API dan pengambilan data cerita.
- **Google Maps API** untuk menampilkan cerita dalam bentuk peta.
- **SharedPreferences** untuk menyimpan data sesi dan token.
- **Custom View** untuk membuat EditText yang sesuai kriteria.
- **Material Design** untuk tampilan yang modern dan responsif.
- **JUnit/Mockito** untuk penerapan unit test.
- **Lottie/Animators** untuk animasi dalam aplikasi.

## 📸 Screenshots

| Halaman Peta | Halaman List Story (Paging) |
|--------------|--------------|
| ![Halaman Peta](https://github.com/user-attachments/assets/04133a0d-09a8-44b9-aef1-1fb543657da6)| ![Halaman List Story](https://github.com/user-attachments/assets/434cdbf5-5e3a-43bf-8b8f-41296a41652f) | 

## 🚀 Instalasi

1. Clone repositori ini:

   ```bash
   git clone https://github.com/username/repo-name.git
   Buka proyek di Android Studio.

2. Mengganti API_KEY pada android.manifest:
   
   ```bash
   Buka file AndroidManifest.xml.
   Cari placeholder API_KEY.
   Ganti dengan kunci API Anda yang benar dari Google Cloud Console:
   xml
   Copy code
   <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY"/>
   
3. Jalankan aplikasi di emulator atau perangkat fisik.
