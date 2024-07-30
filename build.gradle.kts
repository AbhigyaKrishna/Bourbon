group = "me.abhigya.bourbon"
version = "1.0"

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}