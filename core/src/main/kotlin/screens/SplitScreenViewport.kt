package screens
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport


/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


/** This viewport can be used to split up the screen into different regions which can be rendered each on their own. It actually
 * consists of several other viewports. It has one "root" viewport which is used to define the area that can be used by the "sub"
 * viewports. The "sub" viewports will split this area into several areas. <br></br>
 * To render in a certain "sub" viewport, this viewport needs to be activated first. This will result in a layouting of this
 * viewport and its [Viewport.update] method being called to setup the camera and the OpenGL viewport
 * (glViewport) correctly.
 * @author Daniel Holderbaum
 */
class SplitViewport(rootViewport: Viewport) : Viewport() {
    /** @author Daniel Holderbaum
     */
    class SizeInformation(
        /** Determines, how the size should be interpreted.  */
        var sizeType: SizeType,
        /** The size to be used. Is ignored in case [SizeType] REST is used.  */
        var size: Float
    ) {
        init {
            size = size
        }
    }

    /** An enum which determines how a size should be interpreted.
     * @author Daniel Holderbaum
     */
    enum class SizeType {
        /** The size will be fixed and will have exactly the given size all the time.  */
        ABSOLUTE,

        /** The given size needs to be in [0, 1]. It is relative to the "root" viewport.  */
        RELATIVE,

        /** If this type is chosen, the given size will be ignored. Instead all cells with this type will share the rest amount of
         * the "root" viewport that is still left after all other parts have been subtracted.  */
        REST
    }

    /** A sub view for one cell of the [SplitViewport].
     * @author Daniel Holderbaum
     */
    class SubView(sizeInformation: SizeInformation, viewport: Viewport?) {
        /** The size information for this sub view.  */
        var sizeInformation: SizeInformation

        /** The [Viewport] for this sub view.  */
        var viewport: Viewport?

        init {
            this.sizeInformation = sizeInformation
            this.viewport = viewport
        }
    }

    var rootViewport: Viewport
    private var activeViewport: Viewport? = null
    private val rowSizeInformations = Array<SubView>()
    private val subViews = Array<Array<SubView>>()

    /** Adds another row to the split viewport. This has to be called at least once prior to [.add].
     * @param sizeInformation The size information for the row.
     */
    fun row(sizeInformation: SizeInformation) {
        if (sizeInformation.sizeType == SizeType.RELATIVE) {
            validateRelativeSize(sizeInformation.size)
        }

        // for rows we don't need a SubView with a viewport, but to not duplicate some calculation methods, we just create a new
        // SubView
        rowSizeInformations.add(SubView(sizeInformation, null))
        subViews.add(Array())
    }

    /** Adds another sub view to the last added row.
     * @param subView The [SubView] with size and viewport. It can be changed externally. Those changes will be used as soon
     * as the viewport is activated next time.
     */
    fun add(subView: SubView) {
        check(subViews.size != 0) { "A row has to be added first." }
        if (subView.sizeInformation.sizeType == SizeType.RELATIVE) {
            validateRelativeSize(subView.sizeInformation.size)
        }
        val rowViewports = subViews.peek()
        rowViewports.add(subView)
    }

    private val subViewportArea = Rectangle()

    /** Initializes the split viewport.
     * @param rootViewport The viewport to be used to determine the area which the sub viewports can use
     */
    init {
        this.rootViewport = rootViewport
    }

    /** Updates the viewport at (row, column) and sets it as the currently active one. The top left sub viewport is (0, 0).
     * @param row The index of the row with the viewport to be activated. Starts at 0.
     * @param column The index of the column with the viewport to be activated. Starts at 0.
     * @param centerCamera Whether the subView should center the camera or not.
     */
    fun activateSubViewport(row: Int, column: Int, centerCamera: Boolean) {
        validateCoordinates(row, column)
        val rowMap = subViews[row]
        val viewport = rowMap[column].viewport

        // update the viewport simulating a smaller sub view
        calculateSubViewportArea(row, column, subViewportArea)
        viewport!!.update(subViewportArea.width.toInt(), subViewportArea.height.toInt(), centerCamera)

        // some scaling strategies will scale the viewport bigger than the allowed sub view, so we need to limit it
        // viewport.viewportWidth = (int)Math.min(viewport.viewportWidth, subViewportArea.width);
        // viewport.viewportHeight = (int)Math.min(viewport.viewportHeight, subViewportArea.height);

        // now shift it to the correct place
        viewport.screenX += subViewportArea.x.toInt()
        viewport.screenY += subViewportArea.y.toInt()

        // we changed the viewport parameters, now we need to update once more to correct the glViewport
        viewport.update(subViewportArea.width.toInt(), subViewportArea.height.toInt())
        activeViewport = viewport
    }

