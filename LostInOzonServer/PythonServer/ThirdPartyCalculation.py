import numpy as np

from numpy import pi, cos, sin, arctan2, sqrt, dot
def geo2mag(incoord):
    """geographic coordinate to magnetic coordinate:

        incoord is numpy array of shape (2,*)
        array([[glat0,glat1,glat2,...],
            [glon0,glon1,glon2,...])
        where glat, glon are geographic latitude and longitude
        (or if you have only one point it is [[glat,glon]])

        returns
        array([mlat0,mlat1,...],
            [mlon0,mlon1,...]])
        """

    # SOME 'constants'...
    lon = 288.59 # or 71.41W
    lat = 79.3
    r = 1.0

    # convert first to radians
    lon, lat = [x*pi/180 for x in lon,lat]

    glat = incoord[0] * pi / 180.0
    glon = incoord[1] * pi / 180.0
    galt = glat * 0. + r

    coord = np.vstack([glat,glon,galt])

    # convert to rectangular coordinates
    x = coord[2]*cos(coord[0])*cos(coord[1])
    y = coord[2]*cos(coord[0])*sin(coord[1])
    z = coord[2]*sin(coord[0])
    xyz = np.vstack((x,y,z))

    # computer 1st rotation matrix:
    geo2maglon = np.zeros((3,3), dtype='float64')
    geo2maglon[0,0] = cos(lon)
    geo2maglon[0,1] = sin(lon)
    geo2maglon[1,0] = -sin(lon)
    geo2maglon[1,1] = cos(lon)
    geo2maglon[2,2] = 1.
    out = dot(geo2maglon , xyz)

    tomaglat = np.zeros((3,3), dtype='float64')
    tomaglat[0,0] = cos(.5*pi-lat)
    tomaglat[0,2] = -sin(.5*pi-lat)
    tomaglat[2,0] = sin(.5*pi-lat)
    tomaglat[2,2] = cos(.5*pi-lat)
    tomaglat[1,1] = 1.
    out = dot(tomaglat , out)

    mlat = arctan2(out[2], 
            sqrt(out[0]*out[0] + out[1]*out[1]))
    mlat = mlat * 180 / pi
    mlon = arctan2(out[1], out[0])
    mlon = mlon * 180 / pi

    outcoord = np.vstack((mlat, mlon))
    return outcoord

def GetInclination(day) :
    day_of_year = day
    inclination = 23.45 * np.sin((day_of_year - 81) * 360. / 365 )
    delta = 90 - 23 - abs(inclination)
    return delta

import math
def RigidityCutOff(latitude, inclination) :
    lambd_rad = math.radians(latitude)
    delta_rad = math.radians(inclination)
    under_square = 1 - np.cos(delta_rad) * (np.cos(lambd_rad) ** 3) 
    Rc = 57.2 * (np.cos(lambd_rad) ** 4) / ((1 + under_square ** 0.5) ** 2)
    return Rc

def Energy(rigidity, q, a):
    amu = 931.5
    energy = (((rigidity ** 2) * (q / a * amu) ** 2 + 1) ** 0.5 - 1) * amu
    return energy

def Rigidity(velocity) :
    c = 3. * 10 ** 8
    m = 1.6 * (10 ** -27)
    ev = 6.242 * (10 ** 18)
    gamma = (1 / (1 - (velocity / c) ** 2) ** 0.5)
    energy = m * c ** 2 * gamma

    energy_ev = energy * ev

    return ((1.876 + energy_ev) * energy_ev) ** 0.5 * (10 ** -9)

import datetime as dt
def IsParticlePass(latitude, longitude, velocity) :
    mag =  geo2mag(np.array([[latitude, longitude]]).T).T
    print mag
    inclination = GetInclination(dt.datetime.now().timetuple().tm_yday)
    rigidity = Rigidity(velocity)
    rigidityCutOff = RigidityCutOff(mag[0,0], inclination)
    print rigidity
    print rigidityCutOff
    return rigidity > rigidityCutOff

IsParticlePass(11.6, -160, 700)
