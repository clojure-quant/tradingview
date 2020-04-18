
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

  //  "http://localhost:5005/dump"
  // "https://quant.hoertlehner.com/dump"
  return {redirectUrl: "https://quant.hoertlehner.com/dumptv"};
  //return redirect(requestDetails);
}


function reForward(req) {
  console.log("reForwarded: " + req.url);
  enableRedirect = false;
  return { redirectUrl: "https://www.tradingview.com/savechart/"};

}


browser.webRequest.onBeforeRequest.addListener(safeChart,
  {urls: ["https://www.tradingview.com/savechart/"]},
  ["requestBody", "blocking"]);

browser.webRequest.onBeforeRequest.addListener(reForward,
    {urls: ["https://www.tradingview.com/savechart/bongistan"]},
    ["requestBody", "blocking"]);





// https://developer.mozilla.org/en-US/docs/Mozilla/Add-ons/WebExtensions/Temporary_Installation_in_Firefox
