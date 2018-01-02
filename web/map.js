var map = L.map('map').setView([47.0, 28.865], 16);
// http://a.tiles.mapbox.com/v3/examples.map-i875mjb7/0/0/0.png
// L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {

L.tileLayer('../tiles/botanica.osm/{z}/tile_{z}_{y}_{x}.png', {
  minZoom: 10,
  maxZoom: 20
}).addTo(map);

//L.tileLayer('http://localhost:8080/aaa/random?x={x}&y={y}&z={z}', {
//  minZoom: 10,
//  maxZoom: 20
//}).addTo(map);

var popup = L.popup();

function onMapClick(e) {
  popup
      .setLatLng(e.latlng)
      .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
      .openOn(map);
}

map.on('click', onMapClick);