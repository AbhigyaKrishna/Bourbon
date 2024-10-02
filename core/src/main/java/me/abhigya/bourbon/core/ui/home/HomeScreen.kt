package me.abhigya.bourbon.core.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.components.StatCard
import me.abhigya.bourbon.core.ui.router.LocalRouter
import me.abhigya.bourbon.core.ui.router.RoutePath
import me.abhigya.bourbon.core.utils.bouncyClick
import me.abhigya.bourbon.core.utils.navigationBarsPadding
import me.abhigya.bourbon.core.utils.statusBarsPadding
import me.abhigya.bourbon.domain.entities.DietPreference
import me.abhigya.bourbon.domain.entities.Food
import me.abhigya.bourbon.domain.entities.User
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import java.time.DayOfWeek

object HomeScreen : AppScreen, SubScreen {

    @Composable
    override operator fun invoke() {
        val coroutine = rememberCoroutineScope()
        val viewModel: HomeViewModel = remember(coroutine) { get { parametersOf(coroutine) } }
        val uiState by viewModel.observeStates().collectAsState()
        val userState by viewModel.user.collectAsState()
        var selected by remember { mutableStateOf(NavItem.Home) }

        val user = userState
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Header(user = user)
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.background),
                    topBar = {
                        if (selected == NavItem.Profile) return@Scaffold

                        Calendar(uiState = uiState) {
                            viewModel.trySend(HomeContract.Inputs.SelectDate(it.date.toKotlinLocalDate()))
                        }
                    },
                    bottomBar = {
                        BottomBavBar(
                            allStates = NavItem.entries,
                            current = selected
                        ) {
                            selected = it
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        val screen: SubScreen = when (selected) {
                            NavItem.Home -> HomeScreen
                            NavItem.Exercise -> ExerciseViewScreen
                            NavItem.Diet -> DietViewScreen
                            NavItem.Profile -> ProfileScreen
                        }

                        screen(uiState = uiState, user = user)
                    }
                }
            }
        }
    }

    @Composable
    override fun invoke(uiState: HomeContract.State, user: User) {
        val router = LocalRouter.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.home_screen_quick_access),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize(),
                        elevation = CardDefaults.elevatedCardElevation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .bouncyClick()
                                    .clickable {
                                        router.trySend(
                                            RouterContract.Inputs.GoToDestination(
                                                RoutePath.CALORIE_VIEWER
                                                    .directions()
                                                    .build()
                                            )
                                        )
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.home_screen_calorie_viewer),
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.fire),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .bouncyClick()
                                    .clickable {
                                        router.trySend(
                                            RouterContract.Inputs.GoToDestination(
                                                RoutePath.MAKE_SOMETHING_OUT_OF
                                                    .directions()
                                                    .build()
                                            )
                                        )
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.home_screen_make_something_out_of),
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.book),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.home_screen_upcoming_meal),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            HorizontalDivider(thickness = 4.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                    elevation = CardDefaults.elevatedCardElevation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val food = user.diet[uiState.selectedDate.dayOfWeek]?.food?.first() ?: Food("dummy", "Dummy", "Dummy", 0, DietPreference.Vegetarian)
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.calories, food.calories),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 12.dp)
            Text(
                text = stringResource(R.string.home_screen_stats),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            HorizontalDivider(thickness = 4.dp)
            StatCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                calorieEaten = 1291,
                calorieRemaining = 826,
                calorieBurned = 244,
                totalCalorie = 1291 + 826
            )
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
                            text = stringResource(R.string.hello),
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
            contentPadding = PaddingValues(horizontal = 8.dp),
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

    @Composable
    internal fun BottomBavBar(allStates: List<NavItem>, current: NavItem, onClick: (NavItem) -> Unit) {
        AnimatedNavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp),
            selectedIndex = current.ordinal,
            barColor = MaterialTheme.colorScheme.secondary,
            ballColor = MaterialTheme.colorScheme.primary,
            cornerRadius = shapeCornerRadius(8.dp),
        ) {
            allStates.forEachIndexed { index, navItem ->
                val isSelected = index == current.ordinal
                IconButton(
                    modifier = Modifier
                        .padding(4.dp),
                    onClick = {
                        onClick(navItem)
                    }
                ) {
                    Icon(
                        painter = navItem.icon(),
                        contentDescription = navItem.name.lowercase(),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }
    }

    enum class SelectionState {
        PREVIOUS,
        CURRENT,
        SELECTED,
        NEXT
    }

    enum class NavItem {
        Home,
        Exercise,
        Diet,
        Profile
    }

    @Composable
    private fun NavItem.icon(): Painter {
        return when (this) {
            NavItem.Home -> painterResource(id = R.drawable.home)
            NavItem.Exercise -> painterResource(id = R.drawable.dumbbell)
            NavItem.Diet -> painterResource(id = R.drawable.diet)
            NavItem.Profile -> painterResource(id = R.drawable.profile)
        }
    }

}