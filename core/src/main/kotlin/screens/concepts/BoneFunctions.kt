package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.AnimatedSprited3d
import screens.stuff.Bone3d
import screens.stuff.selectRecursive
import screens.stuff.toIso
import space.earlygrey.shapedrawer.ShapeDrawer

fun IThing.toMap(): Map<String, IThing> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    ).associateBy { it.name }
}

fun gimmeSomeBones(): Bone {
    return Bone("body", vec3(), 30f).apply {
        rotate(0f, -90f, 0f)
        rotateAroundJointEnabled = false
        rotateAroundUpEnabled = false
        addChild(Bone("head", vec3(0f, 5f, 0f), 3f).apply {
            rotate(0f, 90f, 0f)
            rotateAroundUpEnabled = false
            attachments.add(AnimatedSprited3d(CharacterSprites.head, vec3(0f, 5f, -1f), this))
        })
        addChild(Bone("left-arm-upper", vec3(10f, 0f, 0f), 10f).apply {
            rotateAroundForwardEnabled = false
            uRange = 15f..225f
            jRange = -90f..225f
            addChild(Bone("left-arm-lower", vec3(0f, 0f, -10f), 16f).apply {
                rotateAroundUpEnabled = false
                rotateAroundForwardEnabled = false
                jRange = 0f..180f
                addChild(Thing("left-hand", this.localPosition.cpy() + vec3(0f, 0f, -10f)).apply {
                    attachments.add(AnimatedSprited3d(CharacterSprites.hand, vec3(), this))
                })
            })
        })
        addChild(Bone("right-arm-upper", vec3(-10f, 0f, 0f), 10f).apply {
            rotateAroundForwardEnabled = false
            addChild(Bone("right-arm-lower", vec3(0f, 0f, -10f), 16f).apply {})
            rotateAroundUpEnabled = false
            rotateAroundForwardEnabled = false
        })
    }
}

fun drawBone(bone: Bone, batch: Batch, shapeDrawer: ShapeDrawer) {
    for (attachment in bone.attachments) {
        val p = attachment.position3d.toIso() - attachment.offset
        batch.draw(attachment, p.x, p.y)
        shapeDrawer.filledCircle(p + attachment.offset, 1f, Color.ORANGE)
    }
    drawOrientation(bone.position, bone.orientation, shapeDrawer)
    shapeDrawer.setColor(Color.GRAY)
    shapeDrawer.line(bone.position.toIso(), bone.boneEnd.toIso())
    shapeDrawer.filledCircle(bone.position.toIso(), 1f, Color.GREEN)
    shapeDrawer.filledCircle(bone.boneEnd.toIso(), 1f, Color.BLUE)
}

fun drawThingRecursive(thing: IThing, batch: Batch, shapeDrawer: ShapeDrawer) {
    for (child in thing.children)
        drawThingRecursive(child, batch, shapeDrawer)

    if (thing is Bone)
        drawBone(thing, batch, shapeDrawer)
    else {
        for (attachment in thing.attachments) {
            val p = attachment.position3d.toIso() - attachment.offset
            batch.draw(attachment, p.x, p.y)
            shapeDrawer.filledCircle(p + attachment.offset, 1f, Color.ORANGE)
        }
    }
}

fun drawOrientation(position: Vector3, orientation: Orientation, shapeDrawer: ShapeDrawer) {
    val up = orientation.up.cpy().scl(10f).toIso()
    val forward = orientation.forward.cpy().scl(10f).toIso()
    val left = orientation.leftOrRight.cpy().scl(10f).toIso()
    val isoPos = position.toIso()
    shapeDrawer.setColor(Color.BLUE)
    shapeDrawer.line(isoPos, isoPos + forward, .5f)
    shapeDrawer.setColor(Color.RED)
    shapeDrawer.line(isoPos, isoPos + up, .5f)
    shapeDrawer.setColor(Color.GREEN)
    shapeDrawer.line(isoPos, isoPos + left, .5f)
    shapeDrawer.setColor(Color.WHITE)
    shapeDrawer.filledCircle(isoPos, 1f, Color.WHITE)
    shapeDrawer.filledCircle(isoPos + forward, 1f, Color.BLUE)
    shapeDrawer.filledCircle(isoPos + up, 1f, Color.RED)
    shapeDrawer.filledCircle(isoPos + left, 1f, Color.GREEN)
}

fun drawSkeletonRecursive(bone3d: Bone3d, shapeDrawer: ShapeDrawer) {
    val offset = vec2(-100f)
    for (child in bone3d.children)
        drawSkeletonRecursive(child, shapeDrawer)
    shapeDrawer.filledCircle(bone3d.globalStart.toIso() + offset, 1f, Color.GREEN)
    shapeDrawer.line(bone3d.globalStart.toIso() + offset, bone3d.globalEnd.toIso() + offset, .5f)
    shapeDrawer.filledCircle(bone3d.globalEnd.toIso() + offset, .5f, Color.RED)

    offset.x += 50f
    val start2d = vec2(bone3d.globalStart.x, bone3d.globalStart.y)
    val end2d = vec2(bone3d.globalEnd.x, bone3d.globalEnd.y)
    shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
    shapeDrawer.line(start2d + offset, end2d + offset, 0.5f)
    shapeDrawer.filledCircle(end2d + offset, .5f, Color.RED)
    offset.x += 50f
    start2d.set(bone3d.globalStart.y, bone3d.globalStart.z)
    end2d.set(bone3d.globalEnd.y, bone3d.globalEnd.z)
    shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
    shapeDrawer.line(start2d + offset, end2d + offset, 1f)
    shapeDrawer.filledCircle(end2d + offset, 1f, Color.RED)
    offset.x += 50f
    start2d.set(bone3d.globalStart.z, bone3d.globalStart.x)
    end2d.set(bone3d.globalEnd.z, bone3d.globalEnd.x)
    shapeDrawer.filledCircle(start2d + offset, 1f, Color.GREEN)
    shapeDrawer.line(start2d + offset, end2d + offset, 1f)
    shapeDrawer.filledCircle(end2d + offset, 1f, Color.RED)
}