# tradingview
tradingview chart visualization

Tradingview chart component can be used data served from custom servers.

## Requirements
- Java JVM 11 or later
- Leiningen 1.9 or later
- npm
- MongoDB (running on localhost with default port config)

## MongoDB db dump

A mongodb database dump is available for download here:
https://drive.google.com/file/d/1g3gJ1tnHbLKEf88x2g5dKiCmsLnsXPfy/view?usp=sharing

A script to restore the db is in scripts/adminRestoreDB.sh
(you need to remove the rclone part, if you just want to do it once.)


## Start demo

```
npm install
lein demo
```
Webserver runs on port 8087.



## Tradingview Demo Feed

https://demo_feed.tradingview.com
https://saveload.tradingview.com


## Tradingview Library Documentation:

https://github.com/tradingview/charting_library/wiki/UDF (need to register with tradingview)

https://github.com/awb99/charting-library-examples



