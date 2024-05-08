package com.example.chillinapp.ui.home.map.utils

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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


@Composable
internal fun HeatMap(
    cameraPositionState: CameraPositionState,
    points: List<WeightedLatLng>,
    setOnCameraMoveListener: (CameraPositionState) -> Unit,
    setOnMapLoadedCallback: (LatLng) -> Unit,
    gradient: Gradient
) {

    val tileOverlay: MutableState<TileOverlay?> =
        remember {
            mutableStateOf(null)
        }
    val chargedPoints: MutableState<List<WeightedLatLng>> =
        remember {
            mutableStateOf(points)
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                        }
                        googleMap.setOnMapLoadedCallback {
                            setOnMapLoadedCallback(googleMap.cameraPosition.target)
                        }
                    }
                }
            },
            update = { mapView ->

                // Update the heat map layer only if the points have changed
                if(points.isEmpty() || points == chargedPoints.value)
                    return@AndroidView

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