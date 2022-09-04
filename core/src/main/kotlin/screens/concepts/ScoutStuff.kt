package screens.concepts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import data.selectedItemListOf
import ktx.collections.toGdxArray
import tru.AnimState
import tru.CardinalDirection
import tru.LpcCharacterAnim

object ScoutStuff {
    val scoutHeight = 150
    val scoutWidth = 150
    val scoutTextures = mapOf(
        CardinalDirection.East to Texture(Gdx.files.internal("sprites/scout/scout_e_small.png")),
        CardinalDirection.NorthEast to Texture(Gdx.files.internal("sprites/scout/scout_ne_small.png")),
        CardinalDirection.North to Texture(Gdx.files.internal("sprites/scout/scout_n_small.png")),
        CardinalDirection.SouthEast to Texture(Gdx.files.internal("sprites/scout/scout_se_small.png")),
        CardinalDirection.South to Texture(Gdx.files.internal("sprites/scout/scout_s_small.png")),
        CardinalDirection.SouthWest to Texture(Gdx.files.internal("sprites/scout/scout_sw_small.png")),
    )

    val animStates = mapOf(
        AnimState.Idle to Pair(0, 8),
        AnimState.Shoot to Pair(1, 3),
        AnimState.Walk to Pair(2, 8),
        AnimState.Death to Pair(4, 11)
    )

    val scoutAnims: Map<AnimState, LpcCharacterAnim<TextureRegion>> = animStates.map { state ->
        state.key to LpcCharacterAnim(
            state.key, CardinalDirection.scoutDirections.map { direction ->
                direction to Animation(1f / 8f, Array(state.value.second) { frame ->
                    TextureRegion(
                        scoutTextures[direction]!!,
                        frame * scoutWidth,
                        state.value.first * scoutHeight,
                        scoutWidth,
                        scoutHeight
                    )
                }.toGdxArray(), Animation.PlayMode.LOOP)
            }.toMap()
        )
    }.toMap()

    val anims = selectedItemListOf(AnimState.Idle, AnimState.Walk, AnimState.Shoot)
    val directions = selectedItemListOf(*CardinalDirection.scoutDirections.toTypedArray())
}