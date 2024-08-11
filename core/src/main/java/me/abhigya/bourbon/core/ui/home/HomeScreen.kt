package me.abhigya.bourbon.core.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.components.StatCard
import me.abhigya.bourbon.core.utils.statusBarsPadding
import me.abhigya.bourbon.domain.entities.User
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import java.time.DayOfWeek

object HomeScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        val coroutine = rememberCoroutineScope()
        val viewModel: HomeViewModel = remember(coroutine) { get { parametersOf(coroutine) } }
        val uiState by viewModel.observeStates().collectAsState()
        val userDataState by viewModel.fetchUser().collectAsState(initial = null)

        val user = userDataState
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Header(user = user)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Calendar(uiState = uiState) {
                        viewModel.trySend(HomeContract.Inputs.SelectDate(it.date.toKotlinLocalDate()))
                    }


                }
            }
        }
    }

    @Composable
    internal fun Header(user: User) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(100.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hello,",
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Text(
                            text = user.name,
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    internal fun Calendar(uiState: HomeContract.State, onClick: (WeekDay) -> Unit) {
        val selectedDate = uiState.selectedDate.toJavaLocalDate()
        val currentDate = uiState.date.toJavaLocalDate()
        val startDate = remember { currentDate.yearMonth.withMonth(1).atStartOfMonth() }
        val endDate = remember { currentDate.yearMonth.plusMonths(1).atEndOfMonth() }
        val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

        val state = rememberWeekCalendarState(
            startDate = startDate,
            endDate = endDate,
            firstVisibleWeekDate = selectedDate,
            firstDayOfWeek = firstDayOfWeek
        )

        WeekCalendar(
            state = state,
            contentPadding = PaddingValues(8.dp),
            dayContent = {
                val selection = when {
                    it.date == selectedDate -> SelectionState.SELECTED
                    it.date == currentDate -> SelectionState.CURRENT
                    it.date < currentDate -> SelectionState.PREVIOUS
                    else -> SelectionState.NEXT
                }
                Day(day = it, selection = selection, onClick = onClick)
            },
        )
    }

    @Composable
    internal fun Day(day: WeekDay, selection: SelectionState, onClick: (WeekDay) -> Unit) {
        val color = when (selection) {
            SelectionState.CURRENT -> Color.White
            SelectionState.SELECTED -> MaterialTheme.colorScheme.primary
            SelectionState.NEXT, SelectionState.PREVIOUS -> MaterialTheme.colorScheme.tertiary
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(8.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    onClick(day)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val dayOfWeek = day.date.dayOfWeek
            Text(
                text = dayOfWeek.name.take(1) + if (dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.SATURDAY) {
                     dayOfWeek.name[1].lowercase()
                } else {
                    ""
                },
                fontSize = 16.sp,
                color = color
            )

            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 16.sp,
                color = color
            )
        }
    }

    enum class SelectionState {
        PREVIOUS,
        CURRENT,
        SELECTED,
        NEXT
    }

}