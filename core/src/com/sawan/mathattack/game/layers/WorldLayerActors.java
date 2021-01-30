/**
 * File name:	WorldLayerActors.java
 * Version:		1.0
 * Date:		20/3/2015 10:05:12
 * Author:		Itop1
 * Copyright:	Copyright 200X Itop1
 *
 *				This file is part of Math Attack.
 *
 *				Math Attack is free software: you can redistribute it 
 *				and/or modify it under the terms of the GNU General
 *				Public License as published by the Free Software 
 *				Foundation, either version 3 of the License, 
 *				or (at your option) any later version.
 *
 *				Math Attack is distributed in the hope that it will 
 *				be useful, but WITHOUT ANY WARRANTY; without even 
 *				the implied warranty of MERCHANTABILITY or FITNESS 
 *				FOR A PARTICULAR PURPOSE. See the GNU General Public
 *			    License for more details.
 *
 *				You should have received a copy of the GNU General 
 *				Public License along with Math Attack. If not, see 
 *				http://www.gnu.org/licenses/.
 */
package com.sawan.mathattack.game.layers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.sawan.mathattack.asset.BlueMonsterAssets;
import com.sawan.mathattack.asset.HeroAssests;
import com.sawan.mathattack.asset.UIAssets;
import com.sawan.mathattack.collision.CollisionDetector;
import com.sawan.mathattack.game.GameState;
import com.sawan.mathattack.game.managers.MAGameManager;
import com.sawan.mathattack.models.ammunition.Bullet;
import com.sawan.mathattack.models.characters.Hero;
import com.sawan.mathattack.models.characters.enemies.BlueMonster;
import com.sawan.mathattack.scene2d.AbstractWorldScene2d;
import com.sawan.mathattack.settings.AppSettings;

/**
 * @author Sawan
 *
 */
public class WorldLayerActors extends AbstractWorldScene2d {
	
	private MAGameManager gameManager;
	public Hero hero;
	public ArrayList<BlueMonster> enemies;
	public ArrayList<Bullet> bullets;
	
	protected final static int NUM_ENEMIES = 20;
	
	
	public WorldLayerActors(MAGameManager gameManager, float posX, float posY, float worldWidth, float worldHeight) {
		super(posX, posY, worldWidth, worldHeight);
		this.gameManager =  gameManager;
		setUpHero();
		setUpEnemies();
		
	}
	
	public void setUpHero() {
		hero = new Hero(gameManager.worldLayer_background.SOIL_WIDHT, gameManager.worldLayer_background.SOIL_HEIGHT, true);
		bullets = new ArrayList<Bullet>();
		
		hero.setY(gameManager.worldLayer_background.SOIL_HEIGHT * AppSettings.getWorldSizeRatio());
		hero.setX(0f * AppSettings.getWorldPositionXRatio());
		hero.setAnimation(HeroAssests.hero_standing, true, true);
		addActor(hero);
	}
	
	public void setUpEnemies() {
		enemies = new ArrayList<BlueMonster>();
		Random rnd = new Random();
		
		for (int i = 0; i < NUM_ENEMIES; i++) {
			BlueMonster current_monster = new BlueMonster(gameManager.worldLayer_background.SOIL_WIDHT, gameManager.worldLayer_background.SOIL_HEIGHT, true);
			
			float posY = gameManager.worldLayer_background.SOIL_HEIGHT * AppSettings.getWorldSizeRatio();
			current_monster.setY(posY);
			current_monster.setX(gameManager.getStage().getWidth() + (i * (current_monster.getWidth() + 100)));
			
			current_monster.setAnimation(BlueMonsterAssets.monster_walking, true, true);
			
			float rndSpeed = rnd.nextInt(100) + 20;
			current_monster.startMoving(gameManager.getStage().getWidth(), rndSpeed, true, false);
			
			enemies.add(current_monster);
			addActor(current_monster);
			
			
		}
	}
	
	public void killHero() {
		if (hero.getLifes() <= 0) {
			hero.setAlive(false);
			hero.setAnimationMomentary(HeroAssests.hero_faint, true, null, true, true);
			if (hero.isAnimationActive()) {
				gameManager.setGameState(GameState.GAME_OVER);
			}
		}
	}
	
	public boolean isHeroAlive() {
		 if (hero.isAlive()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void checkCollision(Hero hero, ArrayList<BlueMonster> enemies) {
		for (Iterator<BlueMonster> iterator = enemies.iterator(); iterator.hasNext();) {
			BlueMonster enemy = (BlueMonster) iterator.next();
			if (CollisionDetector.isActorsCollide(hero, enemy) && enemy.isAlive() && hero.getLifes() > 0) {
			//	System.out.println(enemies.indexOf(enemy));
				enemy.setAlive(false);
				iterator.remove();
				enemies.remove(enemy);
				removeActor(enemy);
				
				hero.setLifes(hero.getLifes() - 1);
				if (!isHeroAlive()) {
					killHero();
				}
				hero.setAnimationMomentary(HeroAssests.hero_dizzy, true, HeroAssests.hero_standing, true, false);
				
				hero.setLost_life(true);				
			}
		}
	}

	public void hitEnemy(ArrayList<Bullet> bullets, ArrayList<BlueMonster> enemies) {
		for (Iterator<Bullet> iterator_bullets = bullets.iterator(); iterator_bullets.hasNext();) {
			Bullet bullet = (Bullet) iterator_bullets.next();
			
			for (Iterator<BlueMonster> iterator_enemies = enemies.iterator(); iterator_enemies.hasNext();) {
				BlueMonster blueMonster = (BlueMonster) iterator_enemies.next();
				
				if (CollisionDetector.isActorsCollide(bullet, blueMonster)) {
					iterator_bullets.remove();
					iterator_enemies.remove();
					bullets.remove(bullet);
					enemies.remove(blueMonster);
					removeActor(bullet);
					removeActor(blueMonster);
				}
			}
			
		}
	}
	
	public void addBullet() {
		final Bullet bullet = new Bullet(25f, 25f, true);
		bullet.setX(hero.getX() + hero.getWidth());
		bullet.setY(gameManager.worldLayer_background.SOIL_HEIGHT);
		bullet.setTextureRegion(UIAssets.image_level_star, true);
		
		bullets.add(bullet);
		
		bullet.startMoving(gameManager.getStage().getWidth(), 125f, true);
		
		addActor(bullet);
	}
	
}