(ns avro-registry.validator
  (:require [clojure.tools.logging :as log]
            [avro-registry.env :as e])
  (:import (org.apache.avro SchemaValidator SchemaValidatorBuilder Schema Schema$Parser)))

(def validators {:no-op    (proxy [SchemaValidator] []
                             (validate [_ _]
                               (log/infof "Nothing to do in no-op validator")))
                 :backward (->
                             (SchemaValidatorBuilder.)
                             (.canReadStrategy)
                             (.validateLatest))

                 :forward  (->
                             (SchemaValidatorBuilder.)
                             (.canBeReadStrategy)
                             (.validateLatest))

                 :full     (->
                             (SchemaValidatorBuilder.)
                             (.mutualReadStrategy)
                             (.validateLatest))
                 }

  )


(defn validate!! [^Schema new-schema ^Schema old-schema]
  (let [^SchemaValidator validator ((:validator e/props) validators)
        old-schema-as-list (doto (java.util.ArrayList.) (.add old-schema))]
    (.validate validator new-schema old-schema-as-list)
    )
  )

(defn parse [^String json-string]
  (let [parser (Schema$Parser.)]
    (.parse parser json-string)))
