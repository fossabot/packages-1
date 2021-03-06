(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[cljsjs/boot-cljsjs "0.10.4" :scope "test"]
                 [cljsjs/react "16.9.0-0"]
                 [cljsjs/react-dom "16.9.0-0"]
                 [cljsjs/moment "2.24.0-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "3.24.3")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 push {:ensure-clean false}
 pom {:project     'cljsjs/antd
      :version     +version+
      :description "One design language http://ant.design"
      :url         "https://github.com/ant-design/ant-design"
      :license     {"MIT" "https://raw.githubusercontent.com/ant-design/ant-design/master/LICENSE"}
      :scm         {:url "https://github.com/cljsjs/packages"}})

(deftask package []
 (comp
   (download
     :url (str "https://unpkg.com/antd@" +lib-version+ "/dist/antd-with-locales.js")
     :target "cljsjs/antd/development/antd.inc.js")
   (download
     :url (str "https://unpkg.com/antd@" +lib-version+ "/dist/antd.css")
     :target "cljsjs/antd/development/antd.inc.css")
   (download
     :url (str "https://unpkg.com/antd@" +lib-version+ "/dist/antd-with-locales.min.js")
     :target "cljsjs/antd/production/antd.min.inc.js")
   (download
     :url (str "https://unpkg.com/antd@" +lib-version+ "/dist/antd.min.css")
     :target "cljsjs/antd/production/antd.min.inc.css")

   (replace-content
      :in "cljsjs/antd/development/antd.inc.js"
      :match #"\/\/\# sourceMappingURL=performance-now\.js\.map"
      :value "")
   (replace-content
      :in "cljsjs/antd/development/antd.inc.js"
      :match #"\/\/\# sourceMappingURL=antd-with-locales\.js\.map$"
      :value "")
   (replace-content
      :in "cljsjs/antd/production/antd.min.inc.js"
      :match #"\/\/\# sourceMappingURL=antd-with-locales\.min\.js\.map$"
      :value "")

   (sift :include #{#"cljsjs"})
   (deps-cljs
    :provides ["antd" "cljsjs.antd"]
    :requires ["react" "react-dom" "moment"]
    :global-exports '{antd antd})
   (pom)
   (jar)
   (validate)))
