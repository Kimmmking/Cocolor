/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cocolor.palette;

import java.util.Arrays;

import android.util.Log;

/**
 * Class which provides a histogram for RGB values.
 */
final class ColorHistogram {

    private final int[] mColors;
    private final int[] mColorCounts;
    private final int mNumberColors;

    /**
     * A new {@link ColorHistogram} instance.
     *
     * @param pixels array of image contents
     */
    ColorHistogram(final int[] pixels) {
        // Sort the pixels to enable counting below
        Arrays.sort(pixels);

        // Count number of distinct colors
        mNumberColors = countDistinctColors(pixels);

        // Create arrays
        mColors = new int[mNumberColors];
        mColorCounts = new int[mNumberColors];

        // Finally count the frequency of each color
        countFrequencies(pixels);
    }

    /**
     * @return 获取共用多少柱不同颜色 number of distinct colors in the image.
     */
    int getNumberOfColors() {
        return mNumberColors;
    }

    /**
     * @return 获取排好序后的不同颜色的数组  an array containing all of the distinct colors in the image.
     */
    int[] getColors() {
        return mColors;
    }

    /**
     * @return 获取保存每一柱有多高的数组 an array containing the frequency of a distinct colors within the image.
     */
    int[] getColorCounts() {
        return mColorCounts;
    }

    //计算共用多少柱不同颜色
    private static int countDistinctColors(final int[] pixels) {
        if (pixels.length < 2) {
            // If we have less than 2 pixels we can stop here
            return pixels.length;
        }

        // If we have at least 2 pixels, we have a minimum of 1 color...
        int colorCount = 1;
        int currentColor = pixels[0];

        // Now iterate from the second pixel to the end, counting distinct colors
        for (int i = 1; i < pixels.length; i++) {
            // If we encounter a new color, increase the population
            if (pixels[i] != currentColor) {
                currentColor = pixels[i];
                colorCount++;
            }
        }

        return colorCount;
    }
   
    //计算每一柱有多高
    private void countFrequencies(final int[] pixels) {
        if (pixels.length == 0) {
            return;
        }

        int currentColorIndex = 0;
        int currentColor = pixels[0];

        mColors[currentColorIndex] = currentColor;
        mColorCounts[currentColorIndex] = 1;

        Log.i("pixels.length",""+ pixels.length);
       
        if (pixels.length == 1) {
            // If we only have one pixel, we can stop here
            return;
        }
       
        // Now iterate from the second pixel to the end, population distinct colors
        for (int i = 1; i < pixels.length; i++) {
            if (pixels[i] == currentColor) {
                // We've hit the same color as before, increase population
                mColorCounts[currentColorIndex]++;
            } else {
                // We've hit a new color, increase index
                currentColor = pixels[i];

                currentColorIndex++;
                mColors[currentColorIndex] = currentColor;
                mColorCounts[currentColorIndex] = 1;
            }
        }
    }

}
