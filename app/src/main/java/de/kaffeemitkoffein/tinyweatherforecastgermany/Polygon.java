package de.kaffeemitkoffein.tinyweatherforecastgermany;

public class Polygon{
    public float[] polygonX;
    public float[] polygonY;
    private int nvert;

    public String identifier_link;

    public Polygon(int n){
        polygonX = new float[n];
        polygonY = new float[n];
        nvert = n;
    }

    public Polygon(String source){
        String[] s = source.split(" ");
        polygonX = new float[s.length];
        polygonY = new float[s.length];
        nvert = s.length;
        for (int j=0; j<s.length; j++){
            String[] polygon_pair=s[j].split(",");
            polygonX[j] = Float.parseFloat(polygon_pair[1]);
            polygonY[j] = Float.parseFloat(polygon_pair[0]);
        }
    }

    public Polygon(float[] polygonX, float[] polygonY, String identifier_link){
        this.polygonX = polygonX;
        this.polygonY = polygonY;
        this.nvert = polygonX.length;
        this.identifier_link = identifier_link;
    }

    public float getPolygonX(int pos){
        return polygonX[pos];
    }

    public float getPolygonY(int pos){
        return polygonY[pos];
    }

    /**
     * determines if point is inside a polygon.
     *
     * Adapted to java from C, see https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html,
     * original code Copyright (c) 1970-2003, Wm. Randolph Franklin, see COPYING.txt for
     * license.
     *
     * @param nvert
     * @param vertx
     * @param verty
     * @param testx
     * @param testy
     * @return
     */

    private boolean isInPolygon(int nvert, float[] vertx, float[] verty, float testx, float testy ){
        boolean c = false;
        int i, j;
        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                c = !c;
        }
        return c;
    }

    public boolean isInPolygon(float testx, float testy){
        return isInPolygon(nvert,polygonX,polygonY,testx,testy);
    }

}

