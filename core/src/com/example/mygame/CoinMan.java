package com.example.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
	private Texture[] man;
    private Texture dizzy;
    private int manState = 0;
    private int pause = 0;
    private float velocity = 0;
    private int manY = 0;
    private BitmapFont font;

    private int score = 0;
    private int gameState = 0;

    private Random random;

    private ArrayList<Integer> coinXs = new ArrayList<Integer>();
    private ArrayList<Integer> coinYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	private Texture coin;
	private int coinCount;

    private ArrayList<Integer> bombXs = new ArrayList<Integer>();
    private ArrayList<Integer> bombYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
    private Texture bomb;
    private int bombCount;


	@Override
	public void create() {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	private void makeCoin() {

		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	private void makeBomb() {

		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render() {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			//GAME IS LIVE

			//COIN
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangles.clear();

			for (int i = 0; i < coinXs.size(); i++) {

				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 12);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			//BOMB
			if (bombCount < 100) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();

			for (int i = 0; i < bombXs.size(); i++) {

				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 16);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			if (Gdx.input.justTouched()) {

				velocity = -15;
			}

			if (pause < 2) {
				pause++;
			} else {
				pause = 0;

				if (manState < 3) {
					manState++;
				} else {

					manState = 0;
				}
			}

            float gravity = 0.4f;
            velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {

				manY = 0;
			}


		} else if (gameState == 0) {
			//Waiting to start

			if (Gdx.input.justTouched()) {
				gameState = 1;

			}
		} else if (gameState == 2) {
			//GAME IS OVER
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;

			}
		}

            if(gameState == 2){

                batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

            }else {
                batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
            }
        Rectangle manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

			for (int i = 0; i < coinRectangles.size(); i++) {

				if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
					score++;

					coinRectangles.remove(i);
					coinXs.remove(i);
					coinYs.remove(i);
					break;
				}
			}
			for (int i = 0; i < bombRectangles.size(); i++) {

				if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
					Gdx.app.log("BOMB!", "COLLISION");
					gameState = 2;

				}
			}

			font.draw(batch, String.valueOf(score), 100, 200);

			batch.end();
		}

		@Override
		public void dispose () {
			batch.dispose();

		}
	}



