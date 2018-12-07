import cgitb
import cgi
import pymssql
import json
import os
from collections import defaultdict
from urllib.parse import parse_qs
from wsgiref.simple_server import make_server

# Map REST call to handler
g_handler = {}

_hello_resp = '''\
    <html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <head>
    <style></style>
    <title>Hello {name}</title>
    </head>
    <body>
    <h2>Hello {name}!</h2>
    </body>
    </html>'''

_student_resp = '''\
    <html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <head>
    <title>CSC1003 - Student List</title>
    </head>
    <body>
    <h1>{header}</h1>
    {body}
    </body>
    </html>'''


def notfound_404(environ, start_response, response='Not Found'):
    start_response('404 Not Found', [ ('Content-type', 'text/plain') ])
    return [response.encode('utf-8')]

def hello_world(environ, start_response):
    start_response('200 OK', [ ('Content-type','text/plain')])
    params = environ['params']
    name = params.get('name',['Stranger'])
    resp = 'hello {}'.format(name[0])
    return [resp.encode('utf-8')]

def hello_world2(environ, start_response):
    start_response('200 OK', [ ('Content-type','text/html')])
    params = environ['params']
    name = params.get('name',['Stranger'])
    resp = _hello_resp.format(name=name[0])
    yield resp.encode('utf-8')

def getStudentDBData(id=-1):
    conn = pymssql.connect(server='10.20.12.28', user='csc1003', password='csc1003', database='')
    cursor = conn.cursor()
    sqltxt = "Select id, cname, ename, pinyin, sid, shape, school, major from users order by id"
    if id != -1:
        sqltxt += " where id = {}".format(id[0]) 
    cursor.execute(sqltxt)
    return cursor

def getStudentsByHTML(id=-1):
    cursor = getStudentDBData(id)
    ret = ''
    row = cursor.fetchone()
    while row:
        id, cname, ename, pinyin, sid, shape, school, major = row
        ret += '<li><h3>{} {} {} {} {}/{} {}</h3></li>'.format(id,cname,ename,sid,school,major,pinyin)
        row = cursor.fetchone()
    if len(ret) == 0:
        ret = "<div><h3>No record matched!</h3></div>"
    else:
        ret = '<ul>' + ret + '</ul>'
    return ret

def getStudentsByJSON(id=-1):
    cursor = getStudentDBData(id)
    ret = defaultdict(list)
    row = cursor.fetchone()
    while row:
        s = {}
        s['id'], s['cname'], s['ename'], s['pinyin'], s['sid'], s['shape'], s['school'], s['major'] = row
        ret['students'].append(s)
        row = cursor.fetchone()
    cursor.close()
    return json.dumps(ret)

def get_student(environ, start_response):
    start_response('200 OK', [ ('Content-type','text/json')])
    resp = getStudentsByJSON(environ['params'].get('id',-1))
    yield resp.encode('utf-8')

def get_student2(environ, start_response):
    # use application/html will force browser to prompt
    start_response('200 OK', [ ('Content-type','text/html')])
    students = getStudentsByHTML(environ['params'].get('id',-1))
    resp = _student_resp.format(header='CSC1003 Students',body=students)
    yield resp.encode('utf-8')

def get_photo(environ, start_response):
    file = get_file_path(environ)
    if not os.path.isfile(file):
        return notfound_404(environ, start_response, 'Photo not fouund')
    start_response('200 OK', [('Content-Type','application/octet-stream')])
    #start_response('200 OK', [('Content-Type','image/png')])
    return fbuffer(file,10000)

def get_file_path(environ):
    file = ''
    name = environ['params'].get('name','')
    if len(name) == 0:
        return file
    name = name[0]
    for ext in ['png', 'jpg']:
        file = '{}/students/{}.{}'.format(os.getcwd(),name,ext)
        if os.path.isfile(file):
            break
    return file

def fbuffer(file, chunk_size):
    f = open(file, 'rb')
    while True:
        chunk = f.read(chunk_size)      
        if not chunk: break
        yield chunk

## ONLY HTTP GET 
def http_handler(environ, start_response):
    path = environ['PATH_INFO']
    method = environ['REQUEST_METHOD'].lower()
    if method == 'get':
        environ['params'] = parse_qs(environ['QUERY_STRING'])
    handler = g_handler.get((method,path), notfound_404)
    return handler(environ, start_response)

if __name__ == '__main__':
    # cgitb.enable(display=1, logdir="/log")
    g_handler[('get', '/hello')] = hello_world
    g_handler[('get', '/hello2')] = hello_world2
    g_handler[('get', '/student')] = get_student
    g_handler[('get', '/student2')] = get_student2
    g_handler[('get', '/photo')] = get_photo

    # Launch a basic server
    port = 8080
    httpd = make_server('', port, http_handler)
    print('Serving on port {}...'.format(port))
    httpd.serve_forever()