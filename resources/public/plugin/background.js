

// configuration

var url_our_server = "http://localhost:8087/tvhack/dump"

// var url_our_server = "https://tradingview.bitblockart.com/tvhack/dump"


function checkRaw (req) {
  if (req.requestBody.raw != null) {
    console.log("we got raw data!")
    //var postedString = decodeURIComponent(String.fromCharCode.apply(null,   new Uint8Array(requestDetails.requestBody.raw[0].bytes)));
    //console.log(postedString);
  }
  var details = req;
  if(details.requestBody && details.requestBody.raw){
                  for(var i = 0; i < details.requestBody.raw.length; ++i){
                      if(details.requestBody.raw[i].file){
                          console.log("FILE");
                          console.log(details.requestBody.raw[i].file);
                      }
                      if(details.requestBody.raw[i].bytes){
                          console.log("RAW");
                          console.log(details.requestBody.raw[i].bytes.byteLength);
                          var dv = new DataView(details.requestBody.raw[i].bytes);
                          var result = "";
                          for(var j = 0; j < dv.byteLength; ++j){
                              result += (String.fromCharCode( dv.getInt8(j) ));
                          }
                          console.log(result);
                      }
                  }
              }

}

var enableRedirect = true;

function safeChart (req) {
  if (req.method != "POST") return;

  if (!enableRedirect) {
     console.log("Not Intercepting reforwarded request.")
     enableRedirect = true;
     return;
  }

  //console.log(JSON.stringify(req));
  console.log("SaveChart " +
              " method: " + req.method +
              " Url:" + req.url +
              " documentUrl: " + req.documentUrl);
  window.myreq = req;

  if (req.requestBody != null) {
      console.log ("body-formData: " + JSON.stringify(req.requestBody.formData));
      window.myform = req.requestBody.formData;
  }

  return {redirectUrl: url_our_server};
}


function reForward(req) {
  console.log("reForwarded: " + req.url);
  enableRedirect = false;
  return { redirectUrl: "https://www.tradingview.com/savechart/"};
}


// this forwards requests to us
browser.webRequest.onBeforeRequest.addListener(safeChart,
  {urls: ["https://www.tradingview.com/savechart/"]},
  ["requestBody", "blocking"]);

// this is the redirect from our server, thart has to go back to tradingview
browser.webRequest.onBeforeRequest.addListener(reForward,
    {urls: ["https://www.tradingview.com/savechart/bongistan"]},
    ["requestBody", "blocking"]);

