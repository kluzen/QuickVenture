package com.quickventure.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {

	protected final double TARGET_FPS = 80.0;
	private final double OPTIMAL_TIME = 1 / TARGET_FPS; // 16.67ms per frame
	private double maxSpeed = 200;
	
	private int id;
	protected double x;		// Current x coordinate
	protected double y;		// Current y coordinate
	protected double nx;		// Next Frame x coordinate
	protected double ny;		// Next Frame y coordinate
	protected int height;
	protected int width;
	private double vx;		// Velocity x component
	private double vy;		// Velocity y component
	private double ax;		// Acceleration x component
	private double ay;		// Acceleration y component
	private boolean grounded;
	private Color color;
	private boolean limited; // Speed limit for guy but not for bullets
	private boolean crop;
	private BufferedImage image = null;
	private BufferedImage topImage = null;
	private BufferedImage botImage = null;
	private String type;
	private boolean gas;
//	private boolean yPGround;
//	private boolean yNGround;
//	private boolean xPGround;
//	private boolean xNGround;
	
	public GameObject(int id, double x, double y, int h, int w){
		this.id = id;
		this.x = x;
		this.y = y;
		this.nx = x;
		this.ny = y;
		this.height = h;
		this.width = w;
		this.vx = 0;
		this.vy = 0;
		this.ax = 0;
		this.ay = 0;
		this.color = Color.red;
		this.limited = true;
		this.crop = false;
		this.type = "normal";
		this.gas = false;
	}
	
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void setHeight(int h){
		this.height = h;
	}
	public void setWidth(int w){
		this.width = w;
	}
	public void setVX(double v){
		this.vx = v;
	}
	public void setVY(double v){
		this.vy = v;
	}
	public void setAX(double a){
		this.ax = a;
	}
	public void setAY(double a){
		this.ay = a;
	}
	public void setGrounded(boolean b){
		this.grounded = b;
	}
	public void setColor(Color c){
		this.color = c;
	}
	public void setLimited(boolean b){
		this.limited = b;
	}
	public void setImage(String s){
		try {
            image = ImageIO.read(new File("images/" + s));
            if(this.crop){
//            	image = image.getSubimage(0, 0, image.getWidth(), this.height);
            	topImage = image.getSubimage(0, 0, image.getWidth(), image.getHeight()/2);
            	botImage = image.getSubimage(0, image.getHeight()/2, image.getWidth(), image.getHeight()/2);
            }
        } catch (IOException e) {
        	System.out.println(s);
            e.printStackTrace();
        }
	}
	public void setMaxSpeed(double d){
		this.maxSpeed = d;
	}
	public void setCrop(boolean b){
		this.crop = b;
	}
	public void setType(String s){
		this.type = s;
	}
	public String getType(){
		return this.type;
	}
	public void setGas(boolean b){
		this.gas = b;
	}
	public boolean getGas(){
		return this.gas;
	}
	
	public int getId(){
		return this.id;
	}
	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}
	public double getNX(){
		return this.nx;
	}
	public double getNY(){
		return this.ny;
	}
	public int getHeight(){
		return this.height;
	}
	public int getWidth(){
		return this.width;
	}
	public double getVX(){
		return this.vx;
	}
	public double getVY(){
		return this.vy;
	}
	public double getAX(){
		return this.ax;
	}
	public double getAY(){
		return this.ay;
	}
	public boolean isGrounded(){
		return this.grounded;
	}
	
	public void draw(Graphics g, int xOffset){
		if(image == null){
			Color co = g.getColor();
			g.setColor(this.color);
			g.fillRect((int)this.x - xOffset, (int)this.y, this.width, this.height);
			g.setColor(co);
		}else{

//			g.drawImage(topImage, 100, 100, topImage.getWidth(), topImage.getHeight(), null);
//			g.drawImage(botImage, 200, 200, botImage.getWidth(), botImage.getHeight(), null);
			
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			
			if(!crop){
				g.drawImage(image, (int)this.x - xOffset, (int)this.y, this.width, this.height, null);
			}else{
				for(int i = (int)this.y; i < (int)this.y + this.height; i += imageHeight/2){
					BufferedImage temp = null;
					int toHeight = imageHeight/2;
					
					if(i == (int)this.y){ // Very top of ground, draw topImage
						temp = topImage;
					}else{
						temp = botImage;
					}
					
					if(i + toHeight > (int)this.y + this.height){
						toHeight = (int)this.y + this.height - i;
					}
					
					for(int j = (int)this.x; j < (int)this.x + this.width; j += imageWidth){
						
						int toWidth = imageWidth;
						
						if(j + toWidth > (int)this.x + this.width){
							toWidth = (int)this.x + this.width - j;
						}
						
						temp = temp.getSubimage(0, 0, toWidth, toHeight);
						g.drawImage(temp, j - xOffset, i, toWidth, toHeight, null);
						
					}
				}
			}
		}
	}
	public void getNewLocation(double delta){
		/*
		 * Delta is the percentage of 16.67ms that has occurred for this frame update
		 */
		double dt = delta * this.OPTIMAL_TIME;

//		System.out.println(this.y + ", " + this.vy + ", " + dt + ", " + this.ay + ", " + Math.pow(dt, 2));

		if(this.grounded){
			this.vy = 0;
			if(this.vx > 0){
				this.ax = -1000;
				if(this.vx + this.ax * dt < 0){
					this.vx = 0;
				}else{
					this.vx += this.ax * dt;
				}
			}else if(this.vx < 0){
				this.ax = 1000;
				if(this.vx + this.ax * dt > 0){
					this.vx = 0;
				}else{
					this.vx += this.ax * dt;
				}
			}else{
				this.ax = 0;
				this.vx = 0;
			}
		}else{
			this.vy += this.ay * dt;
		}
		
		if(this.limited){
			if(this.vx > this.maxSpeed){
				this.vx = this.maxSpeed;
			}else if(this.vx < -1*this.maxSpeed){
				this.vx = -1*this.maxSpeed;
			}
		}
		
		this.nx = this.x + this.vx * dt;// + 0.5 * this.ax * Math.pow(dt, 2); // xf = vo*(t*delta) + 1/2*a*(t*delta)^2
		this.ny = this.y + this.vy * dt;// + 0.5 * this.ay * Math.pow(dt, 2);
		
	}
	public void setNewLocation(double x, double y){
		if(x != -1) this.nx = x;
		if(y != -1) this.ny = y;
	}
	
	public void move(){
		this.x = this.nx;
		this.y = this.ny;
	}
	
	public boolean collides(GameObject go){
		return (go.getY()+go.getHeight() >= this.ny && go.getY() <= this.ny+this.height &&
				go.getX()+go.getWidth() >= this.nx && go.getX() <= this.nx+this.width);
	}
}
