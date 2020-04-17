
var MyTradingView = {
   datafeed: null,
	 widget: null,
   initChart : null,
   setSymbol : null
};


function initChart (id, feedURL, storageURL, symbol) {
    console.log("MyTradingView.initChart feedUrl:" + feedURL + " storageUrl: " + storageURL);

		var udf_datafeed = new Datafeeds.UDFCompatibleDatafeed(feedURL);
    MyTradingView.datafeed = udf_datafeed;

		var widgetOptions = {
			debug: true, // false,
			symbol: symbol,
			datafeed: MyTradingView.datafeed,
			interval: "D",
			container_id: "", // ID of the div
			library_path: "/charting_library/",
			locale: "en", // getLanguageFromURL() || 'en',
			disabled_features: [], // 'use_localstorage_for_settings'],
			enabled_features: ['study_templates' ],

			charts_storage_url: storageURL, // "https://saveload.tradingview.com",
			charts_storage_api_version: "1.1",
			client_id: 1, // "tradingview.com",
			user_id: 1, // "public_user_id",

			//width: 200,
			//height: 200,
			fullscreen:false, // all window
			autosize: true, // all space in container

			studies_overrides: {},
			//overrides: {
				// "mainSeriesProperties.showCountdown": true,
			//	"paneProperties.background": "#131722",
			//	"paneProperties.vertGridProperties.color": "#363c4e",
		//		"paneProperties.horzGridProperties.color": "#363c4e",
		//		"symbolWatermarkProperties.transparency": 90,
		//		"scalesProperties.textColor" : "#AAA",
		//		"mainSeriesProperties.candleStyle.wickUpColor": '#336854',
		//		"mainSeriesProperties.candleStyle.wickDownColor": '#7f323f'
		//	}
		};
		widgetOptions.container_id = id;
    var widget = new window.TradingView.widget(widgetOptions); // = window.tvWidget
    widget.onChartReady(() => {
		 		console.log('TradingView ChartWidget has loaded!')
		});
		MyTradingView.widget = widget;
}

function setSymbol(symbol, interval) {
  console.log("TradingView.setSymbol: " + symbol);
  if (MyTradingView.widget != null) {
	   MyTradingView.widget.setSymbol (symbol, interval, function () {
		    console.log("TradingView symbol successfully changed to: " + symbol);
	   });
  }
	else {
	   console.log("TradingView.setSymbol " + symbol +"ERROR: MyTradingView.widget is undefined." )
	}
}

function removeChart () {
  if (MyTradingView.widget != null) {
	   MyTradingView.widget.remove ();
     MyTradingView = null;
   }
}




MyTradingView.initChart = initChart;
MyTradingView.setSymbol = setSymbol;
MyTradingView.removeChart = removeChart;

window.MyTradingView = MyTradingView;
