(ns tradingview.impl.marks)



; https://demo_feed.tradingview.com/marks?symbol=AAPL&from=1488810600&to=1491226200&resolution=D

; { "id":[0,1,2,3,4,5],
;    "time":[1568246400,1567900800,1567641600,1567641600,1566950400,1565654400],
;    "color":["red","blue","green","red","blue","green"],
;    "text":["Today","4 days back","7 days back + Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.","7 days back once again","15 days back","30 days back"],
;    "label":["A","B","CORE","D","EURO","F"],
;    "labelFontColor":["white","white","red","#FFFFFF","white","#000"],
;    "minSize":[14,28,7,40,7,14]
;  }


(comment "
Request: GET /marks?symbol=<ticker_name>&from=<unix_timestamp>&to=<unix_timestamp>&resolution=<resolution>

symbol: symbol name or ticker.
from: unix timestamp (UTC) of leftmost visible bar
to: unix timestamp (UTC) of rightmost visible bar
resolution: string
A response is expected to be an object with some properties listed below. This object is similar to respective response in JS API, but each property is treated as a table column, as described above.

{
    id: [array of ids],
    time: [array of times],
    color: [array of colors],
    text: [array of texts],
    label: [array of labels],
    labelFontColor: [array of label font colors],
    minSize: [array of minSizes],
}

")

(comment "
Request: GET /timescale_marks?symbol=<ticker_name>&from=<unix_timestamp>&to=<unix_timestamp>&resolution=<resolution>

symbol: symbol name or ticker.
from: unix timestamp (UTC) or leftmost visible bar
to: unix timestamp (UTC) or rightmost visible bar
resolution: string
A response is expected to be an array of objects with properties listed below.

id: unique identifier of a mark
color: rgba color
label: a letter to be displayed in a circle
time: unix time
tooltip: tooltip text
")
