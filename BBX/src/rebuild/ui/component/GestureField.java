//#preprocessor

//---------------------------------------------------------------------------------
//
//  BlackBerry Extensions
//  Copyright (c) 2011-2012 Vincent Simonetti
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
//---------------------------------------------------------------------------------
//
// Copyright (c) 2011-2012 Vincent Simonetti
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
// to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
//---------------------------------------------------------------------------------
//
package rebuild.ui.component;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
//#endif
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
//#ifdef BlackBerrySDK4.5.0
import rebuild.util.MathUtilities;
//#else
import net.rim.device.api.util.MathUtilities;
//#endif

//Based off: http://dwilson.org/blog/2009/4/19/implementing-pinch-zoom-on-the-iphone/

//TODO Update to support non-touchscreen interactions
//TODO Update to support Pinch-Zoom Gesture (will require some work because it seems to be the cheap, scale-only type Pinch-zoom that all built-in smartphone systems do).
//TODO Rotation component help: http://www.euclideanspace.com/maths/geometry/affine/aroundPoint/index.htm
//TODO Very weird bug: If you do pinch-zoom where one finger is stationary and the other moves, you get different performance results. If you move one corner, it runs smooth. But move the other corner, it lags.
//TODO Update for 7.1

/**
 * Gesture field, various gestures and system events will are processed and handled in this field.
 * @since BBX 1.0.0
 */
public abstract class GestureField extends Field
{
	/**
	 * A gesture that was generated by a {@link GestureField}.
	 */
	public final class Gesture
	{
		/**
		 * Click and pause at a specific point on the touch screen for more than 500 milliseconds. A new consecutive EVENT_CLICK_REPEAT event is generated every 500 
		 * milliseconds until the user moves or removes touch from the touch screen.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int EVENT_CLICK_REPEAT = TouchGesture.CLICK_REPEAT;
//#else
		public static final int EVENT_CLICK_REPEAT = 0x3507;
//#endif
		/**
		 * Two consecutive quick touch and release gesture on the touch screen. EVENT_DOUBLE_TAP events are independent of {@link Gesture#EVENT_TAP} event, i.e. 
		 * applications will receive a EVENT_DOUBLE_TAP event after a {@link Gesture#EVENT_TAP}.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int EVENT_DOUBLE_TAP = TouchGesture.DOUBLE_TAP;
//#else
		public static final int EVENT_DOUBLE_TAP = 0x3;
//#endif
		/**
		 * Touch and pause at a specific point on the touch screen for more than the user-defined number of milliseconds (configurable setting found in Screen/Keyboard 
		 * Options). A new consecutive HOVER event is generated at this interval in milliseconds until the user moves or removes touch from the touch screen.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int EVENT_HOVER = TouchGesture.HOVER;
//#else
		public static final int EVENT_HOVER = 0x0;
//#endif
		/**
		 * Quick motion gesture across the touch screen.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int EVENT_SWIPE = TouchGesture.SWIPE;
//#else
		public static final int EVENT_SWIPE = 0x3504;
//#endif
		/**
		 * Quick touch and release gesture on the touch screen.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int EVENT_TAP = TouchGesture.TAP;
//#else
		public static final int EVENT_TAP = 0x2;
//#endif

		/**
		 * A pinch event where two fingers are used to change the size, position, or rotation of something on screen.
		 */
		public static final int EVENT_PINCH = 0x3505;
		
		private static final int TYPE_VALUE1 = (1 + 0) << 29;
		private static final int TYPE_VALUE2 = (1 + 1) << 29;
		private static final int TYPE_VALUE3 = (1 + 2) << 29;
		private static final int TYPE_VALUE4 = (1 + 3) << 29;
		private static final int TYPE_VALUE5 = (1 + 4) << 29;
		private static final int TYPE_MASK = (1 + 6) << 29; //Limit should be 0xFFFF0000 or 65536 << 16
		private static final int TYPE_MASK_INVERSE = ~TYPE_MASK;
		
