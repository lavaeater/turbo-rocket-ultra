Code Review: Turbo Rocket Ultra

Critical Bugs

1. Off-by-one: fires N+1 projectiles                                                                                                
   core/.../ecs/systems/player/PlayerShootingSystem.kt:209                                                                             
   for (projectile in 0..weapon.numberOfProjectiles)  // BUG: 0..N is inclusive                                                        
   // Should be: 0 until weapon.numberOfProjectiles                                                                                    
   Shotguns and spread-fire weapons fire one extra projectile every shot.

2. Off-by-one: melee scan arc is slightly too wide                                                                                  
   PlayerShootingSystem.kt:150                                                                                                         
   Same issue — 0..numberOfSteps generates one extra scan point, widening the FOV.

3. Force-unwrap chains in ContactManager during physics callbacks                                                                   
   core/.../physics/ContactManager.kt:328,370 and similar:                                                                             
   Assets.newSoundEffects["weapons"]!!["grenade"]!!.random()                                                                           
   inventory.ammo[loot.ammoType]!!                           
   Missing asset keys or ammo types crash the game mid-physics-step with a NullPointerException. These run inside the Box2D contact    
   callback, which is especially bad timing.

4. RenderSystem sorts by position.y twice                 
   core/.../ecs/systems/graphics/RenderSystem.kt:58-60                                                                                 
   val x0 = p0.transform().position.y  // should be .x       
   val x1 = p1.transform().position.y  // should be .x                                                                                 
   The X-coordinate tie-break is never actually used; sprites at the same layer and Y may flicker or have wrong draw order.

  ---                                                                                                                                 
Likely Bugs

5. Unsafe userData cast in Box2D extensions               
   core/.../physics/Box2DExtensions.kt:29                                                                                              
   (userData as Entity)  // ClassCastException if null or wrong type                                                                   
   No guard before casting fixture userData.

6. Duplicate grenade detonation paths                                                                                               
   Both PhysicsSystem (height ≤ 0 check) and ContactManager (collision callback) can trigger grenade detonation. Double-detonation     
   causes duplicate effects or double damage.

7. Stale entity access in contact callbacks                                                                                         
   ContactManager.kt contact handlers assume both entities still exist. If an entity is DestroyComponent-flagged and removed mid-frame,
   reading its components in the contact callback is undefined.

  ---                                                                                                                                 
Code Smells

8. ContactManager.kt is a God Object (~407 lines, ~59 contact branches)
   All contact logic — damage, audio, UI, game state — lives in one class. Worth decomposing into per-contact-type handlers.

9. Magic weapon name strings                                                                                                        
   factories/Factories.kt:87-101: "Molotov Cocktail" and "Grenade" used for dispatch. A typo silently breaks the weapon with no error.

10. ~78 unsafe mapper extensions with no validation layer                                                                           
    AshleyMappers.kt: Every mapper does a raw .get(entity) call. There's no checked wrapper pattern, so callers have to remember to     
    verify component existence themselves.

11. Mutable global state in FitnessTracker                                                                                          
    EnemyDeathSystem.kt: FitnessTracker.fitnessData is a global mutable list modified per entity death. Entity-specific state shouldn't
    live in a static global.

  ---                                                                                                                                 
Dependency Upgrade Opportunities

┌───────────────────┬─────────────┬────────────────────────┐
│      Library      │   Current   │      Recommended       │                                                                        
├───────────────────┼─────────────┼────────────────────────┤
│ Kotlin            │ 1.8.0       │ 1.9.x or 2.0.x         │
├───────────────────┼─────────────┼────────────────────────┤
│ KTX               │ 1.11.0-rc5  │ 1.11.0 stable or newer │                                                                        
├───────────────────┼─────────────┼────────────────────────┤                                                                        
│ LibGDX            │ 1.11.0      │ 1.12.x+                │                                                                        
├───────────────────┼─────────────┼────────────────────────┤                                                                        
│ Kotlin Coroutines │ 1.6.4       │ 1.8.x                  │
├───────────────────┼─────────────┼────────────────────────┤                                                                        
│ box2dlights       │ commit hash │ fork to stable release │
└───────────────────┴─────────────┴────────────────────────┘

Ashley (1.7.4) and GDX AI (1.8.2) are effectively EOL but functional — no action needed there.

### Proposed versions

ktxVersion=1.13.1-rc1
kotlinVersion=2.3.10
aiVersion=1.8.2
ashleyVersion=1.7.4
box2dlightsVersion=1.5
gdxControllersVersion=2.2.4
artemisOdbVersion=2.3.0
bladeInkVersion=1.3.2
colorfulVersion=0.10.0
controllerMappingVersion=2.3.0
cringeVersion=0.3.0
cruxVersion=0.1.3
digitalVersion=0.10.0
flexBoxVersion=818ccf2764
gandVersion=0.3.6
gdcruxVersion=0.1.2
hackLightsVersion=f0ba5deaff
kotlinxCoroutinesVersion=1.10.2
visUiVersion=1f8b37a24b
screenManagerVersion=0.7.1
guacamoleVersion=v0.3.6
utilsBox2dVersion=0.13.7
utilsVersion=0.13.7
miniaudioVersion=0.7
shapeDrawerVersion=2.6.0
simpleGraphsVersion=5.1.1
inGameConsoleVersion=1.0.1
stripeVersion=2.0.0
tantrumDigitalVersion=0.10.0.22
foryVersion=0.16.0
typingLabelVersion=1.4.0
regExodusVersion=0.1.21
universalTweenVersion=6.3.3
gdxVfxCoreVersion=0.5.4
gdxVfxEffectsVersion=0.5.4
lwjgl3Version=3.4.1
graalHelperVersion=2.0.1
gdxTeaVMVersion=1.5.3
enableGraalNative=false
gdxVersion=1.14.0

                                                            
---                                                                                                                                 
Recommended Priority

1. Fix the 0..N → 0 until N loops in PlayerShootingSystem — easy and clearly wrong
2. Guard the !! chains in ContactManager with null-safe access or early returns
3. Fix the position.y/position.y duplicate in RenderSystem
4. Upgrade Kotlin to 1.9.x and pin KTX to a stable release
5. Address the stale-entity contact issue defensively (check engine.getEntity or a DestroyComponent presence before processing)