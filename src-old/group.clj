(ns tradingview.group)



;https://demo_feed.tradingview.com/symbol_info?group=NYSE
;; Do not use UDF with data grouping (see supports_group_request)
;; if your backend has more than a dozen symbols.
(comment "
  GET /symbol_info?group=NYSE request:

  {
     symbol: ["AAPL", "MSFT", "SPX"],
     description: ["Apple Inc", "Microsoft corp", "S&P 500 index"],
     exchange-listed: "NYSE",
     exchange-traded: "NYSE",
     minmovement: 1,
     minmovement2: 0,
     pricescale: [1, 1, 100],
     has-dwm: true,
     has-intraday: true,
     has-no-volume: [false, false, true]
     type: ["stock", "stock", "index"],
     ticker: ["AAPL~0", "MSFT~0", "$SPX500"],
     timezone: “America/New_York”,
     session-regular: “0900-1600”,
  }
")
