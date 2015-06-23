# HttpRequest v0.1
Http Requests for easy implementation on Java.

Next versions will make more confortable readme.

Example use:

- GET request for google maps:
    
    String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                    addres[0] + "&sensor=false";

            Log.d(DEBUG_URL_TAG,uri);

            HttpRequest cliente_mapa = null;
            try {
                cliente_mapa = new HttpRequest(uri,null);
                cliente_mapa.setMethod("GET",0);
                cliente_mapa.start();

                cliente_mapa.join();
                JSONObject json = cliente_mapa.getResponse();
                lng = ((JSONArray)json.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                lat = ((JSONArray)json.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

            } catch (MalformedURLException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }
            