		/**
		 * Retrieves the number of {@link #EVENT_CLICK_REPEAT} events generated before the user moves or releases from the touch screen. A new consecutive 
		 * {@link #EVENT_CLICK_REPEAT} event is generated every 500 milliseconds.
		 */
		public static final int TYPE_CLICK_REPEAT_COUNT = TYPE_VALUE1 | EVENT_CLICK_REPEAT;
		/**
		 * Retrieves the number of {@link #EVENT_HOVER} events generated before the user moves or removes touch from the touch screen. A new consecutive 
		 * {@link #EVENT_HOVER} event is generated every 100 milliseconds.
		 */
		public static final int TYPE_HOVER_COUNT = TYPE_VALUE1 | EVENT_HOVER;
		/**
		 * Retrieves the consecutive number of {@link #EVENT_TAP} events generated before the user moves or maintains touch for greater than 150 milliseconds.
		 */
		public static final int TYPE_TAP_COUNT = TYPE_VALUE1 | EVENT_TAP;
		/**
		 * Retrieves the angle (in degrees) associated with a swipe gesture relative to the device's current upward direction.
		 */
		public static final int TYPE_SWIPE_ANGLE = TYPE_VALUE1 | EVENT_SWIPE;
		/**
		 * Retrieves the relative cardinal direction associated with a swipe gesture based on the device's upward direction.
		 * @see #SWIPE_NORTH
		 * @see #SWIPE_EAST
		 * @see #SWIPE_SOUTH
		 * @see #SWIPE_WEST
		 */
		public static final int TYPE_SWIPE_DIRECTION = TYPE_VALUE2 | EVENT_SWIPE;
		/**
		 * Get the swipe magnitude (in pixels) of the swipe.
		 */
		public static final int TYPE_SWIPE_MAGNITUDE = TYPE_VALUE3 | EVENT_SWIPE;
		/**
		 * Delta translation of pinch point on the X coordinate in Fixed32 format.
		 */
		public static final int TYPE_PINCH_TRANSLATE_X = TYPE_VALUE1 | EVENT_PINCH;
		/**
		 * Delta translation of pinch point on the Y coordinate in Fixed32 format.
		 */
		public static final int TYPE_PINCH_TRANSLATE_Y = TYPE_VALUE2 | EVENT_PINCH;
		/**
		 * Delta rotation in Fixed32 radians.
		 */
		public static final int TYPE_PINCH_ROTATE = TYPE_VALUE3 | EVENT_PINCH;
		/**
		 * Delta scale in the X plane in Fixed32 format.
		 */
		public static final int TYPE_PINCH_SCALE_X = TYPE_VALUE4 | EVENT_PINCH;
		/**
		 * Delta scale in the Y plane in Fixed32 format.
		 */
		public static final int TYPE_PINCH_SCALE_Y = TYPE_VALUE5 | EVENT_PINCH;
		
		/**
		 * Gesture direction that is equivalent to 180 degrees +/- 45 degrees relative to the device's current upward direction. Can be bitwise ORed with other 
		 * cardinal directions.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int SWIPE_EAST = TouchGesture.SWIPE_EAST;
//#else
		public static final int SWIPE_EAST = 0x4;
//#endif
		/**
		 * Gesture direction that is equivalent to 90 degrees +/- 45 degrees relative to the device's current upward direction. Can be bitwise ORed with other 
		 * cardinal directions.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int SWIPE_NORTH = TouchGesture.SWIPE_NORTH;
//#else
		public static final int SWIPE_NORTH = 0x1;
//#endif
		/**
		 * Gesture direction that is equivalent to 270 degrees +/- 45 degrees relative to the device's current upward direction. Can be bitwise ORed with other 
		 * cardinal directions.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int SWIPE_SOUTH = TouchGesture.SWIPE_SOUTH;
//#else
		public static final int SWIPE_SOUTH = 0x2;
//#endif
		/**
		 * Gesture direction that is equivalent to 180 degrees +/- 45 degrees relative to the device's current upward direction. Can be bitwise ORed with other 
		 * cardinal directions.
		 */
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		public static final int SWIPE_WEST = TouchGesture.SWIPE_WEST;
//#else
		public static final int SWIPE_WEST = 0x8;
//#endif

		
		private int type, value1, value2, value3, value4, value5;
		
