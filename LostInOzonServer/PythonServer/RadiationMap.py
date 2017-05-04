from mpl_toolkits.basemap import Basemap
import matplotlib.pyplot as plt
import numpy as np
import LostInOzon as loi

def plot_world_map():
    plt.figure(figsize=(16, 9))
    plt.subplots_adjust(left=0.02, right=0.98, top=0.98, bottom=0.00)
    m = Basemap(projection='ortho',lat_0=0, lon_0=0,resolution='c')
    m.drawparallels(np.arange(-90.,91.,30.), labels=[1,0,0,0])
    m.drawmeridians(np.arange(-180.,181.,60.), labels=[0,0,0,1])  
    #m.fillcontinents(color='white',lake_color='white')
    m.drawcoastlines()

    lat = np.random.randint(-1800, 1800, 100000) / 10
    lon = np.random.randint(-900, 900, 100000) /10
    over = []
    
   
    
    for i in range(len(lon)):
        inCoord = [lat[i], lon[i]]
        magCoord = loi.geo2mag(inCoord)
        over.append(loi.CalculateRigidity(magCoord[0], loi.GetInclination(120)))
    #over = [lon[i] + lat[i] for i in range(len(lon))]

    x, y = m(lon, lat)
    m.scatter(x, y, c = over)
  
    c = plt.colorbar(orientation='horizontal')
    c.set_label("Rigidity cutoff")  
    plt.savefig('d:\world.png',dpi=75)

plot_world_map()