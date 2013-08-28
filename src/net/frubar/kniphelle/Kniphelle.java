package net.frubar.kniphelle;

import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Kniphelle extends Activity implements OnClickListener {
	private RelativeLayout root = null;
	private Dice[] dices = new Dice[Kniphelle.DICE_COUNT];
	private int toss_counter = 0; // 3 = game over
	public int dice_border_len = 0; // Size of the Dice-Image

	public static final String TAG = "MainActivity"; // Tag for Debugging
	public static final int TAG_BTN_SEPARATE = 0; // Tag for onClick
	public static final int TAG_BTN_TOSS = 1; // Tag for onClick
	public static final int TAG_BTN_NEW_ROUND = 2; // Tag for onClick
	public static final int TAG_DICE = 3; // Tag for onClick
	public static final int DICE_COUNT = 5; // Number of Dices in the Game
	public static final int MAX_DICE_VALUE = 6; // Highest value of a Dice

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.activity_main);

		this.root = (RelativeLayout) findViewById(R.id.root);
		this.calculate_border_len();
		this.set_onclick_listener();
		this.start_game();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void start_game() {
		this.initialize_dices();
		this.toss_counter = 0;
		this.toss();
		this.paint_ui();
	}

	private void paint_dices() {
		/**
		 * tmp views to align the dices
		 */
		ImageView tmp_row_one = new ImageView(this);
		ImageView tmp_row_two = new ImageView(this);

		tmp_row_one.setId(-1);
		tmp_row_one.setId(-2);

		RelativeLayout.LayoutParams tmp_row_one_p = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tmp_row_one_p.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		RelativeLayout.LayoutParams tmp_row_two_p = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		tmp_row_two_p.addRule(RelativeLayout.BELOW, tmp_row_one.getId());
		tmp_row_two_p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		this.root.addView(tmp_row_one, tmp_row_one_p);
		this.root.addView(tmp_row_two, tmp_row_two_p);

		Dice tmp_dice_row_one = null;
		Dice tmp_dice_row_two = null;

		int tmp_id_row_one = 10;
		int tmp_id_row_two = 20;

		/**
		 * iterate over tossed dices
		 */
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			if (this.dices[i].get_type() == Dice.TYPE_TOSSED) {
				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);

				this.dices[i].setImageDrawable(this
						.load_dice_image(this.dices[i].get_value()));
				this.dices[i].setAdjustViewBounds(true);
				this.dices[i].setMaxHeight(this.dice_border_len);
				this.dices[i].setMaxWidth(this.dice_border_len);

				// Display in Row 1
				if (tmp_dice_row_one == null) {
					p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					p.addRule(RelativeLayout.RIGHT_OF, tmp_dice_row_one.getId());
				}
				tmp_id_row_one++;
				this.dices[i].setId(tmp_id_row_one);
				tmp_dice_row_one = this.dices[i];

				this.dices[i].setTag(Kniphelle.TAG_DICE);
				this.dices[i].setOnClickListener(this);
				this.root.addView(this.dices[i], p);
			}
		}

		/**
		 * iterate over separated dices
		 */
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			if (this.dices[i].get_type() == Dice.TYPE_SEPARATED) {
				RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);

				this.dices[i].setImageDrawable(this
						.load_dice_image(this.dices[i].get_value()));
				this.dices[i].setAdjustViewBounds(true);
				this.dices[i].setMaxHeight(this.dice_border_len);
				this.dices[i].setMaxWidth(this.dice_border_len);

				// Display in Row 2
				if (tmp_dice_row_two == null) {
					p.addRule(RelativeLayout.ALIGN_LEFT);
					p.addRule(RelativeLayout.BELOW, tmp_dice_row_one.getId());
				} else {
					p.addRule(RelativeLayout.RIGHT_OF, tmp_dice_row_two.getId());
					p.addRule(RelativeLayout.BELOW, tmp_dice_row_one.getId());
				}
				tmp_id_row_two++;
				this.dices[i].setId(tmp_id_row_two);
				tmp_dice_row_two = this.dices[i];

				this.dices[i].setTag(Kniphelle.TAG_DICE);
				this.dices[i].setOnClickListener(this);
				this.root.addView(this.dices[i], p);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		int id = v.getId();
		Log.d(TAG, "" + tag);
		
		switch (tag) {
		case Kniphelle.TAG_DICE:
			// click on a dice - select it
			Dice tmp_dice = (Dice) findViewById(id);
			Log.d(TAG,
					"Dice id: " + id + " Dice value: " + tmp_dice.get_value()
							+ " dice number: " + tmp_dice.get_dice_no());
			this.select_dice(tmp_dice.get_dice_no());
			break;
		case Kniphelle.TAG_BTN_SEPARATE:
			// separate selected dices
			this.remove_dices();
			this.separate_dices();
			this.paint_ui();
			break;
		case Kniphelle.TAG_BTN_NEW_ROUND:
			this.remove_dices();
			this.start_game();
			break;
		case Kniphelle.TAG_BTN_TOSS:
			this.remove_dices();
			this.toss();
			this.paint_ui();
			break;
		default:
			break;
		}
	}

	/**
	 * toss
	 * 
	 * @param dices
	 */
	private void toss() {
		if (this.toss_counter >= 3) {
			Log.d(TAG, "GAME OVER");
		} else {
			for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
				if (this.dices[i].get_type() == Dice.TYPE_TOSSED) {
					Random generator = new Random();
					this.dices[i].set_value(generator
							.nextInt(Kniphelle.MAX_DICE_VALUE) + 1);
				}
			}
			this.log_dices();
			this.toss_counter++;
		}
	}

	private void log_dices() {
		String tmp_tossed = "";
		String tmp_separataed = "";

		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			if (this.dices[i].get_type() == Dice.TYPE_TOSSED) {
				tmp_tossed += "[" + this.dices[i].get_value() + "] ";
			}
			if (this.dices[i].get_type() == Dice.TYPE_SEPARATED) {
				tmp_separataed += "[" + this.dices[i].get_value() + "] ";
			}
		}

		Log.d(TAG, "====================TOSSED DICES====================");
		Log.d(TAG, tmp_tossed);
		Log.d(TAG, "====================SEPARATED DICES====================");
		Log.d(TAG, tmp_separataed);
	}

	/**
	 * search for the dice with dice_number an modify the alpha of the image
	 * 
	 * @param dice_number
	 */
	@SuppressWarnings("deprecation")
	private void select_dice(int dice_number) {
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			if (this.dices[i].get_dice_no() == dice_number) {
				Dice dice = this.dices[i];

				if (dice.is_selected()) {
					dice.setAlpha(255);
					dice.set_selected(false);
					this.toggle_separate_btn(false);
				} else {
					dice.setAlpha(75);
					dice.set_selected(true);
					this.toggle_separate_btn(true);
				}
			}
		}
	}

	private Drawable load_dice_image(int value) {
		Log.d(TAG, "load_dice_image(" + value + ")");
		return getResources().getDrawable(
				getResources().getIdentifier("dice_" + value, "drawable",
						getPackageName()));
	}

	@SuppressWarnings("deprecation")
	private void separate_dices() {
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			if (this.dices[i].is_selected()) {
				this.dices[i].set_selected(false);
				this.toggle_separate_btn(false);
				this.dices[i].setAlpha(255);
				this.dices[i].set_type(Dice.TYPE_SEPARATED);
			}
		}
	}

	private void initialize_dices() {
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			this.dices[i] = new Dice(this);
			this.dices[i].set_type(Dice.TYPE_TOSSED);
			this.dices[i].set_dice_no(1000 + i); // unique dice number
		}
	}

	private void paint_toss_counter() {
		Resources res = getResources();

		String text = String.format(res.getString(R.string.toss_counter),
				this.toss_counter);
		Log.d(TAG, text);
		TextView toss_counter_tv = (TextView) findViewById(R.id.tv_toss_counter);
		toss_counter_tv.setText(text);
	}

	private void paint_ui() {
		this.paint_dices();
		this.paint_toss_counter();
	}

	@SuppressWarnings("deprecation")
	private void calculate_border_len() {
		Display display = getWindowManager().getDefaultDisplay();
		this.dice_border_len = display.getWidth() / 5;
	}

	private void set_onclick_listener() {
		Button separate_dices = (Button) findViewById(R.id.btn_separate);
		separate_dices.setTag(Kniphelle.TAG_BTN_SEPARATE);
		separate_dices.setOnClickListener(this);

		Button toss = (Button) findViewById(R.id.btn_toss);
		toss.setTag(Kniphelle.TAG_BTN_TOSS);
		toss.setOnClickListener(this);

		Button new_round = (Button) findViewById(R.id.btn_new_round);
		new_round.setTag(Kniphelle.TAG_BTN_NEW_ROUND);
		new_round.setOnClickListener(this);
	}
	
	private void remove_dices() {
		for (int i = 0; i < Kniphelle.DICE_COUNT; i++) {
			this.root.removeView(this.dices[i]);
		}
	}
	
	private void toggle_separate_btn(boolean active) {
		// disable separating dices after the second toss
		if (this.toss_counter < 3) {
			Button separate_btn = (Button) findViewById(R.id.btn_separate);
			separate_btn.setEnabled(active);
		}
	}
}