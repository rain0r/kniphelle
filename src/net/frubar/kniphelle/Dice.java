/**
 * 
 */
package net.frubar.kniphelle;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author rainer
 *
 */
public class Dice extends ImageView {
	public static final int TYPE_TOSSED = 0;
	public static final int TYPE_SEPARATED = 1;
	
	private int type 			= -1; // either "tossed" or "separated"
	private int value 			= 0; // the value of the dice
	private int order 			= 0; // the order of the dice
	private int dice_no 		= 0;
	private boolean selected 	= false; 
	
	public Dice(Context context) {
		super(context);
	}

	/**
	 * @return the type
	 */
	public int get_type() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void set_type(int type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public int get_value() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void set_value(int value) {
		this.value = value;
	}

	/**
	 * @return the order
	 */
	public int get_order() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void set_order(int order) {
		this.order = order;
	}
	
	public boolean is_selected() {
		return selected;
	}

	public void set_selected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the dice_no
	 */
	public int get_dice_no() {
		return dice_no;
	}

	/**
	 * @param dice_no the dice_no to set
	 */
	public void set_dice_no(int dice_no) {
		this.dice_no = dice_no;
	}
}
