package me.abhigya.bourbon.domain

import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.Model

interface ModelResource {

    val path: String

    val dimensionScale: Float

    companion object {
        fun create(path: String, dimensionScale: Float): ModelResource {
            return object : ModelResource {
                override val path: String = path
                override val dimensionScale: Float = dimensionScale
            }
        }
    }

}

object Models;

val Models.Burpee: ModelResource get() = ModelResource.create("Burpee.glb", 0.8f)

fun ModelResource.create(modelLoader: ModelLoader): Model {
    return modelLoader.createModel(path)
}