package org.my.drivexcel.ui.screens.home.details.subcontainers

import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MergeSubContainer() {

    var showTargetBorder by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .then(
                        if (showTargetBorder)
                            Modifier.border(2.dp, MaterialTheme.colorScheme.primary)
                        else
                            Modifier.border(2.dp, Color.Companion.Gray)
                    )
                    .dragAndDropTarget(
                        shouldStartDragAndDrop = { true },
                        target = remember {
                            object : DragAndDropTarget {
                                override fun onStarted(event: DragAndDropEvent) {
                                    showTargetBorder = true
                                }

                                override fun onEnded(event: DragAndDropEvent) {
                                    showTargetBorder = false
                                }

                                override fun onDrop(event: DragAndDropEvent): Boolean {
                               /*     val transferable = event.awtTransferable
                                    val files =
                                        transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<File>
                                            ?: emptyList()
                                    val newFiles =
                                        files.filter { it.name.endsWith(".xlsx") || it.name.endsWith(".xls") }
                                            .toSet()*/
                                    showTargetBorder = false
                                    return true
                                }
                            }
                        }
                    )
            )

            Button(
                onClick = {}
            ) {
                Text(
                    text = "Добавить файлы"
                )
            }
        }

        LazyColumn {
            items(30) {
                MergeSubContainerItem()
            }
        }
    }
}