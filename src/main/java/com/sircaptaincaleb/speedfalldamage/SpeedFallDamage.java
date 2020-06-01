package com.sircaptaincaleb.speedfalldamage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author SirCaptainCaleb
 */
@Mod(SpeedFallDamage.MODID)
public final class SpeedFallDamage {

	public static final String MODID = "speedfalldamage";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final Float neg1 = -1.0f;

	public SpeedFallDamage() {
//		LOGGER.debug("Hello from Speed Fall Damage!");
	}

	public static HashMap<UUID, Float> playerFallCounts = new HashMap<UUID, Float>();

	@Mod.EventBusSubscriber(modid = SpeedFallDamage.MODID, bus = EventBusSubscriber.Bus.FORGE)
	public static final class ForgeEventSubscriber {
		@SubscribeEvent
		public static void onLivingFall(LivingFallEvent event) {
			if (event.getEntity() != null && event.getEntity() instanceof PlayerEntity) {
				float h = heightFromYVelocity(event.getEntity().getMotion().getY());
				UUID playerUUID = event.getEntity().getUniqueID();

				/*
				 The following handles the client server disagreement on fall velocity, as the fall event fires twice,
				 once on the server and once on teh client. By the time the server recognizes that the player has
				 fallen, he has already stopped moving, so the server thinks he did not fall from a very high height.
				 This fixes that by utilizing the fact that the client event should fire first. This is not a perfect
				 solution, as rarely the server event fires first for some reason.
				 */

				float storedFallDistance = playerFallCounts.getOrDefault(playerUUID, neg1);
				if( storedFallDistance > -0.5){
					event.setDistance(storedFallDistance);
					playerFallCounts.put(playerUUID, neg1);
				}else {
					event.setDistance(h);
					playerFallCounts.put(playerUUID, h);
				}
			}
		}
	}

	public static float heightFromYVelocity(double yVelocity){

		if (yVelocity > 0){ return 0f; }
		double y1 = yVelocity;
		double y2 = y1*y1;
		double y3 = y2*y1;
		double y4 = y2*y2;
		double y5 = y4*y1;
		double y6 = y4*y2;
		double y7 = y6*y1;
		double h = -0.0305784237d*y7 - 0.1323526196d*y6 - 0.2828236621d*y5 + 0.1444384024d*y4 - 0.8062358857d*y3 + 6.617247118d*y2 + 0.6220960864d*y1 + 0.01623063534d;
		return (float) h;
	}
}