package map.snake

import ecs.components.graphics.renderables.RenderableTextureRegions

class MapTile(val renderables: RenderableTextureRegions, val passable: Boolean, val drawLayer:Int, val tileScale: Float = 1f/4f)