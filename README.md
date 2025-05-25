# AdvProg - B01

## Module 9
## Transaksi
> Siti Shofi Nadhifa - 2306152172
### Component Diagram
![Component Diagram](assets/img/componentdiagram-transaction.png)
### Code Diagram
![Code Diagram](assets/img/codediagram-transaction.png)

> Raja Rafael Pangihutan Sitorus - 2306244923
### Component Diagram
![Screenshot 2025-05-16 at 18.32.54.png](assets/img/Raja_Component_Diagram.png)
### Code Diagram
![Screenshot 2025-05-16 at 18.28.10.png](assets/img/Raja_Code_Diagram.png)

## Profiling
> Siti Shofi Nadhifa - 2306152172

![Profiling](assets/img/profiling-1.png)
![Profiling](assets/img/profiling-2.png)
![Profiling](assets/img/profiling-3.png)
Profiling menunjukkan bahwa method `filterTransactions()` di `TransactionServiceImpl` menjadi bottleneck utama karena menghabiskan waktu eksekusi hingga 23 detik, dengan sebagian besar waktu terpakai di pemanggilan `transactionRepository.findTransactionsWithFilters(...)` dan proses konversi ke `TransactionDTO`. Hal ini kemungkinan disebabkan oleh query database yang tidak efisien, tidak menggunakan indeks yang tepat, atau memuat terlalu banyak data ke dalam memori, ditambah dengan sorting yang dilakukan di sisi Java. Untuk meningkatkan performa, disarankan agar sorting dilakukan langsung di level query, penggunaan pagination diterapkan, dan pemetaan DTO dioptimalkan agar lebih ringan.
