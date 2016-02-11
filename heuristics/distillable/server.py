#!/usr/bin/env python
# Copyright 2016 The Chromium Authors. All rights reserved.
# Use of this source code is governed by a BSD-style license that can be
# found in the LICENSE file.

import argparse
import collections
import cherrypy
import json
import os
import sys
import time

genDelta = [
  0,
  10,
  100,
  1000,
]

genCount = 4
maxGenLength = 5

def cleanGeneration(gen, delta):
  for i in range(len(gen) - 1):
    if gen[i + 1]['time'] - gen[i]['time'] < delta:
      e = gen.pop(i + 1)
      os.remove(e['path'])
      return None
  return gen.pop(0)


def cleanUpArchives(archives):
  for i, gen in enumerate(archives):
    if len(gen) > maxGenLength:
      e = cleanGeneration(gen, genDelta[i])
      if e:
        if i + 1 < len(archives):
          archives[i + 1].append(e)

def printArchives(archives):
  print '******'
  for i in archives:
    for e in i:
      print e['time']
    print '  --  '

archiveIdx = 0
def saveData(service):
  global archiveIdx
  archives = service.getArchives()
  lastGeneration = archives[0]
  lastSavedUpdate = -1
  if len(lastGeneration) > 0:
    lastSavedUpdate = lastGeneration[-1]['lastId']
  lastUpdate = service.getLastUpdateId()
  hasChanges = lastSavedUpdate < lastUpdate

  if not hasChanges:
    return

  timeFormat = '%Y-%m-%e-%H:%M:%S'
  currentTime = time.time()
  path = os.path.join(service.getArchivePath(), 'archive-%s.json' % time.strftime(timeFormat))
  with open(path, 'w') as outfile:
    json.dump(service.getData(), outfile, indent=2)
    print 'saved to %s' % (path)
  lastGeneration.append({
    'lastId': lastUpdate,
    'path': path,
    'time': archiveIdx,
    'realtime': currentTime,
  })
  archiveIdx += 1

  cleanUpArchives(service.getArchives())

class Service(object):
  exposed = True

  def __init__(self, data_dir):
    self.data = None
    self.updates = collections.deque(maxlen=500)
    self.nextUpdateId = 0
    self.idxMap = None
    self.archive_path = os.path.join(data_dir, 'archive')
    if not os.path.exists(self.archive_path):
      os.makedirs(self.archive_path)
    with open(os.path.join(data_dir, 'index')) as inf:
      self.data = json.load(inf)
      self.initIdxMap()

    archives = [os.path.join(self.archive_path, f) for f in os.listdir(self.archive_path)]
    archives = [f for f in archives if os.path.isfile(f)]
    for archive in archives:
      with open(archive) as inf:
        last = json.load(inf)
        for i in last:
          url = i['url']
          if url in self.idxMap and 'good' in i:
            assert self.data[self.idxMap[url]]['index'] == i['index']
            self.data[self.idxMap[url]]['good'] = i['good']
            print "%d good = %s" % (self.idxMap[url], i['good'])

    self.archives = []
    for i in range(genCount):
      self.archives.append([])

    self.saver = cherrypy.process.plugins.BackgroundTask(1.0 * 60, saveData, [self])
    self.saver.start()

  def initIdxMap(self):
    self.idxMap = dict()
    for i, entry in enumerate(self.data):
      self.idxMap[entry['url']] = i

  def getArchivePath(self):
    return self.archive_path

  def getArchives(self):
    return self.archives

  def getData(self):
    return self.data

  def getDataResponse(self):
    return {
      'data': self.data,
      'nextId': self.nextUpdateId,
    }

  def appendUpdate(self, entry, idx):
    self.updates.append({'index': idx, 'id': self.nextUpdateId, 'entry': entry})
    self.nextUpdateId += 1

  def update(self, entry):
    key = entry['url']
    idx = self.idxMap[key]
    self.data[idx] = entry
    self.appendUpdate(entry, idx)
    return 'sdf'

  @cherrypy.expose
  def getupdates(self, nextId):
    data = None
    updates = None
    nextId = int(nextId)
    newNextId = nextId

    if len(self.updates) > 0:
      lastId = self.updates[-1]['id']
      firstId = self.updates[0]['id']
      if firstId > nextId:
        data = self.data
      elif lastId >= nextId:
        updates = list(self.updates)[nextId - firstId:]
      newNextId = lastId + 1
    return json.dumps({'response': {
      'data': data,
      'updates': updates,
      'nextId': newNextId,
    }})


  @cherrypy.expose
  def message(self):
    cl = cherrypy.request.headers['Content-Length']
    rawbody = cherrypy.request.body.read(int(cl))
    request = json.loads(rawbody)
    action = request['action']
    response = None
    if action == 'getData':
      response = self.getDataResponse()
    if action == 'update':
      response = self.update(request['data'])
    return json.dumps({'response': response})

  def getLastUpdateId(self):
    return self.nextUpdateId - 1



if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--data-dir')
  options = parser.parse_args()
  service = Service(options.data_dir)
  conf = {
    'global': {
      'server.socket_host': '0.0.0.0',
      'server.socket_port': 8081,
    },
    '/': {
      'tools.response_headers.on': True,
      'tools.response_headers.headers': [('Content-Type', 'text/plain')],
      'tools.staticdir.on': True,
      'tools.staticdir.dir': os.getcwd(),
      'tools.staticdir.index': 'index.html',
    },
    '/images': {
      'tools.staticdir.on': True,
      'tools.staticdir.dir': os.path.join(os.getcwd(), options.data_dir),
      'tools.expires.on': True,
      'tools.expires.secs': 60,
    }
  }
  cherrypy.quickstart(service, '', conf)
