package com.jayfella.mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Source: https://gamedevelopment.tutsplus.com/tutorials/how-to-use-tile-bitmasking-to-auto-tile-your-level-layouts--cms-25673
 *
 * A problem you may notice is that the values calculated by the 8-bit bitmasking procedure no longer correlate to the
 * sequential order of the tiles in the sprite sheet. There are only 48 tiles, but our possible calculated values range
 * from 0 to 255, so we can no longer use the calculated value as a direct reference when grabbing the appropriate sprite.
 *
 * What we need, therefore, is a data structure to contain the list of calculated values and their corresponding tile
 * values. How you want to implement this is up to you, but remember that the order in which you check for surrounding
 * tiles dictates the order in which your tiles should be placed in the sprite sheet.
 *
 * For this example, we check for bordering tiles in the following order:
 * North-West, North, North-East, West, East, South-West, South, South-East.
 *
 * Below is the complete set of bitmasking values as they relate to the positions of tiles in our sprite sheet
 * (feel free to use these values in your project to save time):
 */
public class BlobTile {

    /**
     * Returns the texture index for a given configuration.
     */
    private static final Map<Integer, Integer> lookupTable = new HashMap<>();
    private static final Map<Integer, Integer> artistsLayoutTable = new HashMap<>();

    static {

        lookupTable.put(2, 1);
        lookupTable.put(8, 2);
        lookupTable.put(10, 3);
        lookupTable.put(11, 4);
        lookupTable.put(16, 5);
        lookupTable.put(18, 6);
        lookupTable.put(22, 7);
        lookupTable.put(24, 8);
        lookupTable.put(26, 9);
        lookupTable.put(27, 10);
        lookupTable.put(30, 11);
        lookupTable.put(31, 12);
        lookupTable.put(64, 13);
        lookupTable.put(66, 14);
        lookupTable.put(72, 15);
        lookupTable.put(74, 16);
        lookupTable.put(75, 17);
        lookupTable.put(80, 18);
        lookupTable.put(82, 19);
        lookupTable.put(86, 20);
        lookupTable.put(88, 21);
        lookupTable.put(90, 22);
        lookupTable.put(91, 23);
        lookupTable.put(94, 24);
        lookupTable.put(95, 25);
        lookupTable.put(104, 26);
        lookupTable.put(106, 27);
        lookupTable.put(107, 28);
        lookupTable.put(120, 29);
        lookupTable.put(122, 30);
        lookupTable.put(123, 31);
        lookupTable.put(126, 32);
        lookupTable.put(127, 33);
        lookupTable.put(208, 34);
        lookupTable.put(210, 35);
        lookupTable.put(214, 36);
        lookupTable.put(216, 37);
        lookupTable.put(218, 38);
        lookupTable.put(219, 39);
        lookupTable.put(222, 40);
        lookupTable.put(223, 41);
        lookupTable.put(248, 42);
        lookupTable.put(250, 43);
        lookupTable.put(251, 44);
        lookupTable.put(254, 45); 
        lookupTable.put(255, 46);
        lookupTable.put(0, 47);

        artistsLayoutTable.put(0,18);
        artistsLayoutTable.put(1,1);
        artistsLayoutTable.put(2,41);
        artistsLayoutTable.put(3,6);
        artistsLayoutTable.put(4,12);
        artistsLayoutTable.put(5,43);
        artistsLayoutTable.put(6,7);
        artistsLayoutTable.put(7,2);
        artistsLayoutTable.put(8,5);
        artistsLayoutTable.put(9,4);
        artistsLayoutTable.put(10,3);
        artistsLayoutTable.put(11,31);
        artistsLayoutTable.put(12,15);
        artistsLayoutTable.put(13,35);
        artistsLayoutTable.put(14,13);
        artistsLayoutTable.put(15,47);
        artistsLayoutTable.put(16,20);
        artistsLayoutTable.put(17,27);
        artistsLayoutTable.put(18,36);
        artistsLayoutTable.put(19,40);
        artistsLayoutTable.put(20,14);
        artistsLayoutTable.put(21,8);
        artistsLayoutTable.put(22,30);
        artistsLayoutTable.put(23,32);
        artistsLayoutTable.put(24,37);
        artistsLayoutTable.put(25,11);
        artistsLayoutTable.put(26,34);
        artistsLayoutTable.put(27,23);
        artistsLayoutTable.put(28,39);
        artistsLayoutTable.put(29,46);
        artistsLayoutTable.put(30,10);
        artistsLayoutTable.put(31,19);
        artistsLayoutTable.put(32,10); // 30 and 32 are the same.
        artistsLayoutTable.put(33,26);
        artistsLayoutTable.put(34,24);
        artistsLayoutTable.put(35,28);
        artistsLayoutTable.put(36,21);
        artistsLayoutTable.put(37,44);
        artistsLayoutTable.put(38,9);
        artistsLayoutTable.put(39,32);
        artistsLayoutTable.put(40,17);
        artistsLayoutTable.put(41,38);
        artistsLayoutTable.put(42,45);
        artistsLayoutTable.put(43,33);
        artistsLayoutTable.put(44,22);
        artistsLayoutTable.put(45,25);
        artistsLayoutTable.put(46,18);
        artistsLayoutTable.put(47,0);

    }

    public static int toArtistsLayout(int textureId) {
        try {
            return  artistsLayoutTable.get(textureId);
        }
        catch (NullPointerException e) {
            return 0;
        }

    }

    /**
     * Returns a tile configuration of a tile based on its neighbors.
     * @param n  true if a tile exists to the north of this tile.
     * @param e true if a tile exists to the east of this tile.
     * @param s true if a tile exists to the south of this tile.
     * @param w true if a tile exists to the west of this tile.
     * @param nw true if a tile exists to the north-west of this tile.
     * @param ne true if a tile exists to the north-east of this tile.
     * @param sw true if a tile exists to the south-west of this tile.
     * @param se true if a tile exists to the south-east of this tile.
     * @return a tile configuration to determine which texture to display.
     */
    public static byte getConfiguration(boolean n, boolean e, boolean s, boolean w,
                                        boolean nw, boolean ne, boolean sw, boolean se) {

        // It irritates me that the method args aren't in the correct order.
        // It should be top-left to bottom-right: nw,n,ne  w,e  sw,s,se

        byte configuration = 0;

        if (n) configuration |= 1 << 1;
        if (w) configuration |= 1 << 3;
        if (e) configuration |= 1 << 4;
        if (s) configuration |= 1 << 6;

        if (nw && n && w) configuration |= 1;
        if (ne && n && e) configuration |= 1 << 2;
        if (sw && s && w) configuration |= 1 << 5;
        if (se && s && e) configuration |= 1 << 7;

        return configuration;
    }

    public static int getTextureIndex(int unsignedInt) {
        return lookupTable.get(unsignedInt);
    }

    public static int getTextureIndex(byte value) {
        return lookupTable.get(Byte.toUnsignedInt(value));
    }

}
