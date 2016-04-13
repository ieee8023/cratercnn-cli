// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.


package edu.asu.jmars.layer.craterdetection;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import edu.asu.jmars.layer.DataReceiver;
import edu.asu.jmars.layer.Layer;
import edu.asu.jmars.layer.SerializedParameters;
import edu.asu.jmars.layer.map2.MapChannel;
import edu.asu.jmars.layer.map2.MapChannelReceiver;
import edu.asu.jmars.layer.map2.MapData;
import edu.asu.jmars.layer.map2.MapRequest;
import edu.asu.jmars.layer.map2.MapServer;
import edu.asu.jmars.layer.map2.MapServerFactory;
import edu.asu.jmars.layer.map2.MapSource;
import edu.asu.jmars.util.Config;
import edu.asu.jmars.util.DebugLog;

/* Layer data model */
public class CraterDetectionLayer extends Layer {
	private static DebugLog log = DebugLog.instance();
	
	/** The active view that provides the viewing geometry for the 3D panel */
    private CraterDetectionLView activeView;
    /** Most recent requests from all views */
    private Map<CraterDetectionLView, Request> requests = new LinkedHashMap<CraterDetectionLView,Request>();
    /** Map to use for elevation data */
    private MapSource elevationSource;
    /** Map produer provides maps for current request for current view for current map source */
    private MapChannel mapProducer = new MapChannel();
    /** Map data last received from the map producer */
    private MapData lastUpdate;
    
    public CraterDetectionLayer(StartupParameters parms) {
		initialLayerData = parms;
		elevationSource = parms.getMapSource();
	}
    
    public MapSource getElevationSource() {
    	return elevationSource;
    }
    
    public void setElevationSource(MapSource source) {
    	this.elevationSource = source;
    }
    
    public CraterDetectionLView getActiveView() {
    	return activeView;
    }
    
    public void setActiveView(CraterDetectionLView view) {
    	this.activeView = view;
    }
    
    public Raster getElevationData() {
    	return lastUpdate.getImage().getRaster();
    }
    
    public void receiveRequest(Object layerRequest, DataReceiver requester) {
    	Request request = (Request)layerRequest;
    	Request oldRequest = requests.put(request.source, request);
    	log.println("Old request: " + oldRequest);
    	if (request.source == getActiveView() && elevationSource != null) {
        	mapProducer.setRequest(new MapRequest(elevationSource, request.extent, request.ppd, request.projection));
        	mapProducer.addReceiver(new MapChannelReceiver() {
    			public void mapChanged(MapData mapData) {
    				if (mapData.isFinished()) {
    					lastUpdate = mapData;
    					//activeView.getFocusPanel().update();
    					activeView.setVisible(false);
    					
    					
    					System.out.println("ran112");
    					if (activeView.viewman2 != null){
    						BufferedImage img = activeView.viewman2.copyMainWindow();
    						try {
    							System.out.println("ran");
    							ImageIO.write(img, "png", new File("test2.png"));
    						} catch (IOException e1) {
    							// TODO Auto-generated catch block
    							e1.printStackTrace();
    						}
    					}
    					
    					
    					Runtime rt = Runtime.getRuntime();
    					try {
							Process proc = rt.exec("sh runcda.sh");
							
							BufferedReader stdInput = new BufferedReader(new 
								     InputStreamReader(proc.getInputStream()));

								BufferedReader stdError = new BufferedReader(new 
								     InputStreamReader(proc.getErrorStream()));
							
							System.out.println("Here is the standard output of the command:\n");
							String s = null;
							while ((s = stdInput.readLine()) != null) {
							    System.out.println(s);
							}

							// read any errors from the attempted command
							System.out.println("Here is the standard error of the command (if any):\n");
							while ((s = stdError.readLine()) != null) {
							    System.out.println(s);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					
    					
    				}
    			}
        	});
    	}
	}
}


class StartupParameters extends HashMap<String,String> implements SerializedParameters {
	private static final long serialVersionUID = 1L;
	private transient DebugLog log = DebugLog.instance();
	
	/** Initializes the elevation data from the jmars.config 'threed.default_elevation*' keys */
	public StartupParameters() {
		put("serverName", Config.get("threed.default_elevation.server"));
		put("sourceName", Config.get("threed.default_elevation.source"));
	}
	
	public MapSource getMapSource() {
		MapServer server = MapServerFactory.getServerByName(get("serverName"));
		if (server == null) {
			log.aprintln("Elevation server not accessible");
			return null;
		}
		
		MapSource source = server.getSourceByName(get("sourceName"));
		if (source == null) {
			log.aprintln("Elevation source not accessible");
			return null;
		}
		
		return source;
	}
	
	public void setMapSource(MapSource source) {
		put("serverName", source.getServer().getName());
		put("sourceName", source.getName());
	}
}