    // ############################################################
    // The following methods all just delegate to the root viewport
    // ############################################################
    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        rootViewport.update(screenWidth, screenHeight, centerCamera)
    }

    override fun unproject(screenCoords: Vector2): Vector2 {
        return rootViewport.unproject(screenCoords)
    }

    override fun project(worldCoords: Vector2): Vector2 {
        return rootViewport.project(worldCoords)
    }

    override fun unproject(screenCoords: Vector3): Vector3 {
        return rootViewport.unproject(screenCoords)
    }

    override fun project(worldCoords: Vector3): Vector3 {
        return rootViewport.project(worldCoords)
    }

    override fun getPickRay(screenX: Float, screenY: Float): Ray {
        return rootViewport.getPickRay(screenX, screenY)
    }

    override fun calculateScissors(batchTransform: Matrix4, area: Rectangle, scissor: Rectangle) {
        rootViewport.calculateScissors(batchTransform, area, scissor)
    }

    override fun toScreenCoordinates(worldCoords: Vector2, transformMatrix: Matrix4): Vector2 {
        return rootViewport.toScreenCoordinates(worldCoords, transformMatrix)
    }

    override fun getCamera(): Camera {
        return rootViewport.camera
    }

    override fun setCamera(camera: Camera) {
        rootViewport.camera = camera
    }

    override fun setWorldSize(worldWidth: Float, worldHeight: Float) {
        rootViewport.setWorldSize(worldWidth, worldHeight)
    }

    override fun getWorldWidth(): Float {
        return rootViewport.worldWidth
    }

    override fun setWorldWidth(worldWidth: Float) {
        rootViewport.worldWidth = worldWidth
    }

    override fun getWorldHeight(): Float {
        return rootViewport.worldHeight
    }

    override fun setWorldHeight(worldHeight: Float) {
        rootViewport.worldHeight = worldHeight
    }

    val viewportX: Int
        get() = rootViewport.screenX
    val viewportY: Int
        get() = rootViewport.screenY
    val viewportWidth: Int
        get() = rootViewport.screenWidth
    val viewportHeight: Int
        get() = rootViewport.screenHeight

    override fun getLeftGutterWidth(): Int {
        return rootViewport.leftGutterWidth
    }

    override fun getRightGutterX(): Int {
        return rootViewport.rightGutterX
    }

    override fun getRightGutterWidth(): Int {
        return rootViewport.rightGutterWidth
    }

    override fun getBottomGutterHeight(): Int {
        return rootViewport.bottomGutterHeight
    }

    override fun getTopGutterY(): Int {
        return rootViewport.topGutterY
    }

    override fun getTopGutterHeight(): Int {
        return rootViewport.topGutterHeight
    }

    // #################################################################
    // Private utility methods to help with calculations and validations
    // #################################################################
    private fun calculateSubViewportArea(row: Int, col: Int, subViewportArea: Rectangle): Rectangle {
        subViewportArea.x = calculateWidthOffset(subViews[row], col)
        subViewportArea.y = calculateHeightOffset(rowSizeInformations, row)
        subViewportArea.width = calculateSize(subViews[row], col, viewportWidth.toFloat())
        subViewportArea.height = calculateSize(rowSizeInformations, row, viewportHeight.toFloat())
        return subViewportArea
    }

    private fun calculateHeightOffset(subViews: Array<SubView>, index: Int): Float {
        // the glViewport offset is y-up, but the first row is the top most one
        // that's why we start at the top and subtract the row heights
        var heightOffset = viewportHeight.toFloat()
        for (i in 0..index) {
            heightOffset -= calculateSize(subViews, i, viewportHeight.toFloat())
        }

        // add the root offset
        heightOffset += viewportY.toFloat()
        return heightOffset
    }

    private fun calculateWidthOffset(sizeInformations: Array<SubView>, index: Int): Float {
        var widthOffset = 0f
        for (i in 0 until index) {
            widthOffset += calculateSize(sizeInformations, i, viewportWidth.toFloat())
        }

        // add the root offset
        widthOffset += viewportX.toFloat()
        return widthOffset
    }

    /** Used to calculate either the width or height.
     * @param subViews The row informations or column informations of a certain row.
     * @param index The index of the element to be calculated.
     * @param totalSize The total size, either the viewport width or height.
     */
    private fun calculateSize(subViews: Array<SubView>, index: Int, totalSize: Float): Float {
        val subView = subViews[index]
        return when (subView.sizeInformation.sizeType) {
            SizeType.ABSOLUTE -> subView.sizeInformation.size
            SizeType.RELATIVE -> subView.sizeInformation.size * totalSize
            SizeType.REST -> {
                val rests = countRest(subViews)
                val usedSize = calculateUsedSize(subViews, totalSize)
                (totalSize - usedSize) / rests
            }
            else -> throw IllegalArgumentException(subView.sizeInformation.sizeType.toString() + " could not be handled.")
        }
    }

    private fun calculateUsedSize(subViews: Array<SubView>, totalSize: Float): Float {
        var usedSize = 0f
        for (subView in subViews) {
            when (subView.sizeInformation.sizeType) {
                SizeType.ABSOLUTE -> usedSize += subView.sizeInformation.size
                SizeType.RELATIVE -> usedSize += subView.sizeInformation.size * totalSize
            }
        }
        return usedSize
    }

    private fun countRest(subViews: Array<SubView>): Int {
        var rests = 0
        for (subView in subViews) {
            if (subView.sizeInformation.sizeType == SizeType.REST) {
                rests++
            }
        }
        return rests
    }

    private fun validateCoordinates(row: Int, col: Int) {
        require(row < subViews.size) { "There is no row with ID $row" }
        val rowSubViews = subViews[row]
        require(col < rowSubViews.size) { "There is no column with ID $col" }
    }

    private fun validateRelativeSize(size: Float) {
        require(!(size < 0 || size > 1)) { "$size does not fulfill the constraint of 0 <= size <= 1." }
    }
}