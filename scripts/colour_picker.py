#!/bin/python
"""Script to choose some random RGB colours that are also CMYK-safe."""

import math
import sys
import numpy as np
from colormath.color_objects import CMYKColor, sRGBColor
from colormath.color_conversions import convert_color


def pick_colours(num_colours):
    _colours = []

    for i in xrange(0, num_colours):
        x = [1.0, 1.0, 1.0, 1.0]
        x_squared = [1.0, 1.0, 1.0, 1.0]

        # Use Marsaglia's (1972) method for choosing random points on a unit 4-sphere.
        # http://mathworld.wolfram.com/HyperspherePointPicking.html
        while (x_squared[0] + x_squared[1]) >= 1.0 or (x_squared[2] + x_squared[3]) >= 1.0:
            x[0] = np.random.normal()
            x[1] = np.random.normal()
            x[2] = np.random.normal()
            x[3] = np.random.normal()

            x_squared[0] = x[0] * x[0]
            x_squared[1] = x[1] * x[1]
            x_squared[2] = x[2] * x[2]
            x_squared[3] = x[3] * x[3]

        c = x[0]
        m = x[1]
        y = x[2] * math.sqrt((1.0 - x_squared[0] - x_squared[1]) / (x_squared[2] + x_squared[3]))
        k = x[3] * math.sqrt((1.0 - x_squared[0] - x_squared[1]) / (x_squared[2] + x_squared[3]))

        cmyk = CMYKColor((c + 1.0) / 2.0, (m + 1.0) / 2.0, (y + 1.0) / 2.0, (k + 1.0) / 2.0)
        rgb = convert_color(cmyk, sRGBColor)

        _colours.append(rgb.get_rgb_hex())

    return _colours

if __name__ == '__main__':
    try:
        n = int(sys.argv[1])
        colours = pick_colours(n)
        print(colours)
    except ValueError:
        print('Usage: colour_picker <n>')
        print('Where <n> is a positive integer')