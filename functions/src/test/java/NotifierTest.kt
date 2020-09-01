import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import repository.source.ApartmentCache
import repository.source.ReadableDataSource
import java.util.logging.Logger

import com.google.cloud.functions.HttpResponse
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import model.ApartmentUnit

@RunWith(JUnit4::class)
class NotifierTest {
    private lateinit var notifier: Notifier

    @MockK(relaxUnitFun = true)
    lateinit var mockCache: ApartmentCache
    @MockK
    lateinit var mockApartmentDataSource: ReadableDataSource
    @RelaxedMockK
    lateinit var mockEmailer: Emailer
    @RelaxedMockK
    lateinit var mockLogger: Logger

    private var currentId: Int = 0

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        currentId = 0
    }

    @Test
    fun `Given new units are found, When the service is invoked, Then an email is sent and the cache is updated`() {
        //Given
        val cachedUnits = listOf(
                ApartmentUnit(
                        id = "${currentId++}",
                        unitNumber = "508",
                        area = 1000,
                        price = 777,
                        availableOn = "2020-08-29"
                )
        )
        every { mockCache.fetchData() } returns cachedUnits

        val currentUnits = cachedUnits.plus(
                listOf(
                        ApartmentUnit(
                                id = "${currentId++}",
                                unitNumber = "100",
                                area = 1000,
                                price = 900,
                                availableOn = "2020-02-29"
                        )
                )
        )
        every { mockApartmentDataSource.fetchData() } returns currentUnits

        notifier = Notifier(
                apartmentsCache = mockCache,
                apartmentsLiveDataSource = mockApartmentDataSource,
                emailer = mockEmailer,
                logger = mockLogger
        )

        //When
        val response = mockk<HttpResponse>(relaxed = true)
        notifier.service(mockk(), response)

        //Then
        verify {
            response.writer.write("New units found? true")
        }

        verify {
            mockEmailer.sendEmail(any(), "New Apartments at Luma")
        }

        verify {
            mockCache.updateApartments(
                currentUnits
            )
        }
    }

    @Test
    fun `Given no new units are found, When the service is invoked, Then no email is sent and the cache is not updated`() {
        //Given
        val cachedUnits = listOf(
                ApartmentUnit(
                        id = "${currentId++}",
                        unitNumber = "508",
                        area = 1000,
                        price = 777,
                        availableOn = "2020-08-29"
                )
        )
        every { mockCache.fetchData() } returns cachedUnits

        val currentUnits = cachedUnits.map {
            it.copy()
        }
        every { mockApartmentDataSource.fetchData() } returns currentUnits

        notifier = Notifier(
                apartmentsCache = mockCache,
                apartmentsLiveDataSource = mockApartmentDataSource,
                emailer = mockEmailer,
                logger = mockLogger
        )

        //When
        val response = mockk<HttpResponse>(relaxed = true)
        notifier.service(mockk(), response)

        //Then
        verify {
            response.writer.write("New units found? false")
        }

        verify {
            mockEmailer wasNot called
        }

        verify(exactly = 1) {
            mockCache.fetchData()
        }

        verify(exactly = 0) {
            mockCache.updateApartments(any())
        }
    }
}