		private Gesture(int type, int[] values)
		{
			this.type = type; //Should probably do some verification but it is a private function (and there is no possibility of Reflection) so no worries.
			switch(this.type)
			{
				case EVENT_PINCH:
					value1 = values[0];
					value2 = values[1];
					value3 = values[2];
					value4 = values[3];
					value5 = values[4];
					break;
			}
		}
		
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
		private Gesture(TouchGesture gesture)
		{
			this.type = gesture.getEvent();
			switch(this.type)
			{
				case EVENT_CLICK_REPEAT:
					this.value1 = gesture.getClickRepeatCount();
					break;
				case EVENT_HOVER:
					this.value1 = gesture.getHoverCount();
					break;
				case EVENT_TAP:
					this.value1 = gesture.getTapCount();
					break;
				case EVENT_SWIPE:
					this.value1 = gesture.getSwipeAngle();
					this.value2 = gesture.getSwipeDirection();
					this.value3 = gesture.getSwipeMagnitude();
					break;
			}
		}
//#endif
		
		/**
		 * Get what type of gesture this Gesture is.
		 */
		public int getEvent()
		{
			return this.type;
		}
		
		/**
		 * Get a specific value from this Gesture.
		 * @param valueType The value type to get.
		 * @return The resulting value.
		 * @throws IllegalArgumentException If valueType is not a valid type for this Gesture.
		 */
		public int getGestureValue(int valueType)
		{
			if((TYPE_MASK_INVERSE & valueType) == this.type)
			{
				switch(TYPE_MASK & valueType)
				{
					case TYPE_VALUE1:
						return this.value1;
					case TYPE_VALUE2:
						return this.value2;
					case TYPE_VALUE3:
						return this.value3;
					case TYPE_VALUE4:
						return this.value4;
					case TYPE_VALUE5:
						return this.value5;
				}
			}
			throw new IllegalArgumentException();
		}
	}
	
	//General constants
	private static final double FP_2_DOUBLE = 1.0 / Fixed32.ONE;
	private static final int MAX_CONTACT_COUNT = 2;
	
	//Interaction types
	private static final int INTERACTION_MOVE = 1;
	private static final int INTERACTION_GESTURE = 2;
	private static final int INTERACTION_CONTACT = 3;
	private static final int INTERACTION_CANCEL = 4;
	private static final int INTERACTION_CLICK = 5;
	
	//Fields
	private XYPoint[] start = new XYPoint[MAX_CONTACT_COUNT], contacts = new XYPoint[MAX_CONTACT_COUNT];
	
	/** Set to true if gesture processing should occur, false if otherwise. Default is true.*/
	protected boolean gestureProcessing = true;
	
	//Functions
	protected abstract void layout(int width, int height);

	protected abstract void paint(Graphics graphics);
	
	/**
	 * A move event occurred.
	 * @param x The delta-X of the move in Fixed32 format.
	 * @param y The delta-Y of the move in Fixed32 format.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionMove(int x, int y)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * A scale event occurred.
	 * @param x The delta scale of the X coordinate in Fixed32 format.
	 * @param y The delta scale of the Y coordinate in Fixed32 format.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionScale(int x, int y)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * A rotation event occurred.
	 * @param rad The delta rotation of the event in Fixed32 format.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionRotate(int rad)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * Any gesture that has occurred.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionGesture(Gesture gesture)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * Any gesture operation was canceled.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionCanceled()
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * Any click event.
	 * @param click true if the event was a click, false if it was an unclick.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionClick(boolean click)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * Any contact with the screen.
	 * @param contact The contact that occurred (1 for first contact, 2 for second contact, etc.).
	 * @param down true if the contact touched the screen, false if it was removed.
	 * @return true if the gesture was handled, false if otherwise.
	 */
	protected boolean interactionContact(int contact, boolean down)
	{
		//Doesn't do anything by default
		return false;
	}
	
	/**
	 * Any interaction is complete.
	 */
	protected void interactionComplete()
	{
		//Doesn't do anything by default
	}
	
