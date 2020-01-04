function searchStreets(ev)
{
  var xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function()
  {
    if (this.readyState == 4 && this.status == 200)
    {
      var response = JSON.parse(this.responseText);

      var htmlResponse = "<table>";
      htmlResponse += "<tr> <td>ID</td> <td>NAME</td> <td>NAME_RU</td> <td>NAME_OLD</td> </tr>"

      for (var i = 0; i < response.length; i++)
      {
        var element = response[i];
        var id = element.id;
        var name = element.name;
        var nameRu = element.nameRu == undefined ? "N/A" : element.nameRu;
        var nameOld = element.nameOld == undefined ? "N/A" : element.nameOld;

        htmlResponse += "<tr> <td>" + id + "</td> <td>" + name + "</td> <td>" + nameRu + "</td> <td>" + nameOld + "</td> </tr>"
      }
      htmlResponse += "</table>";
      document.getElementById("demo").innerHTML = htmlResponse;
    }
  };

  var key = document.getElementById("key").value;

  xhttp.open("GET", "http://onemap.md/search?key=" + key, true);
//  xhttp.open("GET", "http://localhost:8080/onemap/search?key=" + key, true);
  xhttp.send();
};

// a very basic example of xml http request
function loadDoc()
{
  var xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function()
  {
    if (this.readyState == 4 && this.status == 200)
      document.getElementById("demo").innerHTML = this.responseText ;
  };

  xhttp.open("GET", "ajax_info.txt", true);
  xhttp.send();
};
