# -*- coding: utf-8 -*-
"""
Created on Fri May  5 15:04:51 2017

@author: User
"""

import math
from math import sin, cos, sqrt, atan2, radians, degrees
 
#pi - число pi, rad - радиус сферы (Земли)
rad = 6372795

def dist(llat1, llong1, llat2, llong2):
 
  #в радианах
  lat1 = llat1*math.pi/180.
  lat2 = llat2*math.pi/180.
  long1 = llong1*math.pi/180.
  long2 = llong2*math.pi/180.
 
  #косинусы и синусы широт и разницы долгот
  cl1 = math.cos(lat1)
  cl2 = math.cos(lat2)
  sl1 = math.sin(lat1)
  sl2 = math.sin(lat2)
  delta = long2 - long1
  cdelta = math.cos(delta)
  sdelta = math.sin(delta)
 
  #вычисления длины большого круга
  y = math.sqrt(math.pow(cl2*sdelta,2)+math.pow(cl1*sl2-sl1*cl2*cdelta,2))
  x = sl1*sl2+cl1*cl2*cdelta
  ad = math.atan2(y,x)
  dist = ad*rad
 
  #вычисление начального азимута
  x = (cl1*sl2) - (sl1*cl2*cdelta)
  y = sdelta*cl2
  z = math.degrees(math.atan(-y/x))
 
  if (x < 0):
    z = z+180.
 
  z2 = (z+180.) % 360. - 180.
  z2 = - math.radians(z2)
  anglerad2 = z2 - ((2*math.pi)*math.floor((z2/(2*math.pi))) )
  angledeg = (anglerad2*180.)/math.pi
 
  print('Distance >> %.0f' % dist, ' [meters]')
  print('Initial bearing >> ', angledeg, '[degrees]')
  return [dist, angledeg]


def equal_distance(lon1, lat1, lon2, lat2, numb):
  r = 1.0
  
  if lat1 < 0:
      lat1 = 360 + lat1
      
  if lat2 < 0:
      lat2 = 360 + lat2
  
  lon1r, lat1r, lon2r, lat2r = [radians(foo) for foo in [lon1, lat1, lon2, lat2]]
  
  coord1 = [lat1r, lon1r, r]
  coord2 = [lat2r, lon2r, r]
  
  # convert to rectangular coordinates
  x1 = coord1[2]*cos(coord1[0])*cos(coord1[1])
  y1 = coord1[2]*cos(coord1[0])*sin(coord1[1])
  z1 = coord1[2]*sin(coord1[0])
  xyz1 = [x1, y1, z1]
  
  x2 = coord2[2]*cos(coord2[0])*cos(coord2[1])
  y2 = coord2[2]*cos(coord2[0])*sin(coord2[1])
  z2 = coord2[2]*sin(coord2[0])
  xyz2 = [x2, y2, z2]
  
  dx = (xyz2[0]-xyz1[0]) / numb
  dy = (xyz2[1]-xyz1[1]) / numb  
  dz = (xyz2[2]-xyz1[2]) / numb
  
  nx = []
  ny = []
  nz = []
  lonOut = []
  latOut = []
  
  for i in range(numb + 1):
    nx.append(xyz1[0] + i*dx)
    ny.append(xyz1[1] + i*dy)
    nz.append(xyz1[2] + i*dz)
    LonC = degrees(atan2(ny[i], nx[i]))
    Hyp = sqrt(pow(nx[i], 2) + pow(ny[i], 2))
    LatC = degrees(atan2(nz[i], Hyp))
    print("Latitude ", i , " : ", LatC)
    print("Longitude ", i , " : ", LonC)  
    lonOut.append(LonC)
    latOut.append(LatC)
  
  return latOut, lonOut
  