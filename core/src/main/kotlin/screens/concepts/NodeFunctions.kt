package screens.concepts

import com.badlogic.gdx.graphics.Color
import ktx.math.vec3

fun getArm(): Node {
    val armSegmentLength = 50f / 8f
    val upperArmLength = armSegmentLength * 5f
    val lowerArmLength = armSegmentLength * 3f
    val shoulderLength = armSegmentLength / 2f
    return Node("base").apply {
        addChild(
            Segment(
                "shoulder",
                vec3(),
                shoulderLength,
                Direction3d(90f, 0f, 0f),
                rotationDirections = mapOf()
            ).apply {
                addChild(Segment(
                    "arm-upper", vec3(), upperArmLength, Direction3d(0f, 90f, 0f), rotationDirections = mapOf(
                        RotationDirection.AroundRight to 0f..360f,
                        RotationDirection.AroundParentRight to 0f..90f
                    )
                ).apply {
                    addChild(
                        Segment(
                            "arm-lower",
                            vec3(),
                            lowerArmLength,
                            Direction3d(0f, 90f, 0f),
                            rotationDirections = mapOf(RotationDirection.AroundParentRight to 0f..180f)
                        )
                    )
                })
            })
    }
}

fun getCharacter(): Node {
    val totalLength = 90f
    val head = totalLength / 8f
    val torsoLength = head * 3f
    val totalArmLength = head * 4f
    val upperArmL = totalArmLength / 8f * 5f
    val lowerArmL = totalArmLength / 8f * 3f
    val upperLegL = head * 3f
    val lowerLegL = head * 2f
    return Node("base", vec3(0f, 0f, 0f), 0f, 0f, 0f).apply {
        addChild(Segment("torso", vec3(0f, 0f, 0f), torsoLength, Direction3d(0f, 90f, 0f), Color.GREEN).apply {
            addChild(Segment("neck", vec3(0f, 0f, 0f), 5f, Direction3d(0f, 90f, 0f)).apply {
                addChild(Node("face", vec3(0f, 5f, -5f)))
                addChild(Node("right-ear", vec3(-5f, 5f, 0f)))
                addChild(Node("left-ear", vec3(5f, 5f, 0f)))
                addChild(Node("back-of-my-head", vec3(0f, 5f, 5f)))
            })
            addChild(Segment("right-shoulder", vec3(0f, 0f, 0f), 15f, Direction3d(90f, 0f, 0f)).apply {
                addChild(
                    Segment(
                        "right-arm-upper",
                        vec3(0f, 0f, 0f),
                        upperArmL,
                        Direction3d(0f, -90f, 0f),
                        Color.WHITE
                    ).apply {
                        addChild(Segment("right-arm-lower", vec3(0f, 0f, 0f), lowerArmL, Direction3d(0f, -90f, 0f)))
                    })
            })
            addChild(Segment("left-shoulder", vec3(0f, 0f, 0f), 15f, Direction3d(-90f, 0f, 0f)).apply {
                addChild(
                    Segment(
                        "left-arm-upper",
                        vec3(0f, 0f, 0f),
                        upperArmL,
                        Direction3d(0f, -90f, 0f),
                        Color.WHITE
                    ).apply {
                        addChild(Segment("left-arm-lower", vec3(0f, 0f, 0f), lowerArmL, Direction3d(0f, -90f, 0f)))
                    })
            })
            addChild(Segment("right-hip", vec3(0f, -torsoLength, 0f), 10f, Direction3d(90f, 0f, 0f)).apply {
                addChild(
                    Segment(
                        "right-leg-upper",
                        vec3(0f, 0f, 0f),
                        upperLegL,
                        Direction3d(0f, -90f, 0f),
                        Color.WHITE
                    ).apply {
                        addChild(Segment("right-leg-lower", vec3(0f, 0f, 0f), lowerLegL, Direction3d(0f, -90f, 0f)))
                    })
            })
            addChild(Segment("left-hip", vec3(0f, -torsoLength, 0f), 10f, Direction3d(-90f, 0f, 0f)).apply {
                addChild(
                    Segment(
                        "left-leg-upper",
                        vec3(0f, 0f, 0f),
                        upperLegL,
                        Direction3d(0f, -90f, 0f),
                        Color.WHITE
                    ).apply {
                        addChild(Segment("left-leg-lower", vec3(0f, 0f, 0f), lowerLegL, Direction3d(0f, -90f, 0f)))
                    })
            })
        })
    }
}