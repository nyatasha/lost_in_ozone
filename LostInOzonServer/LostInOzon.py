#!/usr/bin/python
# coding: utf-8

import numpy as np
import math
import datetime as dt

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
    lon = 288.59  # or 71.41W
    lat = 79.3
    r = 1.0

    # convert first to radians
    lon, lat = [x*pi/180 for x in lon, lat]

    glat = incoord[0] * pi / 180.0
    glon = incoord[1] * pi / 180.0
    galt = glat * 0. + r

    coord = np.vstack([glat, glon, galt])

    # convert to rectangular coordinates
    x = coord[2]*cos(coord[0])*cos(coord[1])
    y = coord[2]*cos(coord[0])*sin(coord[1])
    z = coord[2]*sin(coord[0])
    xyz = np.vstack((x, y, z))

    # computer 1st rotation matrix:
    geo2maglon = np.zeros((3, 3), dtype='float64')
    geo2maglon[0, 0] = cos(lon)
    geo2maglon[0, 1] = sin(lon)
    geo2maglon[1, 0] = -sin(lon)
    geo2maglon[1, 1] = cos(lon)
    geo2maglon[2, 2] = 1.
    out = dot(geo2maglon, xyz)

    tomaglat = np.zeros((3, 3), dtype='float64')
    tomaglat[0, 0] = cos(.5*pi-lat)
    tomaglat[0, 2] = -sin(.5*pi-lat)
    tomaglat[2, 0] = sin(.5*pi-lat)
    tomaglat[2, 2] = cos(.5*pi-lat)
    tomaglat[1, 1] = 1.
    out = dot(tomaglat, out)

    mlat = arctan2(out[2], sqrt(out[0]*out[0] + out[1]*out[1]))
    mlat = mlat * 180 / pi
    mlon = arctan2(out[1], out[0])
    mlon = mlon * 180 / pi

    outcoord = np.vstack((mlat, mlon))
    return outcoord


#if __name__ == '__main__':
#    mag = geo2mag(np.array([[79.3, 288.59]]).T).T
#    print mag  # should be [90,0]
#
#    mag = geo2mag(np.array([[90, 0]]).T).T
#    print mag  # should be [79.3,*]
#
#    mag = geo2mag(np.array([
#        [79.3, 288.59],
#        [90, 0]
#        ]).T).T
#
#    print mag  # should be [ [90,0]. [79.3,*] ]
#
#    # kyoto, japan
#    mag = geo2mag(np.array([[35., 135.45]]).T).T
#    print mag  # should be [25.18, -155.80], according to
#               # this site using value for 1995
#               # http://wdc.kugi.kyoto-u.ac.jp/igrf/gggm/index.html


def GetInclination(day):
    day_of_year = day
    inclination = 23.45 * np.sin((day_of_year - 81) * 360. / 365)
    delta = 90 - 23 - abs(inclination)
    return delta


def CalculateRigidity(latitude, inclination):
    lambd_rad = math.radians(latitude)
    delta_rad = math.radians(inclination)
    under_square = 1 - np.cos(delta_rad) * (np.cos(lambd_rad) ** 3)
    Rc = 57.2 * (np.cos(lambd_rad) ** 4) / ((1 + under_square ** 0.5) ** 2)
    # For simplicity counting without inclination
    # Rc = 57.2 * (np.cos(lambd_rad) ** 4) / 4
    return Rc


def EnergyCutoff(rigidity, q, a):
    amu = 931.5
    return (sqrt((rigidity * 10**3) ** 2 * (q / a * amu) ** 2 + 1) - 1) * amu


def speed_to_ev(m, v):
    c = 2.998 * 10 ** 8
    ev = 6.242 * (10 ** 18)
    gamma = (1 / (1 - (v / c) ** 2) ** 0.5)
    return m * c ** 2 * gamma * ev


def ParticleRigidity(m, v):
    # rest_mass in GeV / c ** 2, so rigidity is in the same order
    #c = 2.998 * 10 ** 8
    #e = 1.602 * 10 ** -19
    #momentum = rest_mass * velocity / sqrt(1 - (velocity / c) ** 2)
    #print momentum
    #return momentum * c / (z * e)
    energy_ev = speed_to_ev(m, v)

    return ((1.876 + energy_ev) * energy_ev) ** 0.5


PROTON = 0
ELECTRON = 1


