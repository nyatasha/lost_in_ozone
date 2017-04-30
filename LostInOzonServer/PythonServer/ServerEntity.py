import json

class Location:
    name = ''
    latitude = float()
    longitude = float()

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, 
            sort_keys=True, indent=4)

class Point:
    latitude = float()
    longitude = float()
    doze = float()
    height = float()
    speed = float()

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, 
            sort_keys=True, indent=4)

class Response:
    path = []
    source = Location()
    destination = Location()
    

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, 
            sort_keys=True, indent=4)