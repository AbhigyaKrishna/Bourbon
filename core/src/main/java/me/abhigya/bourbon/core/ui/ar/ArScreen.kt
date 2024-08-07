package me.abhigya.bourbon.core.ui.ar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import me.abhigya.bourbon.core.ui.AppScreen

class ArScreen : AppScreen {

    @Composable
    override fun invoke() {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val engine = rememberEngine()
                val modelLoader = rememberModelLoader(engine = engine)
                val materialLoader = rememberMaterialLoader(engine = engine)
                val cameraNode = rememberARCameraNode(engine = engine)
                val view = rememberView(engine = engine)
                val collisionSystem = rememberCollisionSystem(view = view)
                val childNodes = rememberNodes()

                var planeRenderer by remember { mutableStateOf(true) }
                var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }

                var frame by remember { mutableStateOf<Frame?>(null) }
                ARScene(
                    modifier = Modifier
                        .fillMaxSize(),
                    childNodes = childNodes,
                    engine = engine,
                    view = view,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    collisionSystem = collisionSystem,
                    sessionConfiguration = { session, config ->
                        config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            false -> Config.DepthMode.DISABLED
                        }
                        config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                        config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
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
                            if (node == null) return@rememberOnGestureListener
                            val hitResult = frame?.hitTest(motionEvent) ?: return@rememberOnGestureListener
                            hitResult.firstOrNull {
                                it.isValid(depthPoint = false, point = false)
                            }?.createAnchorOrNull()
                                ?.let {
                                    planeRenderer = false
                                    childNodes += it.createAnchorNode(
                                        engine,
                                        modelLoader,
                                        materialLoader
                                    )
                                }
                        }
                    )
                )
            }
        }
    }

    private fun Anchor.createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
    ): AnchorNode {
        val anchorNode = AnchorNode(engine, this)
        val modelNode = ModelNode(
            modelInstance = modelLoader.createModelInstance("Burpee.fbx"),
            scaleToUnits = 0.02f
        ).apply {
            isEditable = true
            isPositionEditable = true
            editableScaleRange = 0.01f..0.05f
            rotation = Rotation(0.0f, 90f, 0.0f)
        }
        val boundingBoxNode = CubeNode(
            engine,
            size = modelNode.extents,
            center = modelNode.center,
            materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
        )

        modelNode.addChildNode(boundingBoxNode)
        anchorNode.addChildNode(modelNode)

        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { transforms ->
                boundingBoxNode.isVisible = transforms.isNotEmpty()
            }
        }

        return anchorNode
    }

}