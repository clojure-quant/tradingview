(ns tradingview.quotes)



(comment "

GET /quotes?symbols=<ticker_name_1>,<ticker_name_2>,...,<ticker_name_n>

Example: GET /quotes?symbols=NYSE%3AAA%2CNYSE%3AF%2CNasdaqNM%3AAAPL

A response is an object with the following keys.

s: Status code for the request. Expected values are: ok or error
errmsg: Error message. Should be present only when s = 'error'
d: symbols data Array
Example:

{
    "s": "ok",
    "d": [
        {
            "s": "ok",
            "n": "NYSE:AA",
            "v": {
                "ch": "+0.16",
                "chp": "0.98",
                "short_name": "AA",
                "exchange": "NYSE",
                "description": "Alcoa Inc. Common",
                "lp": "16.57",
                "ask": "16.58",
                "bid": "16.57",
                "open_price": "16.25",
                "high_price": "16.60",
                "low_price": "16.25",
                "prev_close_price": "16.41",
                "volume": "4029041"
            }
        },
        {
            "s": "ok",
            "n": "NYSE:F",
            "v": {
                "ch": "+0.15",
                "chp": "0.89",
                "short_name": "F",
                "exchange": "NYSE",
                "description": "Ford Motor Company",
                "lp": "17.02",
                "ask": "17.03",
                "bid": "17.02",
                "open_price": "16.74",
                "high_price": "17.08",
                "low_price": "16.74",
                "prev_close_price": "16.87",
                "volume": "7713782"
            }
        }
    ]
}


")
