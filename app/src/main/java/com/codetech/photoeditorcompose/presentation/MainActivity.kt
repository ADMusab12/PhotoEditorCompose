package com.codetech.photoeditorcompose.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codetech.photoeditorcompose.abstraction.enums.EditMode
import com.codetech.photoeditorcompose.abstraction.enums.FilterType
import com.codetech.photoeditorcompose.abstraction.model.EditState
import com.codetech.photoeditorcompose.ui.theme.PhotoEditorComposeTheme
import com.codetech.photoeditorcompose.util.getColorFilterForType
import com.codetech.photoeditorcompose.util.getIconForFilter
import com.codetech.photoeditorcompose.util.saveImageToGallery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoEditorComposeTheme {
                PhotoEditorScreen()
            }
        }
    }
}

val primaryGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2)
    )
)

// Function to apply filter to bitmap
fun applyFilterToBitmap(bitmap: Bitmap, filter: FilterType): Bitmap {
    if (filter == FilterType.NONE) return bitmap.copy(bitmap.config!!, true)

    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint()

    val colorMatrix = when (filter) {
        FilterType.GRAYSCALE -> ColorMatrix().apply { setSaturation(0f) }
        FilterType.SEPIA -> ColorMatrix(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.INVERT -> ColorMatrix(floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.VINTAGE -> ColorMatrix(floatArrayOf(
            0.6f, 0.3f, 0.1f, 0f, 30f,
            0.2f, 0.6f, 0.2f, 0f, 20f,
            0.2f, 0.2f, 0.5f, 0f, 10f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.COOL -> ColorMatrix(floatArrayOf(
            0.8f, 0f, 0f, 0f, 0f,
            0f, 0.9f, 0f, 0f, 0f,
            0f, 0f, 1.2f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.WARM -> ColorMatrix(floatArrayOf(
            1.2f, 0f, 0f, 0f, 0f,
            0f, 1.05f, 0f, 0f, 0f,
            0f, 0f, 0.8f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.BRIGHT -> ColorMatrix().apply {
            setScale(1.3f, 1.3f, 1.3f, 1f)
        }
        FilterType.DARK -> ColorMatrix().apply {
            setScale(0.7f, 0.7f, 0.7f, 1f)
        }
        FilterType.CONTRAST -> ColorMatrix(floatArrayOf(
            1.5f, 0f, 0f, 0f, -64f,
            0f, 1.5f, 0f, 0f, -64f,
            0f, 0f, 1.5f, 0f, -64f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.SATURATE -> ColorMatrix().apply { setSaturation(1.8f) }
        FilterType.VIVID -> ColorMatrix().apply { setSaturation(2.0f) }
        FilterType.FADE -> ColorMatrix().apply {
            setScale(1f, 1f, 1f, 0.7f)
        }
        FilterType.BLUE_TINT -> ColorMatrix(floatArrayOf(
            0.9f, 0f, 0f, 0f, 0f,
            0f, 0.9f, 0f, 0f, 0f,
            0f, 0f, 1.3f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        FilterType.RED_TINT -> ColorMatrix(floatArrayOf(
            1.3f, 0f, 0f, 0f, 0f,
            0f, 0.9f, 0f, 0f, 0f,
            0f, 0f, 0.9f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        else -> ColorMatrix()
    }

    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return result
}

@Composable
fun PhotoEditorScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                selectedImageUri = uri
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentFilter by remember { mutableStateOf(FilterType.NONE) }
    var currentRotation by remember { mutableFloatStateOf(0f) }
    var editMode by remember { mutableStateOf(EditMode.FILTER) }
    var isSaving by remember { mutableStateOf(false) }

    // History management
    var historyStack by remember { mutableStateOf<List<EditState>>(emptyList()) }
    var historyIndex by remember { mutableIntStateOf(-1) }

    // Crop state
    var isCropping by remember { mutableStateOf(false) }
    var cropRect by remember { mutableStateOf(Rect.Zero) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var useContentScaleCrop by remember { mutableStateOf(true) }

    // Save state to history
    fun saveToHistory(bitmap: Bitmap) {
        val newState = EditState(
            bitmap = bitmap.copy(bitmap.config!!, true),
            filter = currentFilter,
            rotation = currentRotation
        )
        val newHistory = historyStack.take(historyIndex + 1) + newState
        historyStack = newHistory
        historyIndex = newHistory.size - 1
    }

    fun undo() {
        if (historyIndex > 0) {
            historyIndex--
            val state = historyStack[historyIndex]
            selectedBitmap?.recycle()
            selectedBitmap = state.bitmap.copy(state.bitmap.config!!, true)
            currentFilter = state.filter
            currentRotation = state.rotation
        }
    }

    fun redo() {
        if (historyIndex < historyStack.size - 1) {
            historyIndex++
            val state = historyStack[historyIndex]
            selectedBitmap?.recycle()
            selectedBitmap = state.bitmap.copy(state.bitmap.config!!, true)
            currentFilter = state.filter
            currentRotation = state.rotation
        }
    }

    fun rotateBitmap() {
        selectedBitmap?.let { bitmap ->
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            selectedBitmap = rotated
            currentRotation = (currentRotation + 90f) % 360f
            saveToHistory(rotated)
        }
    }

    // Crop functionality
    fun applyCrop() {
        selectedBitmap?.let { bitmap ->
            if (cropRect.width > 0 && cropRect.height > 0 && containerSize != Size.Zero) {
                val dstSize = containerSize
                val srcSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
                val scaleX = dstSize.width / srcSize.width
                val scaleY = dstSize.height / srcSize.height
                val scale = max(scaleX, scaleY)
                val srcCropWidth = dstSize.width / scale
                val srcCropHeight = dstSize.height / scale
                val srcOffsetX = (srcSize.width - srcCropWidth) / 2f
                val srcOffsetY = (srcSize.height - srcCropHeight) / 2f

                val cropLeftDst = cropRect.left
                val cropTopDst = cropRect.top
                val cropWidthDst = cropRect.width
                val cropHeightDst = cropRect.height

                val cropLeftSrc = srcOffsetX + cropLeftDst / scale
                val cropTopSrc = srcOffsetY + cropTopDst / scale
                val cropWidthSrc = cropWidthDst / scale
                val cropHeightSrc = cropHeightDst / scale

                val left = cropLeftSrc.coerceAtLeast(0f).roundToInt().coerceAtMost(bitmap.width - 1)
                val top = cropTopSrc.coerceAtLeast(0f).roundToInt().coerceAtMost(bitmap.height - 1)
                val width = cropWidthSrc.roundToInt().coerceAtMost(bitmap.width - left)
                val height = cropHeightSrc.roundToInt().coerceAtMost(bitmap.height - top)
                val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
                selectedBitmap = cropped
                saveToHistory(cropped)
                isCropping = false
                cropRect = Rect.Zero
                useContentScaleCrop = false
            }
        }
    }

    // Save with filter applied
    fun saveImageWithFilter() {
        selectedBitmap?.let { bitmap ->
            isSaving = true
            scope.launch(Dispatchers.IO) {
                try {
                    // Apply the current filter to the bitmap
                    val filteredBitmap = applyFilterToBitmap(bitmap, currentFilter)
                    val saved = saveImageToGallery(context, filteredBitmap)

                    // Clean up the temporary filtered bitmap if it's different
                    if (filteredBitmap != bitmap) {
                        filteredBitmap.recycle()
                    }

                    withContext(Dispatchers.Main) {
                        isSaving = false
                        if (saved) {
                            snackBarHostState.showSnackbar("Image saved to gallery")
                        } else {
                            snackBarHostState.showSnackbar("Failed to save image")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isSaving = false
                        snackBarHostState.showSnackbar("Error: ${e.message}")
                    }
                }
            }
        }
    }

    // Load bitmap from URI
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val bitmap = BitmapFactory.decodeStream(input)
                    withContext(Dispatchers.Main) {
                        selectedBitmap?.recycle()
                        selectedBitmap = bitmap
                        currentFilter = FilterType.NONE
                        currentRotation = 0f
                        historyStack = listOf(EditState(bitmap.copy(bitmap.config!!, true), FilterType.NONE, 0f))
                        historyIndex = 0
                        useContentScaleCrop = true
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize()
            .background(brush = primaryGradient),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (selectedBitmap == null) {
                // Welcome screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Photo Editor",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Transform your photos with advanced editing tools",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                    Button(
                        onClick = {
                            try {
                                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                                        type = "image/*"
                                    }
                                } else {
                                    Intent(Intent.ACTION_GET_CONTENT).apply {
                                        type = "image/*"
                                    }
                                }
                                val chooser = Intent.createChooser(intent, "Select an Image")
                                pickImageLauncher.launch(chooser)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "No app found to pick images", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Text(
                            text = "Select Image",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                // Editing screen
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { undo() },
                                enabled = historyIndex > 0
                            ) { Icon(Icons.Default.Undo, "Undo", tint = Color.White) }

                            IconButton(
                                onClick = { redo() },
                                enabled = historyIndex < historyStack.size - 1
                            ) { Icon(Icons.Default.Redo, "Redo", tint = Color.White) }

                            IconButton(
                                onClick = { saveImageWithFilter() },
                                enabled = !isSaving
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Save, "Save", tint = Color.White)
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                selectedBitmap?.recycle()
                                historyStack.forEach { it.bitmap.recycle() }
                                selectedBitmap = null
                                selectedImageUri = null
                                historyStack = emptyList()
                                historyIndex = -1
                            }
                        ) { Icon(Icons.Default.Close, "Close", tint = Color.White) }
                    }

                    // Image with crop overlay
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        selectedBitmap?.let { bitmap ->
                            val colorFilter = getColorFilterForType(currentFilter)

                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Edited Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onSizeChanged { containerSize = Size(it.width.toFloat(), it.height.toFloat()) }
                                    .clip(RoundedCornerShape(24.dp))
                                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                                contentScale = if (useContentScaleCrop) ContentScale.Crop else ContentScale.Fit,
                                colorFilter = colorFilter
                            )

                            if (isCropping) {
                                CropOverlay { cropRect = it }
                            }
                        }
                    }

                    // Bottom action toolbar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column {
                            // Mode buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ActionButton(
                                    icon = Icons.Default.Filter,
                                    label = "Filters",
                                    isSelected = editMode == EditMode.FILTER,
                                    onClick = { editMode = EditMode.FILTER; isCropping = false }
                                )
                                ActionButton(
                                    icon = Icons.Default.Crop,
                                    label = "Crop",
                                    isSelected = editMode == EditMode.CROP,
                                    onClick = {
                                        editMode = EditMode.CROP;
                                        isCropping = true;
                                        useContentScaleCrop = true
                                        cropRect = Rect.Zero
                                    }
                                )
                                ActionButton(
                                    icon = Icons.Default.RotateRight,
                                    label = "Rotate",
                                    isSelected = editMode == EditMode.ROTATE,
                                    onClick = { editMode = EditMode.ROTATE; isCropping = false }
                                )
                            }

                            // Tool-specific UI
                            when (editMode) {
                                EditMode.FILTER -> {
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(FilterType.entries.size) { index ->
                                            val filter = FilterType.entries[index]
                                            FilterChip(
                                                filter = filter,
                                                onFilterSelected = { newFilter ->
                                                    currentFilter = newFilter
                                                    selectedBitmap?.let { saveToHistory(it) }
                                                },
                                                isSelected = currentFilter == filter
                                            )
                                        }
                                    }
                                }

                                EditMode.ROTATE -> {
                                    Button(
                                        onClick = { rotateBitmap() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Icon(Icons.Default.RotateRight, "Rotate")
                                        Text("Rotate 90°", modifier = Modifier.padding(start = 8.dp))
                                    }
                                }

                                EditMode.CROP -> {
                                    Button(
                                        onClick = { applyCrop() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text("Apply Crop")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CropOverlay(onCropRectChange: (Rect) -> Unit) {
    var startOffset by remember { mutableStateOf<Offset?>(null) }
    var endOffset by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { start ->
                        startOffset = start
                        endOffset = start
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        endOffset = endOffset?.plus(dragAmount)
                        startOffset?.let { start ->
                            endOffset?.let { end ->
                                val rect = Rect(
                                    left = min(start.x, end.x),
                                    top = min(start.y, end.y),
                                    right = max(start.x, end.x),
                                    bottom = max(start.y, end.y)
                                )
                                onCropRectChange(rect)
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            startOffset?.let { start ->
                endOffset?.let { end ->
                    val rect = Rect(
                        left = min(start.x, end.x),
                        top = min(start.y, end.y),
                        right = max(start.x, end.x),
                        bottom = max(start.y, end.y)
                    )
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(rect.left, rect.top),
                        size = Size(rect.width, rect.height),
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    filter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
    isSelected: Boolean
) {
    val chipColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    Card(
        modifier = Modifier
            .size(60.dp)
            .clickable { onFilterSelected(filter) }
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = chipColor.copy(alpha = if (isSelected) 0.2f else 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getIconForFilter(filter),
                contentDescription = filter.name,
                tint = chipColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = chipColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoEditorPreview() {
    PhotoEditorComposeTheme {
        PhotoEditorScreen()
    }
}