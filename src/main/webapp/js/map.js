var map = L.map('map', {
  zoomSnap: 0.25,
  center: [47., 28.865], 
  zoom: 16
});

L.control.scale().addTo(map);

L.tileLayer('http://onemap.md/tile?x={x}&y={y}&z={z}', {
  minZoom: 10,
  maxZoom: 20,
  tileSize: 256,
  detectRetina: true,
}).addTo(map);

// An example how to find out click coordinates
// var popup = L.popup();
// function onMapClick(e) {
//   popup
//       .setLatLng(e.latlng)
//       .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
//       .openOn(map);
// }
//map.on('click', onMapClick);