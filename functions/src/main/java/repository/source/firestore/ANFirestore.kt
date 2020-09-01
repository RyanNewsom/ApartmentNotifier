package repository.source.firestore
import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient
import model.ApartmentUnit
import model.FirestoreConstants
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.CollectionReference
import repository.source.ApartmentCache
import java.util.logging.Logger

class ANFirestore : ApartmentCache {
    private val logger = Logger.getLogger(ANFirestore::class.java.name)
    private val db: Firestore

    init {
        val credentials = GoogleCredentials.getApplicationDefault()
        val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId("apartmentnotifier")
                .build()
        if (FirebaseApp.getApps().size == 0) {
            FirebaseApp.initializeApp(options)
        }
        db = FirestoreClient.getFirestore()
    }

    override fun fetchData() : List<ApartmentUnit> {
        // Use the application default credentials
        val existingUnits = mutableListOf<ApartmentUnit>()
        val query = db.collection("units").get()
        val querySnapshot = query.get()
        val documents = querySnapshot.documents
        for (document in documents) {
            existingUnits.add(ApartmentUnit(
                    id = document[FirestoreConstants.UNIT_ID] as? String ?: "unknown",
                    unitNumber = document[FirestoreConstants.UNIT_NUMBER] as? String ?: "unknown",
                    area = document[FirestoreConstants.AREA] as? Int ?: 0,
                    price = document[FirestoreConstants.PRICE] as? Int ?: 0,
                    availableOn = document[FirestoreConstants.AVAILABLE_ON] as? String ?: "unknown"
            ))
        }

        return existingUnits
    }

    override fun updateApartments(apartments: List<ApartmentUnit>) {
        logger.info("Updating apartments...")
        deleteCollection(db.collection("units"))
        apartments.forEach { apartment ->
            db.collection("units").add(
                    mapOf(
                            FirestoreConstants.UNIT_ID to apartment.id,
                            FirestoreConstants.UNIT_NUMBER to apartment.unitNumber,
                            FirestoreConstants.AREA to apartment.area,
                            FirestoreConstants.PRICE to apartment.price,
                            FirestoreConstants.AVAILABLE_ON to apartment.availableOn
                    )
            ).get()
        }
    }

    /** Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    private fun deleteCollection(collection: CollectionReference, batchSize: Int = 1000) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            val future = collection.limit(batchSize).get()
            var deleted = 0
            val documents = future.get().documents
            for (document in documents) {
                document.reference.delete()
                ++deleted
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize)
            }
        } catch (e: Exception) {
            logger.warning("Error deleting collection : " + e.message)
        }
    }

}