package me.abhigya.bourbon.core.utils.features.router

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.RoutingTable
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.fromEnum
import com.copperleaf.ballast.navigation.vm.BasicRouter
import com.copperleaf.ballast.navigation.vm.RouterBuilder
import com.copperleaf.ballast.navigation.vm.withRouter
import com.copperleaf.ballast.plusAssign
import kotlinx.coroutines.CoroutineScope

enum class RouteScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet()
) : Route {

    HOME("/home"),
    LOGIN("/login"),
    SIGNUP("/signup")
    ;

    override val matcher: RouteMatcher by lazy { RouteMatcher.create(routeFormat) }
}

class Router(
    scope: CoroutineScope,
    initialRoute: RouteScreen? = null,
    config: RouterBuilder<RouteScreen>.() -> Unit = {}
) : BasicRouter<RouteScreen>(
    config = BallastViewModelConfiguration.Builder()
        .apply {
            this += LoggingInterceptor()
            logger = ::PrintlnLogger
        }
        .withRouter(
            routingTable = RoutingTable.fromEnum(RouteScreen.entries),
            initialRoute = initialRoute
        )
        .apply(config)
        .build(),
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