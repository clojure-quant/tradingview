(ns tradingview.impl.quotes)

(comment "

GET /quotes?symbols=<ticker_name_1>,<ticker_name_2>,...,<ticker_name_n>

Example: GET /quotes?symbols=NYSE%3AAA%2CNYSE%3AF%2CNasdaqNM%3AAAPL

A response is an object with the following keys.

s: Status code for the request. Expected values are: ok or error
errmsg: Error message. Should be present only when s = 'error'
d: symbols data Array
Example:
")
