package ecs.systems

//class EnterVehicleSystem : IteratingSystem(
//    allOf(
//        EnterVehicleComponent::class,
//        PlayerControlComponent::class).get(), 10) {
//
//    private val bodyMapper = mapperFor<BodyComponent>()
//    private val isInVehicleMapper = mapperFor<IsInVehicleComponent>()
//
//    override fun processEntity(entity: Entity, deltaTime: Float) {
//        if(entity.hasNot(isInVehicleMapper)) {
//            entity.add(IsInVehicleComponent())
//            entity.playerControlComponent().stationary = true
//            val pBody = bodyMapper.get(entity).body
//            val vBody = vehicle(pBody.worldCenter)
//            pBody.revoluteJointWith(vBody) {
//                localAnchorA.set(bodyA.localCenter)
//                localAnchorB.set(bodyB.localCenter.cpy().set(0f, -1.5f))
//            }
//        }
//    }
//}

