package me.abhigya.bourbon.core.ui.recipe

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.copperleaf.ballast.navigation.routing.RouterContract
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.components.AppBar
import me.abhigya.bourbon.core.ui.components.BackButton
import me.abhigya.bourbon.core.ui.components.GeminiOutput
import me.abhigya.bourbon.core.ui.components.UiButton
import me.abhigya.bourbon.core.ui.router.LocalRouter
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import java.io.File

object MakeSomethingOutOfScreen : AppScreen {

    @Composable
    override fun invoke() {
        val ctx = LocalContext.current
        val coroutine = rememberCoroutineScope()
        val viewModel: MakeSomethingOutOfViewModel = remember(coroutine) { get { parametersOf(coroutine) } }
        val uiState by viewModel.observeStates().collectAsState()
        val router = LocalRouter.current

        Scaffold(
            topBar = {
                AppBar {
                    BackButton {
                        router.trySend(RouterContract.Inputs.GoBack())
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                ImageArea(
                    directory = File(ctx.cacheDir, "images"),
                    bitmap = uiState.image,
                    onSetUri = {
                        val bitmap = MediaStore.Images.Media.getBitmap(ctx.contentResolver, it)
                        viewModel.trySend(MakeSomethingOutOfContract.Inputs.ImageChanged(bitmap))
                    }
                )

                UiButton(
                    modifier = Modifier
                        .padding(8.dp),
                    text = "Find",
                    clickable = uiState.image != null
                ) {
                    viewModel.trySend(MakeSomethingOutOfContract.Inputs.Find)
                }

                GeminiOutput(
                    state = uiState.outputState,
                    height = 500.dp
                )
            }
        }
    }

    @Composable
    fun ImageArea(
        bitmap: Bitmap? = null,
        directory: File? = null,
        onSetUri : (Uri) -> Unit = {},
    ) {
        val context = LocalContext.current
        val tempUri = remember { mutableStateOf<Uri?>(null) }
        val authority = "me.abhigya.bourbon.fileprovider"

        fun getTempUri(): Uri? {
            directory?.let {
                it.mkdirs()
                val file = File.createTempFile(
                    "image_" + System.currentTimeMillis().toString(),
                    ".jpg",
                    it
                )

                return FileProvider.getUriForFile(
                    context,
                    authority,
                    file
                )
            }
            return null
        }

        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                it?.let {
                    onSetUri.invoke(it)
                }
            }
        )

        val takePhotoLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { _ ->
                tempUri.value?.let {
                    onSetUri.invoke(it)
                }
            }
        )

        var showBottomSheet by remember { mutableStateOf(false) }
        if (showBottomSheet){
            ModalBottomSheet(
                onDismiss = {
                    showBottomSheet = false
                },
                onTakePhotoClick = {
                    showBottomSheet = false

                    val tmpUri = getTempUri()
                    tempUri.value = tmpUri
                    tempUri.value?.let {
                        takePhotoLauncher.launch(it)
                    }
                },
                onPhotoGalleryClick = {
                    showBottomSheet = false
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(28.dp)
                .clickable {
                    showBottomSheet = true
                },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap == null) {
                val strokeColor = MaterialTheme.colorScheme.tertiary
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val stroke = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    drawRoundRect(
                        color = strokeColor,
                        style = stroke
                    )
                }

                Text(
                    text = "Click to take picture",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                AsyncImage(
                    model = bitmap,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = null,
                )
            }
        }
    }

    @Composable
    fun ModalBottomSheet(
        onDismiss: () -> Unit,
        onTakePhotoClick: () -> Unit,
        onPhotoGalleryClick: () -> Unit
    ) {
        ModalBottomSheetContent(
            header = "Choose Option",
            onDismiss = {
                onDismiss.invoke()
            },
            items = listOf(
                BottomSheetItem(
                    title = "Take Photo",
                    icon = Icons.Default.AccountBox,
                    onClick = {
                        onTakePhotoClick.invoke()
                    }
                ),
                BottomSheetItem(
                    title = "Select image",
                    icon = Icons.Default.Place,
                    onClick = {
                        onPhotoGalleryClick.invoke()
                    }
                ),
            )
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ModalBottomSheetContent(
        onDismiss: () -> Unit,
        header: String = "Choose Option",
        items: List<BottomSheetItem> = listOf(),
    ) {
        val skipPartiallyExpanded by remember { mutableStateOf(false) }
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

        ModalBottomSheet(
            shape = MaterialTheme.shapes.medium.copy(
                bottomStart = CornerSize(0),
                bottomEnd = CornerSize(0)
            ),
            onDismissRequest = { onDismiss.invoke() },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp),
                    text = header,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                items.forEach { item ->
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                item.onClick.invoke()
                            },
                        headlineContent = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                    )
                }
            }
        }
    }

    data class BottomSheetItem(
        val title: String = "",
        val icon: ImageVector,
        val onClick: () -> Unit
    )
}