
import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import repository.source.firestore.ANFirestore
import repository.source.retrofit.HttpsSourceReadable
import java.io.IOException
import java.util.logging.Logger
import kotlin.jvm.Throws
import repository.source.ApartmentCache
import repository.source.ReadableDataSource

class Notifier(
        private val apartmentsCache: ApartmentCache = ANFirestore(),
        private val apartmentsLiveDataSource: ReadableDataSource = HttpsSourceReadable(),
        private val unitComparer: UnitComparer = UnitComparerImpl(),
        private val emailer: Emailer = EmailerImpl(),
        private val logger: Logger = Logger.getLogger(Notifier::class.java.name)
) : HttpFunction {

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {
        val existingUnits = apartmentsCache.fetchData()
        val currentUnits = apartmentsLiveDataSource.fetchData()
        logger.info("Fetched existing units, got back: ${existingUnits.size}")
        logger.info("Fetched current units, for back: ${currentUnits.size}")

        val newUnits = unitComparer.getNewUnits(oldData = existingUnits, newData = currentUnits)
        if(newUnits.isNotEmpty()) {
            var newUnitsEmail = ""
            logger.info("NEW UNITS FOUND: ${newUnits.size}")
            newUnits.forEach {
                newUnitsEmail += """
                unitNumber: ${it.unitNumber}
                price: ${it.price}
                area: ${it.area}
                availableOn: ${it.availableOn}
                """.trimIndent() + "\n\n"
            }

            emailer.sendEmail(message = newUnitsEmail, subject = "New Apartments at Luma")

            apartmentsCache.updateApartments(currentUnits)
        }

        response.writer.write("New units found? ${newUnits.isNotEmpty()}")
    }
}