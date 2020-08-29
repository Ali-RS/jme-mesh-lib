package com.jayfella.mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * A Rotated Blob Tile is a Blob Tile that rotates textures instead of providing textures for all rotations.
 */
public class RotatedBlobTile {

    /**
     * Returns the amount of clockwise 90 degree rotations required to display the texture correctly.
     */
    private static final Map<Integer, Integer> rotationTable = new HashMap<>();

    /**
     * Converts a configuration to a texture ID between 0-14.
     */
    private static final Map<Integer, Integer> idToTextureTable = new HashMap<>();

    static {
        populateRotationTable();
        populateConfigurationToTextureTable();
    }

    private static void populateRotationTable() {

        rotationTable.put(0, 0);

        rotationTable.put(1, 0);
        rotationTable.put(4, 1);
        rotationTable.put(16, 2);
        rotationTable.put(64, 3);

        rotationTable.put(5,0);
        rotationTable.put(20,1);
        rotationTable.put(80,2);
        rotationTable.put(65,3);

        rotationTable.put(7,0);
        rotationTable.put(28,1);
        rotationTable.put(112,2);
        rotationTable.put(193,3);

        rotationTable.put(17,0);
        rotationTable.put(68,1);

        rotationTable.put(21,0);
        rotationTable.put(84,1);
        rotationTable.put(81,2);
        rotationTable.put(69,3);

        rotationTable.put(23,0);
        rotationTable.put(92,1);
        rotationTable.put(113,2);
        rotationTable.put(197,3);

        rotationTable.put(29,0);
        rotationTable.put(116,1);
        rotationTable.put(209,2);
        rotationTable.put(71,3);

        rotationTable.put(31,0);
        rotationTable.put(124,1);
        rotationTable.put(241,2);
        rotationTable.put(199,3);

        rotationTable.put(85,0);

        rotationTable.put(87,0);
        rotationTable.put(93,1);
        rotationTable.put(117,2);
        rotationTable.put(213,3);

        rotationTable.put(95,0);
        rotationTable.put(125,1);
        rotationTable.put(245,2);
        rotationTable.put(215,3);

        rotationTable.put(119,0);
        rotationTable.put(221,2);

        rotationTable.put(127,0);
        rotationTable.put(253,1);
        rotationTable.put(247,2);
        rotationTable.put(223,3);

        rotationTable.put(255,0);

    }

    private static void populateConfigurationToTextureTable() {

        idToTextureTable.put(0, 0);

        idToTextureTable.put(1, 1);
        idToTextureTable.put(4, 1);
        idToTextureTable.put(16, 1);
        idToTextureTable.put(64, 1);

        idToTextureTable.put(5, 2);
        idToTextureTable.put(20, 2);
        idToTextureTable.put(80, 2);
        idToTextureTable.put(65, 2);

        idToTextureTable.put(7, 3);
        idToTextureTable.put(28, 3);
        idToTextureTable.put(112, 3);
        idToTextureTable.put(193, 3);

        idToTextureTable.put(17, 4);
        idToTextureTable.put(68, 4);

        idToTextureTable.put(21, 5);
        idToTextureTable.put(84, 5);
        idToTextureTable.put(81, 5);
        idToTextureTable.put(69, 5);

        idToTextureTable.put(23, 6);
        idToTextureTable.put(92, 6);
        idToTextureTable.put(113, 6);
        idToTextureTable.put(197, 6);

        idToTextureTable.put(29, 7);
        idToTextureTable.put(116, 7);
        idToTextureTable.put(209, 7);
        idToTextureTable.put(71, 7);

        idToTextureTable.put(31, 8);
        idToTextureTable.put(124, 8);
        idToTextureTable.put(241, 8);
        idToTextureTable.put(199, 8);

        idToTextureTable.put(85, 9);

        idToTextureTable.put(87, 10);
        idToTextureTable.put(93, 10);
        idToTextureTable.put(117, 10);
        idToTextureTable.put(213, 10);

        idToTextureTable.put(95, 11);
        idToTextureTable.put(125, 11);
        idToTextureTable.put(245, 11);
        idToTextureTable.put(215, 11);

        idToTextureTable.put(119, 12);
        idToTextureTable.put(221, 12);

        idToTextureTable.put(127, 13);
        idToTextureTable.put(253, 13);
        idToTextureTable.put(247, 13);
        idToTextureTable.put(223, 13);

        idToTextureTable.put(255, 14);

    }

    public static byte getConfiguration(boolean n, boolean e, boolean s, boolean w,
                                        boolean nw, boolean ne, boolean sw, boolean se) {

        byte configuration = 0;

        if (n) configuration |= 1;
        if (e) configuration |= 1 << 2;
        if (s) configuration |= 1 << 4;
        if (w) configuration |= 1 << 6;

        if (ne && n && e) configuration |= 1 << 1;
        if (se && s && e) configuration |= 1 << 3;
        if (sw && s && w) configuration |= 1 << 5;
        if (nw && n && w) configuration |= 1 << 7;

        return configuration;
    }

    /**
     * Returns how many times to rotate the texture clockwise 90 degrees for the given configuration.
     *
     * @throws NullPointerException if the given signed configuration is invalid.
     *
     * @param configuration the unsigned integer tileset configuration.
     * @return the amount of 90 degree clockwise rotations the texture should rotate to display correctly.
     */
    public static int getTextureRotationCount(int configuration) {
        return rotationTable.get(configuration);
    }

    public static int getTextureRotationCount(byte configuration) {
        return rotationTable.get(Byte.toUnsignedInt(configuration));
    }

    /**
     * Converts a configuration to a texture index between 0-14.
     *
     * @throws NullPointerException if the given configuration value is invalid.
     *
     * @param unsignedInt the unsigned integer tileset configuration.
     * @return the texture ID between 0-14
     */
    public static int getTextureIndex(int unsignedInt) {
        return idToTextureTable.get(unsignedInt);
    }

    public static int getTextureIndex(byte configuration) {
        return idToTextureTable.get(Byte.toUnsignedInt(configuration));
    }

}
