internal val Project.libs: VersionCatalog get() =
    project.extensions.getByType<VersionCatalogsExtension>().named("libs")