	private boolean inInteractionComplete(int type, int inType, Object args)
	{
		boolean handled = false;
		int[] argI;
		switch(type)
		{
			case INTERACTION_GESTURE:
				Gesture ges;
				if(args instanceof int[])
				{
					argI = (int[])args;
					if(argI[2] == 0 && //Rotation
							argI[3] == Fixed32.ONE && argI[4] == Fixed32.ONE) //Original scale
					{
						//Only moving occurs so save some extra processing that the developer might have and memory
						handled = interactionMove(argI[0], argI[1]);
						break;
					}
					else if(argI[0] == 0 && argI[1] == 0 && //Translation
							argI[3] == Fixed32.ONE && argI[4] == Fixed32.ONE) //Original scale
					{
						//Only rotation occurs so save some extra processing that the developer might have and memory
						handled = interactionRotate(argI[2]);
						break;
					}
					else if(argI[0] == 0 && argI[1] == 0 && //Translation
							argI[2] == 0) //Rotation
					{
						//Only scaling occurs so save some extra processing that the developer might have and memory
						handled = interactionScale(argI[3], argI[4]);
						break;
					}
					else
					{
						ges = new Gesture(inType, argI);
					}
				}
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
				else if(args instanceof TouchGesture)
				{
					ges = new Gesture((TouchGesture)args);
				}
//#endif
				else
				{
					//What happened
					System.out.println("Unknown gesture argument: " + args);
					break;
				}
				handled = interactionGesture(ges);
				break;
			case INTERACTION_MOVE:
				argI = (int[])args;
				handled = interactionMove(argI[0], argI[1]);
				break;
			case INTERACTION_CONTACT:
				if(inType != 0)
				{
					handled = interactionContact(Math.abs(inType), inType > 0);
				}
				break;
			case INTERACTION_CANCEL:
				handled = interactionCanceled();
				break;
			case INTERACTION_CLICK:
				handled = interactionClick(inType == 
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
					TouchEvent.CLICK);
//#else
					0x3505);
//#endif
				break;
		}
		if(handled)
		{
			interactionComplete();
		}
		return handled;
	}
	
