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
            # не блокирующий waitpid (осторожно)
            pid, status = os.waitpid(-1, os.WNOHANG)
            logger.info('Vanish children pid=%d status=%d' % (pid, status))
            if pid == 0: # больше зомбей нет
                break
        except ChildProcessError as e:
            if e.errno == errno.ECHILD:
                # всё, больше потомков нет
                break
            raise


def handle(sock, clinet_ip, client_port):
    # обработчик, работающий в процессе-потомке
    logger.info('Start to process request from %s:%d' % (clinet_ip, client_port))
    in_buffer = b''
    
    while not in_buffer.endswith(b'\n'):
        in_buffer += sock.recv(1024)
    logger.info('In buffer = ' + repr(in_buffer))
    
    #f = open('d:\issoft\LostInOzonData\json_example.txt', 'r')
    #str = f.read()
    received = json.loads(in_buffer)
    latitude = received['path'][0]['point']['latitude']
    longitude = received['path'][0]['point']['longitude']
    

    

    # отправляем
    sock.sendall(response)
    logger.info('Done.')


def serve_forever():
    response = ProcessRequest(0, 0)
    # создаём слушающий сокет
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # re-use port
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(BIND_ADDRESS)
    sock.listen(BACKLOG)
    # слушаем и при получении нового входящего соединения,
    # порождаем потомка, который будет его обрабатывать
    logger.info('Listning no %s:%d...' % BIND_ADDRESS)
    while True:
        try:
            connection, (client_ip, clinet_port) = sock.accept()
        except IOError as e:
            if e.errno == errno.EINTR:
                continue
            raise

        pid = os.fork()
        if pid == 0:
            # этот код выполняется в потомке
            # потомку не нужнен слушающий сокет; сразу закрываем
            sock.close()
            # обрабатывает входщий запрос и формируем ответ
            handle(connection, client_ip, clinet_port)
            # выходим из цикла и завершаем работу потока
            break

        # этот код выполняется в родителе
        # родителю не нужно входщее соединенеие, он его закрывает
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
    # настраиваем логгинг
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
    # подключаем удлятор зомбей
    #signal.signal(signal.SIGCHLD, collect_zombie_children)
    # запускаем сервер
    serve_forever()


main()

