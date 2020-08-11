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