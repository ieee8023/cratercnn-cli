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

import java.awt.geom.Rectangle2D;

import edu.asu.jmars.Main;
import edu.asu.jmars.ProjObj;
import edu.asu.jmars.layer.Layer.LView;

public class CraterDetectionLView extends LView {

	private static final long serialVersionUID = 1L;
	private CraterDetectionLayer myLayer;
	
	public CraterDetectionLView(CraterDetectionLayer parent) {
		super(parent);
		myLayer = parent;
	}
	
	protected LView _new() {
		return new CraterDetectionLView(myLayer);
	}

	boolean isDead = false;

	
	protected Object createRequest(Rectangle2D where) {
		if (isAlive()) {
			return new Request(this, getProj().getWorldWindow(), viewman2.getMagnify(), Main.PO);
		} else {
			return null;
		}
	}
	
	/**
	 * These LViews do nothing except forward view changes to the Layer, so the
	 * Layer never sends data back.
	 */
	public synchronized void receiveData(Object layerData) {}

	protected void updateSettings(boolean saving) {

	}
}

class Request {
	public final CraterDetectionLView source;
	public final Rectangle2D extent;
	public final int ppd;
	public final ProjObj projection;
	public Request(CraterDetectionLView source, Rectangle2D extent, int ppd, ProjObj projection) {
		this.source = source;
		this.extent = extent;
		this.ppd = ppd;
		this.projection = projection;
	}
	public String toString() {
		return "MapRequest[source="+source+", extent="+extent+", ppd="+ppd+", proj="+projection+"]";
	}
}
