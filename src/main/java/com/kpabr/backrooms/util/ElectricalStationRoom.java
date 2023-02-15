package com.kpabr.backrooms.util;

import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.util.math.Direction;

public class ElectricalStationRoom {
	public int eastWallX, westWallX, southWallZ, northWallZ;
	public boolean eastHallway, southHallway;
	public ElectricalStationRoom(int floor, int startX, int startZ, long seed) {
		Random random = new Random(seed + MathHelper.hashCode(startX, startZ, floor));
		int temporaryX = random.nextInt(10)+3;
		int temporaryZ = random.nextInt(10)+3;
		this.eastWallX = Math.max(temporaryX + 2, temporaryX + random.nextInt(14 - temporaryX));
		this.westWallX = Math.min(temporaryX - 2, temporaryX - random.nextInt(temporaryX - 1));
		this.southWallZ = Math.max(temporaryZ + 2, temporaryZ + random.nextInt(14 - temporaryZ));
		this.northWallZ = Math.min(temporaryZ - 2, temporaryZ - random.nextInt(temporaryZ - 1));
		this.eastHallway = random.nextInt(3) < 2;
		this.southHallway = random.nextInt(3) < 2;
	}

	public ElectricalStationRoom() {}

	public void setValues(int eastX, int westX, int southZ, int northZ) {
		this.eastWallX = eastX;
		this.westWallX = westX;
		this.southWallZ = southZ;
		this.northWallZ = northZ;
	}
	public void setValues(boolean east, boolean south) {
		this.eastHallway = east;
		this.southHallway = south;
	}
	public void setValues(int eastX, int westX, int southZ, int northZ, boolean east, boolean south) {
		this.eastWallX = eastX;
		this.westWallX = westX;
		this.southWallZ = southZ;
		this.northWallZ = northZ;
		this.eastHallway = east;
		this.southHallway = south;
	}

	public static ElectricalStationRoom hallwayBetween(ElectricalStationRoom room1, ElectricalStationRoom room2, Direction direction){
		ElectricalStationRoom result = new ElectricalStationRoom();
		if(direction==Direction.EAST||direction==Direction.WEST){
			if(room1.northWallZ + 1 < room2.southWallZ && room2.northWallZ + 1 < room1.southWallZ){
				final int middleResult = middle(room1.southWallZ, room1.northWallZ, room2.southWallZ, room2.northWallZ);
				if(direction==Direction.WEST){
					result.setValues(room1.westWallX - 1,0,middleResult + 1,middleResult - 1);
				}
				else{
					result.setValues(15,room1.eastWallX + 1,middleResult + 1,middleResult - 1);
				}
			}
			else {
				result.setValues(0, 15, 0, 15);
			}
		}
		else{
			if(room1.westWallX + 1 < room2.eastWallX && room2.westWallX + 1 < room1.eastWallX){
				final int middleResult = middle(room1.eastWallX, room1.westWallX, room2.eastWallX, room2.westWallX);
				if(direction==Direction.NORTH){
					result.setValues(middleResult + 1,middleResult - 1,room1.northWallZ - 1,0);
				}
				else{
					result.setValues(middleResult + 1,middleResult - 1,15,room1.southWallZ + 1);
				}
			}
			else {
				result.setValues(0, 15, 0, 15);
			}
		}
		if((direction==Direction.EAST && !room1.eastHallway) || (direction==Direction.WEST && !room2.eastHallway) || (direction==Direction.SOUTH && !room1.southHallway) || (direction==Direction.NORTH && !room2.southHallway)){
			result.setValues(0, 15, 0, 15);
		}
		return result;
	}
	private static int middle(int room1wall1, int room1wall2, int room2wall1, int room2wall2){
		return Math.round((Math.min(room1wall1, room2wall1) + Math.max(room1wall2, room2wall2)) / 2);
	}
}