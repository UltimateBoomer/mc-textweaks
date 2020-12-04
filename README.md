
# TexTweaks Mod for Minecraft Fabric
Utilities for high resolution texture packs

## Features

### Upscale lower resolution textures to a higher resolution
 - Required for higher mipmap levels
 - Scaled textures will look identical to original textures
 - Useful for using SEUS PTGI with different resource packs that have different resolutions
 - Note: will make loading take more time, can significantly increase RAM and VRAM usage at higher resolutions

### Set mipmap levels above vanilla's maximum of 4
|![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/mipmap_aliased.png)|![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/mipmap_smooth.png)|
|----|---|
|256x resource pack w/ 4x mipmapping|256x resource pack w/ 8x mipmapping|

 - Reduces distant aliasing when using higher resolution texture packs
 - Note: increases VRAM usage

### Set mipmap LOD bias
 - Fine-tune distant texture detail
 - Works with shaders (previously forcing it in the graphics control panel may make the screen blurry when shaders are turned on)

![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/lod_bias_high.png)
LOD bias at +3, distant textures have lower resolution. Optifine is needed for foliage to be mipmapped.
 
## Basic Setup
1. Install mod
2. Open mod config through ModMenu
3. Set texture resolution to the highest resolution of all used resource packs
	* Unit is in power of 2 (4 = 16x, 5 = 32x, 6 = 64x etc.)
4. After setting up the mod config, reload resources with F3+T
5. Done!

## Compatibility
 - Compatible with Optifine
 - Should be compatible with Sodium
