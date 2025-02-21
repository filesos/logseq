(ns frontend.mobile.camera
  (:require ["@capacitor/camera" :refer [Camera CameraResultType]]
            ["@capacitor/filesystem" :refer [Filesystem]]
            [lambdaisland.glogi :as log]
            [promesa.core :as p]
            [frontend.handler.editor :as editor-handler]
            [frontend.state :as state]
            [frontend.date :as date]
            [frontend.util :as util]
            [frontend.commands :as commands]
            [frontend.mobile.util :as mobile-util]))

(defn- save-photo []
  (p/let [photo (p/catch
                    (.getPhoto Camera (clj->js
                                       {:allowEditing (get-in
                                                       (state/get-config)
                                                       [:mobile/photo :allow-editing?])
                                        :saveToGallery true
                                        :resultType (.-Base64 CameraResultType)}))
                    (fn [error]
                      (log/error :photo/get-failed {:error error})))
          [repo-dir assets-dir] (editor-handler/ensure-assets-dir! (state/get-current-repo))
          filename (str (date/get-date-time-string-2) ".jpeg")
          path (cond
                 (mobile-util/native-android?)
                 (str "file://" repo-dir "/" assets-dir "/" filename)

                 (mobile-util/native-ios?)
                 (str repo-dir assets-dir "/" filename)

                 :else
                 (str repo-dir assets-dir "/" filename))
          _file (p/catch
                    (.writeFile Filesystem (clj->js {:data (.-base64String photo)
                                                     :path path
                                                     :recursive true}))
                    (fn [error]
                      (log/error :file/write-failed {:path path
                                                     :error error})))]
    (p/resolved filename)))

(defn embed-photo [id]
  (let [block (state/get-edit-block)
        format (:block/format block)]
    (p/let [filename (save-photo)]
      (commands/simple-insert!
       id
       (case format
         :org (util/format "[[../assets/%s]]" filename)
         (util/format "![%s](../assets/%s)" filename filename))
       {}))))
