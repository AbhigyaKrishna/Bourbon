package me.abhigya.bourbon.core.ui.router

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.RoutingTable
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.fromEnum
import com.copperleaf.ballast.navigation.vm.BasicRouter
import com.copperleaf.ballast.navigation.vm.withRouter
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

enum class RouteScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet()
) : Route {

    HOME("/home"),
    AUTH("/auth"),
    ;

    override val matcher: RouteMatcher by lazy { RouteMatcher.create(routeFormat) }
}

val RouterContract.module get() = module {
    factory { (coroutineScope: CoroutineScope, initialRoute: RouteScreen?) ->
        RouterViewModel(
            coroutineScope,
            get<BallastViewModelConfiguration.Builder>()
                .withRouter(
                    routingTable = RoutingTable.fromEnum(RouteScreen.entries),
                    initialRoute = initialRoute
                )
                .build()
        )
    }
}

class RouterViewModel(
    scope: CoroutineScope,
    config: BallastViewModelConfiguration<RouterContract.Inputs<RouteScreen>, RouterContract.Events<RouteScreen>, RouterContract.State<RouteScreen>>
) : BasicRouter<RouteScreen>(
    config = config,
    eventHandler = RouteEventHandler,
    coroutineScope = scope
)

private object RouteEventHandler : EventHandler<RouterContract.Inputs<RouteScreen>, RouterContract.Events<RouteScreen>, RouterContract.State<RouteScreen>> {
    override suspend fun EventHandlerScope<RouterContract.Inputs<RouteScreen>, RouterContract.Events<RouteScreen>, RouterContract.State<RouteScreen>>.handleEvent(
        event: RouterContract.Events<RouteScreen>
    ) {
        when (event) {
            is RouterContract.Events.BackstackEmptied -> postInput(RouterContract.Inputs.GoToDestination(RouteScreen.HOME.directions().build()))
            else -> Unit
        }
    }
}