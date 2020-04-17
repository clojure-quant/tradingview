(ns tradingview.impl.symbol
  (:require
   [clojure.string :as str]
   [clojure.set]
   [clj-time.core :as t]
   [clj-time.coerce :as c]
   [monger.collection :as mc]
   [monger.joda-time]))




(defn capitalize-category [data]
  (update data :category clojure.string/capitalize))


(defn remove-empty-exchange [data]
  (if (clojure.string/blank? (:exchange data))
    (dissoc data :exchange)
    data))

(defn array-to-map [array]
  (if-not (nil? array)
    (->> array
         (drop 1)
         (zipmap [:symbol-only :exchange :category])
         (capitalize-category)
         (remove-empty-exchange))))

(defn categorize-symbol [symbol]
  (->> symbol
       (re-matches #"([\w-\\*&//]+)\s*([\w]*)\s+(\w+$)")   ;  "(.*)\s(\w+)\s*"
       (array-to-map)))

;; SYMBOL CONVERSION [Tradingview does not support space in symbol]

; TradingView symbology EXCHANGE:SYMBOL format.
; ticker field: unique identifier of the symbol
;               that is used only inside the Library.
;               users will never be able to see it.

(defn space-to-doublepoint [str]
  (if (nil? str) str
      (clojure.string/replace str " " ":")))

(defn doublepoint-to-space [str]
  (if (nil? str) nil (clojure.string/replace str ":" " ")))


(defn space-to-underscore [str]
  (if (nil? str) str
      (clojure.string/replace str " " "_")))

(defn underscore-to-space [str]
  (if (nil? str) nil (clojure.string/replace str "_" " ")))


(defn nil-default [str default-value]
  (if (nil? str) default-value str))

(defn field-nil-default [row field default-value]
  (update row field #(nil-default % default-value)))

(defn to-epoch-no-ms- [date]
  (int (/ (c/to-long date) 1000)))


(defn tradingview-symbol-info
  "Converts instrument [from db] to tradingview symbol-information
   Used in symbol and search"
  [instrument]
  (let [c (categorize-symbol (:symbol instrument)) ;  {:symbol-only "AIF-U", :exchange "CN", :category "Equity"}
        exchange (nil-default (:exchange c) "AV")
        ticker (:symbol instrument); (space-to-underscore (:symbol instrument))
        ; display (:symbol-only c)
        display (space-to-underscore (:symbol instrument))
        tradingview  (str exchange ":" (space-to-underscore (:symbol instrument)))
        ;symbol-tradingview
        ]
    {:ticker ticker  ; OUR SYMBOL FORMAT
     :display display
     :tradingview  tradingview

     :exchange exchange
     :description (nil-default (:name instrument) "<Unknown Name>")
     :type (nil-default (:category instrument) "Index") ; Equity / Index /Curncy
     }))


(defn has-no-volume
  [instrument]
  (let [category (:category instrument)]
    (case category
      "Equity" false  ; equities DO HAVE VOLUME.
      "Curncy" true   ; no-volume for currencies
      "Corp" true   ; no-volume for bonds
      "Index" false
      true)))


(def unknown-symbol-response
  {:s "error"
   :errmsg "unknown_symbol"})


(defn duplicate-exchange [item]
  (assoc item :exchange-traded (:exchange-listed item)))


(defn tradingview-symbol-response [instrument]
  (if (nil? instrument)
    unknown-symbol-response
    (-> (tradingview-symbol-info instrument)
        (clojure.set/rename-keys {:display :name})  ;
        (dissoc :tradingview)

        (clojure.set/rename-keys {:exchange :exchange-listed})
        (duplicate-exchange)
        (assoc
              ;:s "ok"
         :has_no_volume (has-no-volume instrument)
              ; :sector
              ; industry
         :supported_resolutions ["D" "M"] ; ,"2D","3D","W","3W","M","6M"],
              ;:has_daily true ;    data feed has its own daily resolution bars or not. If has_daily = false then Charting Library will build the respective resolutions using 1-minute bars by itself. If not, then it will request those bars from the data feed.
              ;:has_weekly_and_monthly true
              ;:has-dw true  ; DAY-WEEK
         :has_intraday false ;If it's false then all buttons for intraday resolutions will be disabled for this particular symbol.
              ;:has_seconds false ; symbol includes seconds in the historical data.
              ;:data_status "streaming" ; "endofday"  ; streaming pulsed delayed_streaming

              ; FORMATTING OF DIGITS
         :minmov 1  ; is the amount of price precision steps for 1 tick. For example, since the tick size for U.S. equities is 0.01, minmov is 1. But the price of the E-mini S&P futures contract moves upward or downward by 0.25 increments, so the minmov is 25.
         :pricescale 100 ;  If a price is displayed as 1.01, pricescale is 100; If it is displayed as 1.005, pricescale is 1000.
         :minmov2 0  ;  for common prices is 0 or it can be skipped.
         :fractional 0  ; for common prices is false or it can be skipped.   ; Fractional prices are displayed 2 different forms: 1) xx'yy (for example, 133'21) 2) xx'yy'zz (for example, 133'21'5).
         :volume_precision 0 ;Integer showing typical volume value decimal places for a particular symbol. 0 means volume is always an integer. 1 means that there might be 1 numeric character after the comma.
         :pointvalue 1

         :timezone "America/New_York"
         :session "0900-1600"  ;"0900-1630|0900-1400:2",
              ;:session-regular "0900-1600"

         :expired true ; whether this symbol is an expired futures contract or not.
         :expiration_date  (to-epoch-no-ms- (-> 1 t/hours t/ago))))))


(defn load-symbol
  ([db symbol]
   (mc/find-one-as-map db "instruments" {:symbol symbol}))
  ([db symbol fields]
   (mc/find-one-as-map db "instruments" {:symbol symbol} fields)))


;https://demo_feed.tradingview.com/symbols?symbol=BP
(defn tradingview-symbol-info-request
  "gets information about one instrument
   important data for chart:
     - number of digits: minmov + pricescale
     - volume / no volume
    "
  [db tradingview-symbol]
  (-> tradingview-symbol
      (str/split #":") ; LN:BP => BP      BP => BP
      (last) ; Ignore Exchange
      (underscore-to-space)
      (#(load-symbol db % [:symbol :symbol-only :exchange :category :name]))
      (tradingview-symbol-response)))

(comment

  (symbols "DAX Index")
  (symbols "BP/ LN Equity")
  (symbols "EURUSD Curncy")

  (tradingview-symbol-info-request "DAX:Index"))
