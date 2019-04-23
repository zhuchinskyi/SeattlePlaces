package com.dzhucinski.seattleplaces.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dzhucinski.seattleplaces.network.Venue
import com.dzhucinski.seattleplaces.repository.FavoritesRepository
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import com.dzhucinski.seattleplaces.repository.PlacesResponse
import com.dzhucinski.seattleplaces.repository.VenueResponse
import com.dzhucinski.seattleplaces.search.SearchViewModel
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentMatchers.anyString

/**
 * Created by Denis Zhuchinski on 4/15/19.
 */
class SearchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var placesRepository: PlacesRepository

    @Mock
    lateinit var favoritesRepository: FavoritesRepository

    private lateinit var searchViewModel: SearchViewModel

    private val venue = Venue(
        ID, TITLE, DESCRIPTION, null, URL,
        Venue.Location(ADDRESS, null, LAT, LNG, POSTAL_CODE, "", CITY, STATE, COUNTRY), null, null,
        listOf(
            Venue.Category(
                IMAGE_ID,
                IMAGE_CATEGORY_NAME,
                Venue.Category.CategoryIcon(
                    IMAGE_PREFIX,
                    IMAGE_SUFFIX
                ),
                true
            )
        )
    )
    private val venueResponse = VenueResponse(venue, null)
    private val venueResponseLiveData = MutableLiveData<VenueResponse>()
    private val placesResponse = PlacesResponse(listOf(venue), "")

    private val favoriteResponse = MutableLiveData<Set<String>>()
    private val searchResultLiveData = MutableLiveData<PlacesResponse>()


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        venueResponseLiveData.value = venueResponse
        searchResultLiveData.value = placesResponse
        favoriteResponse.value = SAVED_IDs

        whenever(placesRepository.getSearchResultLiveData()).thenReturn(searchResultLiveData)
        whenever(placesRepository.getDetails(anyString())).thenReturn(venueResponseLiveData)
        whenever(favoritesRepository.getItemIds()).thenReturn(favoriteResponse)

        searchViewModel = SearchViewModel(placesRepository, favoritesRepository)
    }

    @Test
    fun `test initial live data state`() {
        assertEquals(false, searchViewModel.progressLiveData.value)
        assertEquals(emptyList<SearchViewModel.VenueItem>(), searchViewModel.venuesLiveData.value)
        assertTrue(searchViewModel.errorLiveData.value.isNullOrBlank())
    }

    @Test
    fun `test view model's query params`() {
        searchViewModel.performSearch(QUERY)

        verify(placesRepository).search(QUERY, SEARCH_LOCATION, DEFAULT_ITEMS_LIMIT)
    }

    @Test
    fun `test view model's search results`() {
        whenever(placesRepository.search(anyString(), anyString(), ArgumentMatchers.anyInt())).then {
            placesRepository.getSearchResultLiveData() as MutableLiveData
            (placesRepository.getSearchResultLiveData() as MutableLiveData).value = placesResponse
            it
        }

        assertEquals(false, searchViewModel.progressLiveData.value)

        searchViewModel.performSearch(QUERY)

        searchViewModel.venuesLiveData.observeForever {
            assertEquals(false, searchViewModel.progressLiveData.value)
            // after mapping from network to view model
            val viewModelVenueItem = it[0]

            assertEquals(viewModelVenueItem.isSelected, true)
            assertEquals(viewModelVenueItem.id, ID)
            assertEquals(viewModelVenueItem.title, TITLE)
            assertEquals(viewModelVenueItem.description, DESCRIPTION)
            assertEquals(viewModelVenueItem.address, ADDRESS)
            assertEquals(viewModelVenueItem.categoryTitle, IMAGE_CATEGORY_NAME)
            assertEquals(viewModelVenueItem.distanceTitle, DISTANCE_TITLE)
            assertEquals(viewModelVenueItem.lat, LAT, 0.0)
            assertEquals(viewModelVenueItem.lng, LNG, 0.0)
            assertEquals(viewModelVenueItem.url, URL)
            assertEquals(viewModelVenueItem.imageUrl, IMAGE_PREFIX + IMAGE_SIZE + IMAGE_SUFFIX)
        }
    }

    companion object {

        val SAVED_IDs = setOf("52d456c811d24128cdd7bc8b", "57e95a82498e0a3995a43e90")

        const val QUERY = "Coffee"

        const val ID = "57e95a82498e0a3995a43e90"
        const val TITLE = "Anchorhead Coffee Co"
        const val CITY = "Seattle"
        const val STATE = "WA"
        const val COUNTRY = "USA"
        const val LAT = 47.61340942776967
        const val LNG = -122.33469499761385
        const val POSTAL_CODE = "98101"
        const val DESCRIPTION =
            "Modern, warm Italian eatery with small plates plus pizzas from a blue-tiled wood-burning oven."
        const val URL = "http://cherryst.com"
        const val ADDRESS = "1600 7th Ave Ste 105"

        const val IMAGE_ID = "4bf58dd8d48988d1e0931735"
        const val IMAGE_CATEGORY_NAME = "Coffee Shop"
        const val IMAGE_PREFIX = "https://ss3.4sqi.net/img/categories_v2/food/coffeeshop_"
        const val IMAGE_SUFFIX = ".png"
        const val IMAGE_SIZE = 88

        const val DISTANCE_TITLE = "0.51 miles of the city center"
        const val SEARCH_LOCATION = "Seattle,+WA"
        const val DEFAULT_ITEMS_LIMIT = 30
    }
}