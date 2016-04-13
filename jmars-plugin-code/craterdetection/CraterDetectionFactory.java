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

import edu.asu.jmars.layer.LViewFactory;
import edu.asu.jmars.layer.Layer;
import edu.asu.jmars.layer.SerializedParameters;

public class CraterDetectionFactory extends LViewFactory {

	public CraterDetectionFactory() {
	}

	public Layer.LView createLView() {
		return null;
	}
	
	// Supply the proper name and description.
	public String getName() {
		return "Crater Detection Layer";
	}
	
	public String getDesc() {
		return "A layer for detecting craters";
	}
	
	public void createLView(Callback callback) {
		callback.receiveNewLView(createLView(new StartupParameters()));
	}
	
	public Layer.LView recreateLView(SerializedParameters parmBlock) {
		if (parmBlock instanceof StartupParameters) {
			//	return new LView with previously saved parms
			return createLView((StartupParameters)parmBlock);
		} else {
			// return new LView with default parms
			return createLView(new StartupParameters());
		}
	}
	
	private Layer.LView createLView(StartupParameters parms) {
		// create a new layer to be shared by all the views
		CraterDetectionLayer layer = new CraterDetectionLayer(parms);
		// create the main view
		CraterDetectionLView view = new CraterDetectionLView(layer);
		// by default, the main view provides the viewing area
		layer.setActiveView(view);
		// silliness for session serializing
		view.originatingFactory = this;
		// cause an immediate view change
		view.setVisible(true);
		return view;
	}
}
