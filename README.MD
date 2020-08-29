A mesh library for jMonkeyEngine

Includes test classes for all implementations.

- Marching Squares
- Marching Cubes

### JmeMesh
A class that extends Mesh to add convenience methods to set buffers.

### HeightMap
A heightmap mesh that takes a float[][] array. The array must be +3 in size to account
for the extra vertex at the edges, and +2 to calculate the edge normals correctly.

### Mesh Simplifier (decimator)
Simplifies the given mesh by removing vertices that exceed a given angle threshold, increasing
the angle on each iteration until the desired limit has been met.

### 2D Blob TileSet
An implementation of 2D a BlobTileSet. Derived from [this article](https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673).
 