package com.dzhucinski.seattleplaces.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dzhucinski.seattleplaces.di.appModule
import com.dzhucinski.seattleplaces.network.Venue
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import com.dzhucinski.seattleplaces.repository.VenueResponse
import com.dzhucinski.seattleplaces.search.SearchViewModel
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by Denis Zhuchinski on 4/15/19.
 */
class DetailViewModelTest : KoinTest {

    private val detailViewModel: DetailViewModel by inject()
    private val placesRepository: PlacesRepository by inject()

    @Mock
    lateinit var placesData: Observer<List<VenueResponse>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        startKoin { appModule }
    }

    @Test
    fun `test search method`() {

        val venueResponse = MutableLiveData<VenueResponse>()
        venueResponse.value = VenueResponse(
            Venue("123", "TestName", "Descr", null, "wewqe.com", null, null, null, null)
            , null
        )

        declareMock<PlacesRepository> {
            given(this.getDetails("testId")).willReturn(venueResponse)
        }

    }
}