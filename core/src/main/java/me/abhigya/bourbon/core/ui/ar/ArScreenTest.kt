package me.abhigya.bourbon.core.ui.ar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Pose
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.Model
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.utils.bouncyClick
import me.abhigya.bourbon.domain.entities.ModelResource
import me.abhigya.bourbon.domain.entities.create

class ArScreenTest(
    private val resource: ModelResource
) : AppScreen {

    @OptIn(DelicateCoroutinesApi::class)
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
                var model by remember { mutableStateOf<Model?>(null) }

                DisposableEffect(engine) {
                    val job = GlobalScope.launch {
                        model = resource.create(modelLoader)
                    }

                    onDispose {
                        job.cancel()
                    }
                }

                var trackingFailureReason by remember { mutableStateOf<TrackingFailureReason?>(null) }
                var placed by remember { mutableStateOf(false) }
                var lastAnchor by remember { mutableStateOf<Anchor?>(null) }

                ARScene(
                    modifier = Modifier
                        .fillMaxSize(),
                    childNodes = childNodes,
                    engine = engine,
                    view = view,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    collisionSystem = collisionSystem,
                    planeRenderer = true,
                    sessionConfiguration = { session, config ->
                        config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            false -> Config.DepthMode.DISABLED
                        }
                        config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                        config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    },
                    cameraNode = cameraNode,
                    onTrackingFailureChanged = {
                        trackingFailureReason = it
                    },
                    onSessionUpdated = { session, frame ->
                        if (!placed && frame.camera.trackingState == TrackingState.TRACKING && model != null) {
                            val pos = frame.camera.pose.compose(Pose.makeTranslation(0f, 0f, 0.3f))
                            if (lastAnchor == null) {
                                val anchor = session.createAnchor(pos)
                                lastAnchor = anchor
                                val anchorNode = AnchorNode(engine, anchor)
                                val modelNode = ModelNode(
                                    modelInstance = model!!.instance,
                                    scaleToUnits = resource.dimensionScale,
                                    autoAnimate = false
                                ).apply {
                                    isEditable = true
                                    isPositionEditable = true
                                }

                                anchorNode.addChildNode(modelNode)
                                childNodes.add(anchorNode)
                            } else {
//                                lastAnchor!!.pose.
                            }
                        }
                    },
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .bouncyClick()
                            .clickable {

                            }
                    )
                }
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