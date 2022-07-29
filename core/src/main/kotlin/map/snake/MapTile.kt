package map.snake

import ecs.components.graphics.renderables.RenderableTextureRegions

class MapTile(val renderables: RenderableTextureRegions, val passable: Boolean, val tileScale: Float = 1f/4f)