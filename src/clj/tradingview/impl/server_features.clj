(ns tradingview.impl.server-features)

;; CONFIG - Tell TradingView which featurs are supported by server.

(def server-features
  {:supported_resolutions ["D" "M"] ; ["1" "5" "15" "30" "60" "1D" "1W" "1M"]
   :supports_search true ;search and individual symbol resolve logic.
   :supports_group_request false
   :supports_marks false
   :supports_timescale_marks false
   :supports_time true  ; we send our server-time
   :symbols_types [{:value "" :name "All"}
                   {:value "Equity" :name "Equities"}
                   {:value "Corp" :name "Bonds"}
                   {:value "Index" :name "Indices"}
                   {:value "Curncy" :name "Currencies"}
                   {:value "Crypto" :name "Crypto"}]
   :exchanges [{:value "" :name "All Exchanges" :desc ""}
               {:value "US" :name "US (Nasdaq NYSE)" :desc ""}
               {:value "GR" :name "German (Xetra/Regional)" :desc ""}
               {:value "NO" :name "Norway" :desc ""}
               {:value "AV" :name "Austria" :desc ""}
               {:value "LN" :name "London" :desc ""}]})
