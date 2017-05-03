import os
import errno
import signal
import socket
import logging
import time
import json
import ThirdPartyCalculation as calc
import ServerEntity as se

logger = logging.getLogger('main')


BIND_ADDRESS = ('localhost', 8999)
BACKLOG = 5


def collect_zombie_children(signum, frame):
    while True:
        try:
            pid, status = os.waitpid(-1, os.WNOHANG)
            logger.info('Vanish children pid=%d status=%d' % (pid, status))
            if pid == 0: 
                break
        except ChildProcessError as e:
            if e.errno == errno.ECHILD:
                break
            raise


def handle(connection, clinet_ip, client_port):
    logger.info('Start to process request from %s:%d' % (clinet_ip, client_port))
    in_buffer = b''
    
    while not in_buffer.endswith(b'\n'):
        in_buffer += connection.recv(2048)
    logger.info('In buffer = ' + repr(in_buffer))
    receivedString = in_buffer.split('\r\n')
    notParsedJson = receivedString[-1]

    #f = open('d:\issoft\LostInOzonData\json_example.txt', 'r')
    #str = f.read()
    received = json.loads(notParsedJson)
    latitude = received['source']['latitude']
    longitude = received['source']['longitude']
    
    response = 'very huge radiation'

    connection.send(response)
    response = ProcessRequest(0, 0)
    logger.info('Done.')


def serve_forever():
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # re-use port
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(BIND_ADDRESS)
    sock.listen(BACKLOG)
    logger.info('Listning no %s:%d...' % BIND_ADDRESS)
    while True:
        try:
            connection, (client_ip, clinet_port) = sock.accept()
        except IOError as e:
            if e.errno == errno.EINTR:
                continue
            raise
        
        handle(connection, client_ip, clinet_port)
        sock.close()
        
        #pid = os.fork()
        #if pid == 0:
        #    break

        connection.close()

def ProcessRequest(latitude, longitude):
    calc.IsParticlePass(latitude, longitude, 7 * 10 ** 5)
    me = se.Response()
    me.source = se.Location()
    me.source.name = 'Minsk'
    me.source.latitude = 53.9045
    me.source.longitude = 27.5615

    me.destination.name = 'New York'
    me.destination.latitude = 51.5074
    me.destination.longitude = 0.1278

    point1 = se.Point()
    point1.doze = 2.4
    point1.speed = 900
    me.path.append(point1)
    sendedRequest = me.toJSON()
    return sendedRequest

def main():
    logger.setLevel(logging.DEBUG)
    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    formatter = logging.Formatter(
        '%(asctime)s [%(levelname)s] [%(process)s] %(message)s',
        '%H:%M:%S'
    )
    ch.setFormatter(formatter)
    logger.addHandler(ch)
    logger.info('Run')
    #signal.signal(signal.SIGCHLD, collect_zombie_children)
    serve_forever()


main()

