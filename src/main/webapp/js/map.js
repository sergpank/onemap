var map = L.map('map', {
  zoomSnap: 0.0625,
  center: [47.022, 28.835],
  zoom: 17
});

L.control.scale().addTo(map);

//L.tileLayer('http://localhost:8080/onemap/tile?x={x}&y={y}&z={z}', {
L.tileLayer('http://onemap.md/tile?x={x}&y={y}&z={z}', {
  minZoom: 13,
  maxZoom: 21,
  tileSize: 512,
  detectRetina: true
}).addTo(map);

// An example how to find out click coordinates
 var popup = L.popup();
 function onMapClick(e) {
   popup
       .setLatLng(e.latlng)
       .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
       .openOn(map);
 }
map.on('click', onMapClick);