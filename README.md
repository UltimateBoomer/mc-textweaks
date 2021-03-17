
# TexTweaks Mod for Minecraft Fabric
Utilities for high resolution texture packs

## Features

### Texture Scaling
Upscale or downscale textures at runtime with customizable algorithm (linear/nearest neighbor). This is useful for:
- Creating complete mipmaps (more about this down below)
- Reducing the hardware requirements of texture packs that are only available at very high resolutions
- Using SEUS PTGI with different resource packs that have different resolutions
- [Playing Minecraft using 1x1 textures](https://raw.githubusercontent.com/UltimateBoomer/mc-textweaks/assets/screenshots/1by1.png)

### Better Mipmaps

|256x resource pack w/ 4x mipmapping|256x resource pack w/ 8x mipmapping|

By default, Minecraft allows you to set mipmap levels up to 4 in the options. This is enough for the default 16x16 textures. But, if you are using a higher resolution texture pack, the mipmaps will *not be completed* (not going all the way down to 1x1). This causes distant textures to be more aliased than normal.

It's not so easy to just force the game to generate more levels, as it requires all textures to be equal or larger than two to the power of the mipmap level, and most high resolution texture packs don't have all textures (including items) done. However, by upscaling all textures to the same size in game, it's now possible to generate higher level mipmaps easily, without bothering with any image tools.

Here is a comparison of 4x mipmapping vs 8x mipmapping using a 256x resource pack (view at native resolution to see the details):

|![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/mipmap_aliased.png)|![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/mipmap_smooth.png)|
|----|---|

To test if this is working, press the "Show Texture Info" keybind (unbound by default) and check the mipmap level for the `block.png` atlas.

### Set mipmap LOD bias

LOD Bias changes how far before the next mipmap level is used. This can be used for fine-tuning sharpness and aliasing of the textures, or as a way to give Minecraft an interesting look.

- Fine-tune distant texture detail
- Works with shaders (previously forcing it in the graphics control panel may make the screen blurry when using shaders)

![](https://raw.githubusercontent.com/UltimateBoomer/mc_textweaks/assets/screenshots/lod_bias_high.png)
LOD bias at +3, distant textures have lower resolution. (Optifine is needed for foliage to be correctly mipmapped)
 
## Basic Setup
1. Install mod
2. Open mod config through ModMenu
3. Set texture resolution to the highest resolution of all used resource packs
	* Unit is in power of 2 (4 = 16x, 5 = 32x, 6 = 64x etc.)
4. After setting up the mod config, reload resources with F3+T
5. Done!

## Compatibility
- Mod implements [niapi](https://github.com/UltimateBoomer/mc-niapi)
- No incompatibilities found so far (tested with Sodium, Sodium + Iris, Optifine, Canvas Renderer)
