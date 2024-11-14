package me.abhigya.bourbon.core.ui.ar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.domain.entities.ModelResource

class ArScreen(
    private val model: ModelResource
) : AppScreen {

    @Composable
    override operator fun invoke() {
        Box(modifier = Modifier.fillMaxSize()) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val materialLoader = rememberMaterialLoader(engine)
            val cameraNode = rememberARCameraNode(engine)
            val view = rememberView(engine)
            val collisionSystem = rememberCollisionSystem(view)
            val childNodes = rememberNodes()

            var planeRenderer by remember { mutableStateOf(true) }

            var trackingFailureReason by remember {
                mutableStateOf<TrackingFailureReason?>(null)
            }

            var frame by remember { mutableStateOf<Frame?>(null) }
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode =
                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            false -> Config.DepthMode.DISABLED
                        }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode =
                        Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer,
                onTrackingFailureChanged = {
                    trackingFailureReason = it
                },
                onSessionUpdated = { _, updatedFrame ->
                    frame = updatedFrame
                },
                onGestureListener = rememberOnGestureListener(
                    onSingleTapConfirmed = { motionEvent, node ->
                        if (node == null) {
                            val hitResults = frame?.hitTest(motionEvent)
                                ?: return@rememberOnGestureListener
                            hitResults.firstOrNull {
                                it.isValid(
                                    depthPoint = false,
                                    point = false
                                )
                            }?.createAnchorOrNull()
                                ?.let {
                                    planeRenderer = false
                                    childNodes += it.createAnchorNode(
                                        engine,
                                        modelLoader
                                    )
                                }
                        }
                    }
                )
            )

            Text(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                color = Color.White,
                text = trackingFailureReason?.getDescription(LocalContext.current)
                    ?: if (childNodes.isEmpty()) {
                        stringResource(R.string.ar_empty_child_node)
                    } else {
                        stringResource(R.string.ar_error)
                    }
            )
        }
    }

    private fun Anchor.createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
    ): AnchorNode {
        val anchorNode = AnchorNode(engine, this)
        val modelNode = ModelNode(
            modelInstance = modelLoader.createModelInstance(model.path),
            scaleToUnits = model.dimensionScale
        ).apply {
            isEditable = true
            isPositionEditable = true
            editableScaleRange = 0.01f..0.05f
        }

        anchorNode.addChildNode(modelNode)

        return anchorNode
    }

}