//#ifndef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1
	/**
	 * Touch Event handles all gesture functions, if you are using any gesture functions then you should <strong>not</strong> return without invoking this function.
	 * @see Field#touchEvent(TouchEvent)
	 */
	protected boolean touchEvent(TouchEvent message)
	{
		if(!gestureProcessing)
		{
			//Gesture processing should not occur, so just do default processing.
			return super.touchEvent(message);
		}
		switch(message.getEvent())
		{
			case TouchEvent.CANCEL:
				//Erase contacts because there is nothing to do anymore
				for(int i = 0; i < MAX_CONTACT_COUNT; i++)
				{
					start[i] = null;
					contacts[i] = null;
				}
				return inInteractionComplete(INTERACTION_CANCEL, 0, null);
			case TouchEvent.CLICK:
			case TouchEvent.UNCLICK:
				return inInteractionComplete(INTERACTION_CLICK, message.getEvent(), null);
			case TouchEvent.GESTURE:
				return inInteractionComplete(INTERACTION_GESTURE, 0, message.getGesture());
			case TouchEvent.DOWN:
				int contactIndex = 0;
				for(int i = 0; i < MAX_CONTACT_COUNT; i++)
				{
					int x = message.getX(i + 1);
					int y = message.getY(i + 1);
					if(x != -1)
					{
						contactIndex = i + 1;
						start[i] = new XYPoint(Fixed32.toFP(x), Fixed32.toFP(y));
						contacts[i] = new XYPoint(start[i]);
					}
				}
				return inInteractionComplete(INTERACTION_CONTACT, contactIndex, null);
			case TouchEvent.MOVE:
				//XXX Currently only supports up to two contacts, make it dynamic
				switch(getContactCount())
				{
					case 1:
						int[] motions = new int[2];
						contacts[0].x = Fixed32.toFP(message.getX(1));
						contacts[0].y = Fixed32.toFP(message.getY(1));
						motions[0] = contacts[0].x - start[0].x;
						motions[1] = contacts[0].y - start[0].y;
						start[0].x = contacts[0].x;
						start[0].y = contacts[0].y;
						return inInteractionComplete(INTERACTION_MOVE, 0, motions);
					case 2:
						if(getRawContactCount() != 2)
						{
							//TODO O no, this isn't good. The first contact was removed before the second, how should this react?... well for now just reset the values.
							start[0] = start[1];
							start[1] = null;
							contacts[0] = contacts[1];
							contacts[1] = null;
							System.err.println("Not proper contact arrangement");
							return false;
						}
						if(start[0].equals(contacts[0]))
						{
							//If the first point is the same as the second it means that this is only the first pass through.
							//BlackBerry goes in order of contact for processing; DOWN1, DOWN2, MOVE1, MOVE2, UP1, UP2...
							contacts[0].x = Fixed32.toFP(message.getX(1));
							contacts[0].y = Fixed32.toFP(message.getY(1));
						}
						else
						{
							//Onto second contact, now to do actual processing
							contacts[1].x = Fixed32.toFP(message.getX(2));
							contacts[1].y = Fixed32.toFP(message.getY(2));
							motions = new int[13]; //transX, transY, rotate, scaleX, scaleY, ...temp variables
							//motions[3] = motions[4] = Fixed32.ONE;
							
							//First the scale
							//TODO: Add ability to do axis-independent scaling (maybe as an option so that when "pinching" occurs it can have either axis-independent or general scaling).
							//Axis-independent is each axis is processed the same way but the other axis is not used in the process.
							//sqrt((x2 - x1)^2 + (y2 - y1)^2)
							motions[5] = start[1].x - start[0].x;
							motions[6] = start[1].y - start[0].y;
							motions[7] = contacts[1].x - contacts[0].x;
							motions[8] = contacts[1].y - contacts[0].y;
							//scale = newScale / prevScale, need to use doubles because Fixed32 is not "large" enough and the value "wraps"
							motions[7] = (int)MathUtilities.round((
									Math.sqrt(MathUtilities.pow(motions[7] * FP_2_DOUBLE, 2) + MathUtilities.pow(motions[8] * FP_2_DOUBLE, 2)) / 
									Math.sqrt(MathUtilities.pow(motions[5] * FP_2_DOUBLE, 2) + MathUtilities.pow(motions[6] * FP_2_DOUBLE, 2))
											) * Fixed32.ONE
										); //Currently there is no system to figure out X scale and Y scale, they are just there for future expansion.
							
							//The one problem with scaling a region is that the world origin is not the center of the object, so the object needs to be translated to 
							//keep it in the same location
							//-Figure out width/height of the selection area
							motions[5] = Math.max(start[0].x, start[1].x) - (motions[9] = Math.min(start[0].x, start[1].x));
							motions[6] = Math.max(start[0].y, start[1].y) - (motions[10] = Math.min(start[0].y, start[1].y));
							//-Now get it's midpoint
							XYRect extent = new XYRect(motions[9], motions[10], motions[5], motions[6]);
							motions[9] = (extent.x + extent.X2()) >> 1; //In place of "value / 2"
							motions[10] = (extent.y + extent.Y2()) >> 1; //Hoping that X2/Y2 are simply "return this.x/y + this.width/height;"
							//-Figure out the difference in selection area size
							motions[11] = Fixed32.mul(motions[5], motions[7]);
							motions[12] = Fixed32.mul(motions[6], motions[7]);
							motions[5] -= motions[11];
							motions[6] -= motions[12];
							//-The position of the midpoint of the touch as a percentage of the total distance
							this.getExtent(extent);
							motions[7] = motions[9] / extent.width;
							motions[8] = motions[10] / extent.height;
							//-Figure out the translation
							motions[0] = Fixed32.mul(motions[5], motions[7]);
							motions[1] = Fixed32.mul(motions[6], motions[8]);
							
							//Now apply the actual scale, since we have the "new width and height" we have an X and Y
							motions[5] += motions[11]; //First recreate the original width/height
							motions[6] += motions[12];
							motions[3] = Fixed32.div(motions[11], motions[5]);
							motions[4] = Fixed32.div(motions[12], motions[6]);
							
							//Now the rotation
							//TODO
							
							//Finally the translation
							//TODO: Check adjustment from Scaling offset to make sure that it isn't moving too much, currently "overshooting" destination a bit
							motions[5] = start[0].x + start[1].x;
							motions[6] = start[0].y + start[1].y;
							motions[7] = contacts[0].x + contacts[1].x;
							motions[8] = contacts[0].y + contacts[1].y;
							motions[5] >>= 1;
							motions[6] >>= 1;
							motions[7] >>= 1;
							motions[8] >>= 1;
							motions[0] -= motions[5] - motions[7]; //Add the translation so the "scale translation" doesn't get overridden
							motions[1] -= motions[6] - motions[8];
							
							//Reset starting point and invoke callbacks
							start[0].x = contacts[0].x;
							start[0].y = contacts[0].y;
							start[1].x = contacts[1].x;
							start[1].y = contacts[1].y;
							return inInteractionComplete(INTERACTION_GESTURE, Gesture.EVENT_PINCH, motions);
						}
						break;
				}
				return false; //Means we are only on the first of two points.
			case TouchEvent.UP:
				contactIndex = 0;
				for(int i = 0; i < MAX_CONTACT_COUNT; i++)
				{
					int x = message.getX(i + 1);
					if(x != -1)
					{
						contactIndex = 0 - (i + 1);
						start[i] = null;
						contacts[i] = null;
						GestureField.compactContacts(start, contacts);
					}
				}
				return inInteractionComplete(INTERACTION_CONTACT, contactIndex, null);
		}
		return super.touchEvent(message);
	}
