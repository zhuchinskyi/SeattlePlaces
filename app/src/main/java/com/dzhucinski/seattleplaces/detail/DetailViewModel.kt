package com.dzhucinski.seattleplaces.detail

import androidx.lifecycle.ViewModel
import android.provider.Browser
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import com.dzhucinski.seattleplaces.repository.PlacesRepository
import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import com.dzhucinski.seattleplaces.BuildConfig
import com.dzhucinski.seattleplaces.R
import com.dzhucinski.seattleplaces.network.Venue
import com.dzhucinski.seattleplaces.repository.FavoritesRepository
import com.dzhucinski.seattleplaces.search.SEATTLE_LAT
import com.dzhucinski.seattleplaces.search.SEATTLE_LNG
import com.dzhucinski.seattleplaces.util.ResourceProvider
import kotlinx.coroutines.*
import java.lang.Exception


/**
 * Created by Denis Zhuchinski on 4/12/19.
 */

const val STATIC_MAP_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap"
const val DEFAULT_MAP_IMAGE_SIZE = "400x400"
const val DEFAULT_MARKER_CENTER = "Seattle"
const val DEFAULT_ZOOM = 13
const val DEFAULT_SCALE = 1

class DetailViewModel(
    private val id: String,
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val parentJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + parentJob)
    private val bgScope = CoroutineScope(Dispatchers.IO + parentJob)

    private var urlStr: String = ""
    private var phoneStr: String = ""

    val isSelected = ObservableField<Boolean>()
    val title = ObservableField<String>()
    val description = ObservableField<String>()
    val phone = ObservableField<String>()
    val price = ObservableField<String>()
    val hours = ObservableField<String>()
    val mapUrl = ObservableField<String>()
    val url = ObservableField<String>()

    val errorLiveData = MediatorLiveData<String>()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        bgScope.launch {
            try {
                val detailsResponse = placesRepository.getDetails(id)
                val isInFavorites = favoritesRepository.isInFavorites(id) != null

                withContext(Dispatchers.Main + parentJob) {
                    isSelected.set(isInFavorites)


                    val details = detailsResponse.body()

                    if (!detailsResponse.isSuccessful || details == null) {
                        errorLiveData.value = resourceProvider.getString(R.string.error_msg)
                    } else {
                        val venue = details.response.venue

                        updateView(venue)
                    }
                }
            } catch (e: Exception) {
                errorLiveData.value = resourceProvider.getString(R.string.error_msg)
            }
        }
    }

    private fun updateView(venue: Venue) {
        urlStr = venue.canonicalUrl ?: ""
        phoneStr = venue.contact?.phone ?: ""
        val statusStr = venue.hours?.status ?: ""
        val priceStr = venue.price?.message ?: ""


        title.set(venue.name)
        description.set((venue.description))

        if (urlStr.isNotEmpty())
            url.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_url_text
                    ), urlStr
                )
            )

        if (phoneStr.isNotEmpty())
            phone.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_phone_text
                    ),
                    phoneStr
                )
            )

        if (statusStr.isNotEmpty())
            hours.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_status_text
                    ),
                    statusStr
                )
            )


        if (priceStr.isNotEmpty())
            price.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_price_text
                    ),
                    venue?.price?.tier,
                    priceStr
                )
            )


        mapUrl.set(buildStaticMapUrl(venue))
    }

    private fun buildStaticMapUrl(venue: Venue?) =
        STATIC_MAP_ENDPOINT +
                "?center=$DEFAULT_MARKER_CENTER" +
                "&zoom=$DEFAULT_ZOOM" +
                "&scale=$DEFAULT_SCALE" +
                "&size=$DEFAULT_MAP_IMAGE_SIZE" +
                "&markers=$SEATTLE_LAT,$SEATTLE_LNG|${venue?.location?.lat},${venue?.location?.lng}" +
                "&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"

    fun onUrlClick(view: View) {
        val context = view.context
        if (urlStr.isEmpty()) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
        intent.putExtra(
            Browser.EXTRA_APPLICATION_ID,
            context.packageName
        )
        context.startActivity(intent)
    }

    fun onPhoneClick(view: View) {
        val context = view.context
        if (phoneStr.isEmpty()) return

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel: $phoneStr"))
        intent.putExtra(
            Browser.EXTRA_APPLICATION_ID,
            context.packageName
        )
        context.startActivity(intent)
    }

    fun onStarClick(view: View) {
        val imageButton = view as ImageButton
        val isSelected = imageButton.isSelected
        if (isSelected) {
            favoritesRepository.remove(id)
        } else {
            favoritesRepository.add(id)
        }
        imageButton.isSelected = !isSelected
    }

    override fun onCleared() {
        parentJob.cancel()
        super.onCleared()
    }
}