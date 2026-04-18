
What it is

A skeletal/hardpoint-based character rendering system. The goal is to
move away from simple sprite blitting toward characters with named    
anchor points (shoulders, hips, etc.) that drive arm positioning,     
weapon aiming, and direction-aware layering.

Core pieces

- CharacterComponent — stores world pos, facing direction, aim vector,
  and a map of named hardpoints that rotate with the character
- Arm IK — trigonometric inverse kinematics to bend upper/lower arms  
  toward a grip point, driven by the mouse aim angle
- Skewed sprite rendering — the torso is drawn as a deformable polygon
  using hardpoints as vertices, giving a faux-3D perspective effect
- RenderableType.CharacterWithArms — orchestrates the multi-layer draw
  order (N/S/E/W each have different front/back arm sequencing for     
  depth sorting)
- HardPointConceptScreen — standalone demo screen with mouse aiming   
  and keyboard rotation for testing in isolation

How complete

~70-80%. The hardpoint system, IK, and skewed torso rendering all     
work. What's missing is the integration side — making walk/run      
animations coexist with the new arm system, and wiring it into the    
full game. The last few commits hit that complexity ceiling.

Notable side work

A chunk of AI/steering components were moved into gdx-lava as part of
cleanup on this branch.