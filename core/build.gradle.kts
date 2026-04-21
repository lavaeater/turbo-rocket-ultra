plugins {
    alias(libs.plugins.kotlin.jvm)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

eclipse.project.name = "${extra["appName"]}-core"

dependencies {
    api(libs.ashley)
    api(project(":box2dlights"))
    api(project(":screenmanager"))
    api(libs.gdxControllers)
    api(libs.ai)
    api(libs.gdx)
    api(libs.gdxBox2d)
    api(libs.gdxFreetype)
    api(libs.bladeInk)
    api(project(":gdx-vfx-core"))
    api(project(":gdx-vfx-effects"))
    api(libs.kryo)
    api(libs.websocket)
    api(libs.kryoNet)
    api(libs.noise4j)
    api(libs.tinyVG)
    api(libs.tenPatch)
    api(libs.anim8)
    api(libs.colorful)
    api(libs.utlisBox2d)
    api(libs.utlis)
    api(libs.makeSomeNoise)
    api(libs.regExodus)
    api(libs.textratypist)
    api(libs.typingLabel)
    api(libs.ingameconsole)
    api(libs.joise)
    api(libs.ktxActors)
    api(libs.ktxApp)
    api(libs.ktxAshley)
    api(libs.ktxAssetsAsync)
    api(libs.ktxAssets)
    api(libs.ktxAsync)
    api(libs.ktxBox2d)
    api(libs.ktxCollections)
    api(libs.ktxFreetype)
    api(libs.ktxGraphics)
    api(libs.ktxI18n)
    api(libs.ktxInject)
    api(libs.ktxJson)
    api(libs.ktxLog)
    api(libs.ktxMath)
    api(libs.ktxPreferences)
    api(libs.ktxReflect)
    api(libs.ktxScene2d)
    api(libs.ktxStyle)
    api(libs.ktxTiled)
    api(libs.kotlinxCoroutines)
    api(libs.shapeDrawer)
    api(libs.simpleGraphs)
    api(libs.kotlinReflect)

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}
