function searchStreets(ev)
{
  var xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function()
  {
    if (this.readyState == 4 && this.status == 200)
    {
      var response = JSON.parse(this.responseText);
      var responseText = "";

      responseText += "<table>";
      responseText += "<tr> <td>ID</td> <td>NAME</td> <td>NAME_RU</td> <td>NAME_OLD</td> </tr>"

      for (var i = 0; i < response.length; i++)
      {
        var element = response[i];
        var id = element.id;
        var name = element.name;
        var nameRu = element.nameRu == undefined ? "N/A" : element.nameRu;
        var nameOld = element.nameOld == undefined ? "N/A" : element.nameOld;

        responseText += "<tr> <td>" + id + "</td> <td>" + name + "</td> <td>" + nameRu + "</td> <td>" + nameOld + "</td> </tr>"
      }

      responseText += "</table>";

      document.getElementById("demo").innerHTML = responseText;
      ev.stopPropagation();
    }
  };

  var key = document.getElementById("key").value;

  xhttp.open("GET", "http://localhost:8080/onemap/search?key=" + key, true);

  xhttp.send();
};

// a very basic example of xml http request
function loadDoc()
{
  var xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function()
  {
    if (this.readyState == 4 && this.status == 200)
    {
      // key for form request - just for test
      var key = document.getElementsByName("key")[0].value;
      document.getElementById("demo").innerHTML = this.responseText + "[" + key + "]";
    }
  };

  xhttp.open("GET", "ajax_info.txt", true);

  xhttp.send();
};
