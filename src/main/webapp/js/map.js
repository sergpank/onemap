var url_href = new URL(window.location.href);

var url_z = url_href.searchParams.get("z");
var url_lat = url_href.searchParams.get("lat");
var url_lon = url_href.searchParams.get("lon");

var default_lat = 47.027;
var default_lon = 28.829;
var default_z = 16;

var z = url_z == null ? default_z : url_z;
var lat = url_lat == null ? default_lat : url_lat;
var lon = url_lon == null ? default_lon : url_lon;

// ?z=17&lat=47.02766051366175&lon=28.844984695315368

var map = L.map('map', {
  zoomSnap: 0.0625,
  center: [ lat, lon ],
  zoom: z
});

L.control.scale().addTo(map);

L.tileLayer('http://localhost:8080/onemap/tile?x={x}&y={y}&z={z}', {
//L.tileLayer('http://onemap.md/tile?x={x}&y={y}&z={z}', {
  minZoom: 13,
  maxZoom: 21,
  tileSize: 512,
  detectRetina: true
}).addTo(map);

map.addEventListener('mouseup', function(ev) {
   var lat = ev.latlng.lat;
   var lng = ev.latlng.lng;
   var zoom = map.getZoom();
   history.replaceState(null, null, "?z=" + zoom + "&lat=" + lat + '&lon=' + lng);
});

// An example how to find out click coordinates
 var popup = L.popup();
 function onMapClick(e) {
   popup
       .setLatLng(e.latlng)
       .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
       .openOn(map);
 }
map.on('click', onMapClick);