//#endif
	
	private static void compactContacts(XYPoint[] starts, XYPoint[] contacts)
	{
		for(int i = 0; i < MAX_CONTACT_COUNT - 1; i++)
		{
			if(starts[i] == null && starts[i + 1] != null)
			{
				starts[i] = starts[i + 1];
				starts[i + 1] = null;
				contacts[i] = contacts[i + 1];
				contacts[i + 1] = null;
			}
		}
	}
	
	private static XYPoint determineRotationOrigin(int angle, XYPoint preRot, XYPoint postRot)
	{
		//Code doesn't seem accurate enough. When testing with preRot = {50,50}, angle = 45 degrees, and rotation center = {10,11} it returns {7.77..., 11.797...}
		
		//First create a simple rotation matrix (no rotation center)
		int[] mat = new int[9];
		GestureField.matrixSetRotate(mat, 0, angle);
		
		//Next figure out translation of matrix
		mat[2] = (postRot.x - Fixed32.mul(preRot.x, mat[0])) - Fixed32.mul(preRot.x, mat[1]);
		mat[5] = (postRot.y - Fixed32.mul(preRot.y, mat[4])) - Fixed32.mul(preRot.y, mat[3]);
		/* Original C# source
		 * mat.OffsetX = (postRot.X - (preRot.X * mat.M11)) - (preRot.X * mat.M21);
		 * mat.OffsetY = (postRot.Y - (preRot.Y * mat.M22)) - (preRot.Y * mat.M12);
		 */
		
		/* Finally figure out the rotation origin
		 * Uses Cramer's rule and the coefficient determinant to figure out rotation origin
		 * a*x + b*y = c
		 * d*x + e*y = f
		 * 
		 * x = (c*e - f*b) / D
		 * y = (a*f - d*c) / D
		 * D = a*e - b*d
		 * 
		 * Uses the less efficient Double type so overflow/underflow don't occur
		 */
		double aNe = (Fixed32.ONE - mat[0]) * FP_2_DOUBLE;
		double mat1 = mat[1] * FP_2_DOUBLE;
		double mat2 = mat[2] * FP_2_DOUBLE;
		double mat3 = mat[3] * FP_2_DOUBLE;
		double mat5 = mat[5] * FP_2_DOUBLE;
		double D = MathUtilities.pow(aNe, 2) - (mat3 * mat1);
		return new XYPoint((int)MathUtilities.round((((mat2 * aNe) - (mat5 * mat3) / D)) * Fixed32.ONE), 
				(int)MathUtilities.round((((aNe * mat5) - (mat1 * mat2) / D)) * Fixed32.ONE));
		/* Original C# source
		 * double inverseCos = 1.0 - mat.M11;
		 * double D = (inverseCos * inverseCos) - (mat.M12 * mat.M21);
		 * return new Point(((mat.OffsetX * inverseCos) - (mat.OffsetY * mat.M12)) / D,
		 * 		((inverseCos * mat.OffsetY) - (mat.M21 * mat.OffsetX)) / D);
		 */
	}
	
	/**
	 * Sets a 3x3 matrix to it's identity.
	 */
	protected static void matrixSetIdentity(int[] mat, int index)
	{
		/*
		 * 0-M11	1-M21	2-OffsetX
		 * 3-M12	4-M22	5-OffsetY
		 * 6	7	8
		 */
		mat[index] = mat[index + 4] = mat[index + 8] = Fixed32.ONE;
		mat[index + 1] = mat[index + 2] = mat[index + 3] = mat[index + 5] = mat[index + 6] = mat[index + 7] = 0;
	}
	
	/**
	 * Sets a 3x3 matrix to a rotation matrix. This function sets the matrix to an identity matrix before setting it to a rotation matrix.
	 * @param rotationInRad The rotation in radians.
	 */
	protected static void matrixSetRotate(int[] mat, int index, int rotationInRad)
	{
		matrixSetRotate(mat, index, rotationInRad, 0, 0);
	}
	
	/**
	 * Sets a 3x3 matrix to a rotation matrix. This function sets the matrix to an identity matrix before setting it to a rotation matrix.
	 * @param rotationInRad The rotation in radians.
	 * @param centerX The rotation center on the X coordinate in Fixed32 format.
	 * @param centerY The rotation center on the Y coordinate in Fixed32 format.
	 */
	protected static void matrixSetRotate(int[] mat, int index, int rotationInRad, int centerX, int centerY)
	{
		matrixSetIdentity(mat, index);
		mat[index + 4] = mat[index] = Fixed32.Cos(rotationInRad);
		mat[index + 1] = -(mat[index + 3] = Fixed32.Sin(rotationInRad));
		if(centerX != 0 || centerY != 0)
		{
			int inCos = Fixed32.ONE - mat[index];
			mat[index + 2] = Fixed32.mul(centerX, inCos) + Fixed32.mul(centerY, mat[index + 1]);
			mat[index + 5] = Fixed32.mul(centerY, inCos) - Fixed32.mul(centerX, mat[index + 1]);
		}
	}
	
	/**
	 * Sets a 3x3 matrix to a skew matrix. This function sets the matrix to an identity matrix before setting it to a skew matrix.
	 * @param x The angle on the X coordinate to skew in Fixed32 format.
	 * @param y The angle on the Y coordinate to skew in Fixed32 format.
	 */
	protected static void matrixSetSkew(int[] mat, int index, int x, int y)
	{
		matrixSetIdentity(mat, index);
		mat[index + 3] = Fixed32.Tan(y);
		mat[index + 1] = Fixed32.Tan(x);
	}
	
	/**
	 * Sets a 3x3 matrix to a translation matrix. This function sets the matrix to an identity matrix before setting it to a translation matrix.
	 * @param x The X translation in Fixed32 format.
	 * @param y The Y translation in Fixed32 format.
	 */
	protected static void matrixSetTranslate(int[] mat, int index, int x, int y)
	{
		matrixSetIdentity(mat, index);
		mat[index + 2] = x;
		mat[index + 5] = y;
	}
	
	/**
	 * Sets a 3x3 matrix to a scale matrix. This function sets the matrix to an identity matrix before setting it to a scale matrix.
	 * @param x The X scale in Fixed32 format.
	 * @param y The Y scale in Fixed32 format.
	 */
	protected static void matrixSetScale(int[] mat, int index, int x, int y)
	{
		matrixSetScale(mat, index, x, y, 0, 0);
	}
	
	/**
	 * Sets a 3x3 matrix to a scale matrix. This function sets the matrix to an identity matrix before setting it to a scale matrix.
	 * @param x The X scale in Fixed32 format.
	 * @param y The Y scale in Fixed32 format.
	 * @param centerX The scaling center on the X coordinate in Fixed32 format.
	 * @param centerY The scaling center on the Y coordinate in Fixed32 format.
	 */
	protected static void matrixSetScale(int[] mat, int index, int x, int y, int centerX, int centerY)
	{
		matrixSetIdentity(mat, index);
		mat[index] = x;
		mat[index + 4] = y;
		if(centerX != 0)
		{
			mat[index + 2] = centerX - Fixed32.mul(x, centerX);
		}
		if(centerY != 0)
		{
			mat[index + 5] = centerY - Fixed32.mul(y, centerY);
		}
	}
	
	/**
	 * Get the number of contacts that have occurred.
	 */
	protected int getContactCount()
	{
		for(int i = MAX_CONTACT_COUNT - 1; i >= 0; i--)
		{
			if(this.start[i] != null)
			{
				return i + 1;
			}
		}
		return 0;
	}
	
	/**
	 * Get the actual contact count, counts each non-null element.
	 */
	private int getRawContactCount()
	{
		int c = 0;
		for(int i = 0; i < MAX_CONTACT_COUNT; i++)
		{
			if(this.start[i] != null)
			{
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Get a created contact.
	 * @param index The contact to get; 1 would be the first finger, 2 would be the second finger.
	 * @return The contact if it exists or null if it does not or the index is no valid.
	 */
	protected XYPoint getContact(int index)
	{
		if(index > 0 && index <= MAX_CONTACT_COUNT)
		{
			return this.start[index - 1];
		}
		return null;
	}
}
