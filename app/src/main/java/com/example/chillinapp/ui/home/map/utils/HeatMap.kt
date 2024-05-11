package com.example.chillinapp.ui.home.map.utils

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng

/**
 * A Composable function that displays a heat map on a Google Map.
 *
 * @param cameraPositionState The current camera position state.
 * @param points The list of weighted points to be displayed on the heat map.
 * @param setOnCameraMoveListener A function to be called when the camera moves.
 * @param setOnMapLoadedCallback A function to be called when the map is loaded.
 * @param updateSearchRadius A function to update the search radius based on the zoom level.
 * @param gradient The gradient to be used for the heat map.
 */
@Composable
internal fun HeatMap(
    cameraPositionState: CameraPositionState,
    points: List<WeightedLatLng>,
    setOnCameraMoveListener: (CameraPositionState) -> Unit,
    setOnMapLoadedCallback: (LatLng) -> Unit,
    updateSearchRadius: (Float) -> Unit,
    gradient: Gradient
) {

    // A mutable state holding the current tile overlay for the heat map.
    val tileOverlay: MutableState<TileOverlay?> =
        rememberSaveable {
            mutableStateOf(null)
        }
    // A mutable state holding the current list of weighted points for the heat map.
    val chargedPoints: MutableState<List<WeightedLatLng>> =
        rememberSaveable {
            mutableStateOf(points)
        }

    // A box layout that fills the maximum size.
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // An Android view that displays the Google Map.
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { googleMap ->

                        // Set the map type
                        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

                        // Set the initial camera position
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                cameraPositionState.position.target,
                                cameraPositionState.position.zoom
                            )
                        )

                        updateSearchRadius(cameraPositionState.position.zoom)

                        // Add the heat map layer
                        if(points.isEmpty()) {
                            Log.d("HeatMap Factory", "No points to display")
                        } else {
                            Log.d("HeatMap Factory", "Adding heat map layer")
                            val heatMap = HeatmapTileProvider.Builder()
                                .weightedData(points)
                                .gradient(gradient)
                                .build()
                            tileOverlay.value = googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMap))
                            chargedPoints.value = points
                        }


                        // Set the listeners
                        googleMap.setOnCameraMoveListener {
                            setOnCameraMoveListener(CameraPositionState(googleMap.cameraPosition))

                            // Set the radius of the search
                            updateSearchRadius(CameraPositionState(googleMap.cameraPosition).position.zoom)

                        }
                        googleMap.setOnMapLoadedCallback {
                            setOnMapLoadedCallback(googleMap.cameraPosition.target)
                        }
                    }
                }
            },
            update = { mapView ->

                // Remove the heat map layer if there are no points
                if(points.isEmpty()) {
                    tileOverlay.value?.remove()
                    tileOverlay.value?.clearTileCache()
                    return@AndroidView
                }

                // Check if the points have changed
                if(points == chargedPoints.value){
                    return@AndroidView
                }

                // Change the heat map layer
                mapView.getMapAsync { googleMap ->
                    Log.d("HeatMap Update", "Adding heat map layer")
                    val heatMap = HeatmapTileProvider.Builder()
                        .weightedData(points)
                        .gradient(gradient)
                        .build()
                    tileOverlay.value?.remove()
                    tileOverlay.value?.clearTileCache()
                    tileOverlay.value = googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMap))
                    chargedPoints.value = points
                }

            }
        )
    }
}