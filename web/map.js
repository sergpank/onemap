var map = L.map('map', {
  zoomSnap: 0.25,
  center: [47., 28.865], 
  zoom: 16
});

// var map = L.map('map', {
//     center: [51.505, -0.09],
//     zoom: 13
// });

L.control.scale().addTo(map);

// L.tileLayer('../tiles/kishinev/{z}/tile_{z}_{y}_{x}.png', {
//   minZoom: 10,
//   maxZoom: 20,
//   tileSize: 256,
//   detectRetina: true
// }).addTo(map);

L.tileLayer('http://localhost:8080/onemap/tile?x={x}&y={y}&z={z}', {
  minZoom: 10,
  maxZoom: 20,
  tileSize: 512,
  detectRetina: true,
}).addTo(map);

// var popup = L.popup();
// function onMapClick(e) {
//   popup
//       .setLatLng(e.latlng)
//       .setContent("Lat = " + e.latlng.lat + "; Lon = " + e.latlng.lng)
//       .openOn(map);
// }

//map.on('click', onMapClick);