# flux_density and particle_energy are arrays. Index - per particle
# Counts in Sv/s
# Particle energy in MeV
def EquivelentRadiation(flux_density, particle_energy):
    radiation_quality = [(5, 10),  # protons (<5MeV, >=5MeV),
                         1, ]  # electrons

    # particle energies, penetration path length (mg/cm^2) for aircraft (Al),
    # penetration path length in human body
    proton_path_length = [[1, 3, 5, 10, 20, 40, 100, 1000],
                          [3.45 * 10 ** -3,
                           21 * 10 ** -3,
                           50 * 10 ** -3,
                           170 * 10 ** -3,
                           560 * 10 ** -3,
                           1.9,
                           9.8,
                           400],
                          [2.47 * 10 ** -3,
                           14.82 * 10 ** -3,
                           3.42 * 10 ** -2,
                           11.78 * 10 ** -2,
                           5.13 * 10 ** -1,
                           1.33,
                           6.84,
                           281.2,
                           ]]
    electron_path_length = [[0.05, 0.5, 5, 50, 500],
                            [6 * 10 ** -3,
                             1.68 * 10**-1,
                             2.85,
                             12.9,
                             25.8, ],
                            [4.7 * 10 ** -3,
                             0.19,
                             2.6,
                             19,
                             78]]
    particles_path_lengthes = [proton_path_length, electron_path_length]
    protection_depth = 0.5  # g / cm ^ 2
    tissue_depth = 1
    H = 0
    for i, _ in enumerate(flux_density):
        if i == PROTON:
            quality = radiation_quality[i][0] if particle_energy[i] < 5 else \
                        radiation_quality[i][1]
        else:
            quality = radiation_quality[1]
        aircraft_penetration_path = np.interp([particle_energy[i]],
                                              particles_path_lengthes[i][0],
                                              particles_path_lengthes[i][1])[0]
        print "Aircraft penetration: ", aircraft_penetration_path
        body_penetration_path = np.interp([particle_energy[i]],
                                          particles_path_lengthes[i][0],
                                          particles_path_lengthes[i][2])[0]
        print "Body penetration: ", body_penetration_path

        exp = (-protection_depth / aircraft_penetration_path) -\
              (tissue_depth / body_penetration_path)
        print "Under exp: ", exp
        energy = particle_energy[i] * 10 ** 6 * 1.6 * 10 ** -19
        H += quality * flux_density[i] * energy * math.exp(exp)
    H *= 0.2
    return H


def flux_altitude_attenuation(flux, latitude):
    # Taking into account constant altitude ~ 11 km
    koef = 0.0226 * math.abs(latitude)


# Currently hardcoded get of data
# Data is per minute
FILENAME = 'rtsw_plot_data.txt'
def get_data_from_file(filename):
    # Format: [data, time, density, speed]
    with open(filename) as f:
        frame = f.readlines()
        data = []
        # Skip header
        for fr in frame[1:]:
            l = [s for s in fr.split(' ') if s != '']
            d = [int(a) for a in l[0].split('-')]
            d += [int(a) for a in l[1].split(':')[:-1]]  # last is s - skip it
            d = dt.datetime(*d)
            if l[8] != '-99999':
                data.append([d, float(l[8]), float(l[9])])
    return data


def get_density_velocity_by_time(data, datetime):
    for d in data:
        if d[0].time() == datetime.time():
            return d


def main(latitude, longitude, datetime):
    print datetime
    mag = geo2mag(np.array([[latitude, longitude]]).T).T
    inclination = GetInclination(datetime.timetuple().tm_yday)
    rigidity = CalculateRigidity(mag[0, 0], inclination)

    # In MeV/c**2
    #particles = [938.3,  # proton,
    #             0.511]  # electron
    particles = [1.67 * 10 ** -27,  # proton,
                 9.1 * 10 ** -31]  # electron

    data = get_data_from_file(FILENAME)
    if not data:
        "No data for this time"
        return 0
    data = get_density_velocity_by_time(data, datetime)
    print "Data, density, speed: ", data

    particles_rigidities = []
    for p in particles:
        # Counted in GV
        particles_rigidities.append(ParticleRigidity(p, data[2] * 10 ** 3)
                                    / 10 ** 9)

    print "Local rigidity: ", rigidity
    print "Partical rigidities: ", particles_rigidities
    R = 0
    for i, _ in enumerate(particles_rigidities):
        if particles_rigidities[i] > rigidity:
            speed = speed_to_ev(particles[i], data[2] * 10 ** 3) / 10 ** 6
            print "Particle speed: (MeV)", speed
            R += EquivelentRadiation([data[1], ], [speed, ])
    print R


main(75, 100, dt.datetime(2017, 4, 30, 9, 5))


# impulse = 14.8 * (np.cos(lambd_rad) ** 4) / ((1 + under_square ** 0.5) ** 2)
# print impulse
