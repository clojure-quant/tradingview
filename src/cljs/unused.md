
(def props {
  		:symbol ""; // "Coinbase:BTC/USD"
  		:interval "D"
  		:containerId "tradingview_container"
  		:libraryPath "/charting_library/"
      ;; CHART STORAGE
  		:chartsStorageUrl "https://saveload.tradingview.com"
  		:chartsStorageApiVersion "1.1"
  		:clientId "tradingview.com"
  		:userId "public_user_id"
      ;
  		:fullscreen true
  		:autosize true
  		:studiesOverrides {}
  	})


(def Datafeed
  {:history {}
   :getBars (fn [symbolInfo resolution from to first limit] {})
  })

(def widgetOptions {
    :debug false

    :library_path (:libraryPath props)
    :containerId (:containerId props)
    :locale "en"
    :enabled_features ["study_templates"]
    :disabled_features ["use_localstorage_for_settings"]
    :charts_storage_url (:chartsStorageUrl props)
    :charts_storage_api_version (:chartsStorageApiVersion props)

    ; data sources
    :datafeed (clj->js Datafeed)
    :studies_overrides (:studiesOverrides props)

    ; credentials
    :client_id (:clientId props)
    :user_id (:userId props)

    ; data to show
    :symbol (:symbol props)
    :interval (:interval props)

    ; UI settings
    :fullscreen (:fullscreen props)
    :autosize (:autosize props)
    :overrides {
      ;// "mainSeriesProperties.showCountdown": true,
      #"paneProperties.background" "#131722"
      #"paneProperties.vertGridProperties.color" "#363c4e"
      #"paneProperties.horzGridProperties.color" "#363c4e"
      #"symbolWatermarkProperties.transparency" 90
      #"scalesProperties.textColor"  "#AAA"
      #"mainSeriesProperties.candleStyle.wickUpColor" "#336854"
      #"mainSeriesProperties.candleStyle.wickDownColor" "#7f323f"
    }
  })

;(defn on-mount []
  ;; (js/Date.) is equivalent to new Date().
  ;;  const widget = window.tvWidget = new js/window.TradingView.widget ;
;  (.widget. (.TradingView js/window) (clj->js widgetOptions)))



(defn init-chart-cs [props]
  ; const widget = window.tvWidget = new window.TradingView.widget(widgetOptions);
  ;; You can access properties with the `.-` property access syntax.
  ;; (js/Date.) is equivalent to new Date().
  (let [xxx (.log js/console "TradingView: init-chart-cs")
        jsOptions (clj->js widgetOptions)
        jsWidget (js/window.TradingView.widget. jsOptions)
        xxx (set! (.-tvWidget js/window) jsWidget)
        ; widget.onChartReady(() => {	console.log('Chart has loaded!')});
        xxx (set! (.-onChartReady jsWidget) #(.log js/console "TradingView ChartComponent has loaded!!!"))
        ]
      nil))
