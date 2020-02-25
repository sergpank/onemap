var url_href = new URL(window.location.href);

var url_z = url_href.searchParams.get("z");
var url_lat = url_href.searchParams.get("lat");
var url_lon = url_href.searchParams.get("lon");

var default_lat = 47.0247;
var default_lon = 28.8326;
var default_z = 17;

var z = url_z == null ? default_z : url_z;
var lat = url_lat == null ? default_lat : url_lat;
var lon = url_lon == null ? default_lon : url_lon;

// Holy Gates
// ?z=17&lat=47.024715988406776&lon=28.832663297653202

var map = L.map('map',
{
  zoomSnap: 0.125,
  center: [ lat, lon ],
  zoom: z
});

L.control.scale().addTo(map);

L.tileLayer('tile?x={x}&y={y}&z={z}',
{
  minZoom: 11,
  maxZoom: 21,
  tileSize: 256,
  detectRetina: true,
  attribution: "(c) OpenStreetMap contributor (OSM is awesome)"
}).addTo(map);

map.addEventListener('mouseup',
function(ev)
{
  var lat = ev.latlng.lat;
  var lng = ev.latlng.lng;
  var zoom = map.getZoom();
  history.replaceState(null, null, "?z=" + zoom + "&lat=" + lat + '&lon=' + lng);
});

// An example how to find out click coordinates
var popup = L.popup();
function onMapClick(e)
{
  popup
    .setLatLng(e.latlng)
    .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
    .openOn(map);
}
map.on('click', onMapClick);

// An example of GeoJSON layer
// var feature1 = {"type" : "FeatureCollection", "features" : [{"type": "Feature", "geometry": {"type":"LineString","coordinates":[[28.8316747,47.0279759],[28.8317586,47.0280343],[28.8324489,47.0284839],[28.8327796,47.0286992],[28.8328498,47.0287449]]}, "properties": {"id": 40123326, "type": "highway", "name": "strada tricolorului", "name_ru": "улица триколора", "name_old": "улица жуковского"}}]};
const f1 = {"type" : "FeatureCollection", "features" : [{"type": "Feature", "geometry": {"type":"LineString","coordinates":[[28.8698675,46.9980298],[28.8706374,46.9972598],[28.8713129,46.9965663]]}, "properties": {"id": 26184655, "type": "highway", "name": "stradela hanul morii", "name_ru": "переулок ханул морий", "name_old": "батумский переулок"}}]};
const f2 = {"type" : "FeatureCollection", "features" : [{"type": "Feature", "geometry": {"type":"LineString","coordinates":[[28.8706374,46.9972598],[28.8714057,46.9975957]]}, "properties": {"id": 26184648, "type": "highway", "name": "stradela hanul morii", "name_ru": "переулок ханул морий", "name_old": "батумский переулок"}}]};
const f12 = {"type" : "FeatureCollection", "features" : [{"type" : "Feature", "bbox" : {"type":"Polygon","coordinates":[[[28.8698675,46.9965663],[28.8698675,46.9980298],[28.8713129,46.9980298],[28.8713129,46.9965663],[28.8698675,46.9965663]]]}, "geometry" : {"type":"LineString","coordinates":[[28.8698675,46.9980298],[28.8706374,46.9972598],[28.8713129,46.9965663]]}, "properties" : {"id" : 26184655, "name" : "stradela hanul morii", "name_ru" : "переулок ханул морий", "name_old" : "батумский переулок"}}, {"type" : "Feature", "bbox" : {"type":"Polygon","coordinates":[[[28.8706374,46.9972598],[28.8706374,46.9975957],[28.8714057,46.9975957],[28.8714057,46.9972598],[28.8706374,46.9972598]]]}, "geometry" : {"type":"LineString","coordinates":[[28.8706374,46.9972598],[28.8714057,46.9975957]]}, "properties" : {"id" : 26184648, "name" : "stradela hanul morii", "name_ru" : "переулок ханул морий", "name_old" : "батумский переулок"}}]};

// var myLayer = L.geoJSON().addTo(map);
// myLayer.addData([f1, f2]);
// myLayer.addData(f12);
