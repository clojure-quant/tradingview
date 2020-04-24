(defproject org.pinkgorilla/tradingview "0.0.1-SNAPSHOT"
  :license {:name "MIT"}
  :description "tradingview chart visualization with custom datasource"
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/release_username
                                     :password :env/release_password
                                     :sign-releases false}]]
  :min-java-version "1.11"
  :min-lein-version "2.9.0"

  :plugins [[lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]]

  :release-tasks [["vcs" "assert-committed"]
                  ["bump-version" "release"]
                  ["vcs" "commit" "Release %s"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["bump-version"]
                  ["vcs" "commit" "Begin %s"]
                  ["vcs" "push"]]

  :source-paths ["src/clj"]
  :test-paths ["test"]
  :resource-paths  ["resources"
                    "target/cljsbuild"]

  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [org.clojure/core.async "1.1.582" :exclusions [org.clojure/tools.reader]]
   [org.clojure/tools.logging "0.2.6"]

   ; MongoDB with ssh tunnel
   [com.novemberain/monger "3.5.0" :exclusions [com.google.guava/guava]]
   [clj-commons/clj-ssh "0.5.15"]  ; SSH Tunnel
   
   ; Route handling
   [ring/ring-defaults "0.3.2"]
   ;  [ring/ring-codec "1.1.1"]
   [ring-cors "0.1.12"]                       ; CORS requests
   [compojure "1.6.1"]                        ; Server-Side Routing
   [metosin/compojure-api "1.1.13"]           ; sweet-api
   [cheshire "5.8.0"]                         ; JSON encoding
   [amalloy/ring-gzip-middleware "0.1.4"]     ; gzip compress responses
   [hiccup "1.0.5"]                           ; Templating Server/Side
   
   ; visualization of tradingview saved charts
   [json-html "0.4.7"]
   [clj-time "0.15.2"]] ; joda-time wrapper for clj

  
  :clean-targets ^{:protect false} ["target/classes" 
                                    "target/stale"
                                    ;"resources/static/cljs/"
                                    ]

  :profiles {:uberjar {; UberJar contains code plus resources  
                       ; jar contains only code
                       :aot :all
                       :main ^:skip-aot demo.main
                       :source-paths ["profiles/demo/src/clj"]
                       :resource-paths ["profiles/demo/resources"]
                       :dependencies [; Web Server
                                      [ring "1.7.0"]
                                      [ring/ring-core "1.7.0"]
                                     ;[ring/ring-devel "1.7.0"]
                                      [ring/ring-jetty-adapter "1.7.0"]  ; needs to match compojure version
                                      ]}

             :demo {:source-paths ["profiles/demo/src/clj"]
                    :resource-paths ["profiles/demo/resources"]}

             :cljs {:source-paths ["src/cljs"
                                   "profiles/demo/src/cljs"]
                    :dependencies [[org.clojure/clojurescript "1.10.597"
                                    :scope "provided"
                                    :exclusions [com.google.javascript/closure-compiler-unshaded
                                                 org.clojure/google-closure-library
                                                 org.clojure/google-closure-library-third-party]]
                                   [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
                                   [reagent "0.10.0"
                                    :exclusions [org.clojure/tools.reader
                                                 cljsjs/react
                                                 cljsjs/react-dom]]
                                   [re-frame "0.10.5"]
                                   [thheller/shadow-cljs "2.8.80"]
                                   [thheller/shadow-cljsjs "0.0.21"]]}

             :dev {:dependencies [[clj-kondo "2019.11.23"]]
                   :plugins      [[lein-cljfmt "0.6.6"]]
                   :aliases      {"clj-kondo" ["run" "-m" "clj-kondo.main"]}
                   :cljfmt       {:indents {as->                [[:inner 0]]
                                            with-debug-bindings [[:inner 0]]
                                            merge-meta          [[:inner 0]]
                                            try-if-let          [[:block 1]]}}}}

   ;;; Repl
  ;; Options to change the way the REPL behaves.
  :repl-options {;; Specify the string to print when prompting for input.
                 ;; defaults to something like (fn [ns] (str *ns* "=> "))
                 :prompt (fn [ns] (str "your command for <" ns ">? "))
                 ;; What to print when the repl session starts.
                 :welcome (println "Welcome to the magical world of the repl!")
                 ;; Specify the ns to start the REPL in (overrides :main in
                 ;; this case only)
                 ;:init-ns myrepl
                 ;; This expression will run when first opening a REPL, in the
                 ;; namespace from :init-ns or :main if specified.
                 ;;:init (println "here we are in" *ns*)
                 ;:init (myhelp)
                 ;; Print stack traces on exceptions (highly recommended, but
                 ;; currently overwrites *1, *2, etc).
                 :caught clj-stacktrace.repl/pst+
                 ;; Skip's the default requires and printed help message.
                 :skip-default-init false
                 ;; Customize the socket the repl task listens on and
                 ;; attaches to.
                 :host "0.0.0.0"
                ; :port 14001
                 ;; If nREPL takes too long to load it may timeout,
                 ;; increase this to wait longer before timing out.
                 ;; Defaults to 30000 (30 seconds)
                 :timeout 40000
                 ;; nREPL server customization
                 ;; Only one of #{:nrepl-handler :nrepl-middleware}
                 ;; may be used at a time.
                 ;; Use a different server-side nREPL handler.
                ;;  :nrepl-handler (nrepl.server/default-handler)
                 ;; Add server-side middleware to nREPL stack.
                ;; :nrepl-middleware [my.nrepl.thing/wrap-amazingness
                ;;                    ;; TODO: link to more detailed documentation.
                ;;                    ;; Middleware without appropriate metadata
                ;;                    ;; (see nrepl.middleware/set-descriptor!
                ;;                    ;; for details) will simply be appended to the stack
                ;;                    ;; of middleware (rather than ordered based on its
                ;;                    ;; expectations and requirements).
                ;;                    (fn [handler]
                ;;                      (fn [& args]
                ;;                        (prn :middle args)
                ;;                        (apply handler args)))]
                 }

  :aliases {;"build-shadow-ci" ["run" "-m" "shadow.cljs.devtools.cli" "compile" ":demo"] ; :ci
            "build-shadow-demo"
            ["run" "-m" "shadow.cljs.devtools.cli" "watch" ":demo"]

            "build-cljs"  ^{:doc "Builds Bundle. Gets executed automatically before unit tests."}
            ["with-profile" "+demo,+cljs" "shell" "shadow-cljs" "compile" "demo"]

            "demo"  ^{:doc "Runs UI components via webserver."}
            ["with-profile" "+demo,+dev,+cljs" "run" "-m" "shadow.cljs.devtools.cli" "watch" ":demo"]

            "build-jar"  ^{:doc "Builds java jar file (and compiles frontend bundle)"}
            ["do" ["build-cljs"] "uberjar"]

            "bump-version" ^{:doc "Increases project.clj version number (used by CI)."}
            ["change" "version" "leiningen.release/bump-version"]})
