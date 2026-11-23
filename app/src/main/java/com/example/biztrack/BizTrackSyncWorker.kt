package com.example.biztrack

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class BizTrackSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.success()

        val localRepo = BizTrackLocalRepository(applicationContext)
        val db = FirebaseFirestore.getInstance()

        try {
            // 1. Sync unsynced products
            val unsyncedProductIds = localRepo.getUnsyncedProductIds(userId)
            val products = localRepo.getProducts(userId)
            for (prodId in unsyncedProductIds) {
                val product = products.find { it.id == prodId }
                if (product != null) {
                    db.collection("users").document(userId).collection("products")
                        .document(product.id).set(product.toMap()).await()
                    localRepo.clearProductUnsynced(userId, product.id)
                }
            }

            // 2. Sync unsynced sales
            val unsyncedSaleIds = localRepo.getUnsyncedSaleIds(userId)
            val sales = localRepo.getSales(userId)
            for (saleId in unsyncedSaleIds) {
                val sale = sales.find { it.id == saleId }
                if (sale != null) {
                    db.collection("users").document(userId).collection("sales")
                        .document(sale.id).set(sale.toMap()).await()
                    localRepo.clearSaleUnsynced(userId, sale.id)
                }
            }

            // 3. Sync unsynced expenses
            val unsyncedExpenseIds = localRepo.getUnsyncedExpenseIds(userId)
            val expenses = localRepo.getExpenses(userId)
            for (expId in unsyncedExpenseIds) {
                val expense = expenses.find { it.id == expId }
                if (expense != null) {
                    db.collection("users").document(userId).collection("expenses")
                        .document(expense.id).set(expense.toMap()).await()
                    localRepo.clearExpenseUnsynced(userId, expense.id)
                }
            }

            Log.d("BizTrackSyncWorker", "Background offline sync completed successfully for user $userId")
            return Result.success()
        } catch (e: Exception) {
            Log.e("BizTrackSyncWorker", "Error performing background offline sync", e)
            return Result.retry()
        }
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "BizTrackPeriodicOfflineSync"
        private const val ONE_TIME_WORK_NAME = "BizTrackImmediateOfflineSync"

        fun scheduleSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workManager = WorkManager.getInstance(context)

            // Schedule immediate one-time sync whenever network is connected
            val oneTimeRequest = OneTimeWorkRequestBuilder<BizTrackSyncWorker>()
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )

            // Schedule periodic background sync every 15 minutes when connected
            val periodicRequest = PeriodicWorkRequestBuilder<BizTrackSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}
