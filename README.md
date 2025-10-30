# PergiYuk Android

Versi aplikasi Android dari proyek web *PergiYuk*, dibangun dengan Kotlin, Jetpack Compose, dan Google Maps untuk Android. Aplikasi ini mempertahankan alur kerja web: pengguna memberikan prompt atau memakai lokasi saat ini, aplikasi memanggil Gemini (Generative AI) untuk menyusun itinerary harian, lalu menampilkannya pada peta beserta garis penghubung setiap perhentian. Timeline juga tersedia dalam bentuk lembar bawah (bottom sheet) dan daftar kartu.

## Fitur utama
- Peta Google Maps dengan marker berurutan dan polyline tiap rute.
- Text prompt untuk permintaan itinerary plus tombol memakai lokasi saat ini.
- Integrasi Gemini function calling agar struktur data itinerary sesuai versi web.
- Tampilan kartu dan timeline yang bisa diekspor / dibagikan.
- Sampel itinerary lokal ketika API key belum diisi, memudahkan preview.

## Menjalankan proyek
1. **Buka di Android Studio** (Ladybug atau lebih baru direkomendasikan).
2. Biarkan Android Studio men-download Gradle Wrapper ketika diminta, atau jalankan manual:
   ```bash
   gradle wrapper --gradle-version 8.9
   ```
3. Isi API key pada berkas `secrets.properties` (buat di root proyek) atau lewat environment variable saat build:
   ```properties
   MAPS_API_KEY=YOUR_ANDROID_MAPS_KEY
   GEMINI_API_KEY=YOUR_GEMINI_KEY
   ```
   Anda bisa menyalin `secrets.defaults.properties` sebagai template. Bila menjalankan dari CLI, Anda juga dapat mengeksekusi `MAPS_API_KEY=... GEMINI_API_KEY=... ./gradlew assembleDebug`.
4. Sinkronkan Gradle, lalu jalankan aplikasi pada emulator atau perangkat fisik.

## Catatan integrasi
- Peta menggunakan `maps-compose` sehingga kompatibel dengan Jetpack Compose UI.
- Tombol "Lokasi Saya" memanfaatkan `FusedLocationProviderClient`. Perlu izin lokasi runtime.
- Class `GenAiClient` menyalin deklarasi fungsi (`location`, `line`) dari versi web sehingga keluaran AI tetap konsisten.

Struktur kode utama berada di `app/src/main/java/com/example/mapsplanner/`.
