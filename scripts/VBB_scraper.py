import codecs
import requests
from lxml import etree

template = """<?xml version="1.0" encoding="utf-8"?>
<ReqC lang="DE" prod="testsystem" ver="1.1" accessId="c94c59f3053673b43dab3c1726b68c81">
    <LocValReq id="023" maxNr = "20000" SMODE = "1">
        <ReqLoc type="ST" match="wibble"/>
    </LocValReq>
</ReqC>"""

URL = 'http://demo.hafas.de/bin/pub/vbb-fahrinfo/relaunch2011/extxml.exe/'

session = requests.Session()

with open('u2_and_u8_stations.txt', 'r') as f:
    station_names = f.readlines()

outfile = codecs.open('u2_and_u8_station_ids.csv', 'w', encoding='utf-8')

for station_name in station_names:
    query = template.replace('wibble', station_name.strip())
    r = session.post(URL, data=query)

    root = etree.fromstring(r.content)
    x = root.xpath('/ResC/LocValRes/Station')

    try:
        for station in x:
            if 'score' not in station.attrib or int(station.attrib['score']) >= 100:
                outfile.write(u'"{0}", {1}\n'.format(station.attrib['name'], station.attrib['externalId']))
                break
    except KeyError as e:
        print(u'Cannot find a score for {0}'.format(station_name))

outfile.close()