package com.example.broadcastjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.broadcastjournal.ui.theme.BroadcastJournalTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BroadcastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BroadcastJournalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EventJournalScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EventJournalScreen(viewModel: BroadcastViewModel, modifier: Modifier = Modifier) {
    val events by viewModel.events.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Журнал подій (${events.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { viewModel.clearAll() }, enabled = events.isNotEmpty()) {
                Text("Очистити")
            }
        }

        if (events.isEmpty()) {
            Text(
                text = "Подій ще немає. Увімкни режим польоту або підключи зарядку.",
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = events, key = { event -> event.id }) { event ->
                ExpandableEventCard(
                    event = event,
                    onDelete = { viewModel.deleteEvent(event) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun ExpandableEventCard(
    event: BroadcastEvent,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.type, style = MaterialTheme.typography.titleMedium)
                Text(text = event.timestamp, style = MaterialTheme.typography.bodySmall)

                if (isExpanded) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = event.details, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити")
            }
        }
    }
}