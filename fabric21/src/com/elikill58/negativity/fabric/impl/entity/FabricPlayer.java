package com.elikill58.negativity.fabric.impl.entity;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.AbstractPlayer;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.fabric.impl.FabricPotionEffectType;
import com.elikill58.negativity.fabric.impl.inventory.FabricInventory;
import com.elikill58.negativity.fabric.impl.inventory.FabricPlayerInventory;
import com.elikill58.negativity.fabric.impl.inventory.NegativityScreenHandlerFactory;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;
import com.elikill58.negativity.fabric.impl.location.FabricLocation;
import com.elikill58.negativity.fabric.payload.BungeecordSendToServerMessagePayload;
import com.elikill58.negativity.fabric.payload.FMLMessagePayload;
import com.elikill58.negativity.fabric.payload.NegativityMessagePayload;
import com.elikill58.negativity.fabric.utils.LocationUtils;
import com.elikill58.negativity.fabric.utils.Utils;
import com.elikill58.negativity.universal.Adapter;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class FabricPlayer extends AbstractPlayer implements Player {

	private ServerPlayerEntity entity;

	public FabricPlayer(ServerPlayerEntity p) {
		this.entity = p;
		this.location = FabricLocation.toCommon(p.getWorld(), p.getPos());
		init();
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUuid();
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(Text.of(msg), false);
	}

	@Override
	public boolean isOp() {
		return entity.hasPermissionLevel(4);
	}

	@Override
	public boolean hasElytra() {
		return false;
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this.entity, this.entity.getWorld(), ((net.minecraft.entity.Entity) entity.getDefault()).getPos());
	}

	@Override
	public float getWalkSpeed() {
		return entity.getAbilities().getWalkSpeed();
	}

	@Override
	public double getHealth() {
		return entity.getHealth();
	}
	
	@Override
	public double getMaxHealth() {
		return entity.getMaxHealth();
	}
	
	@Override
	public void setHealth(double health) {
		entity.setHealth((float) health);
	}

	@Override
	public float getFallDistance() {
		return entity.fallDistance;
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.interactionManager.getGameMode().name());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		entity.changeGameMode(net.minecraft.world.GameMode.byName(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		
	}

	@Override
	public Location getLocation() {
		return FabricLocation.toCommon(entity.getWorld(), entity.getPos());
	}

	@Override
	public int getPing() {
		return entity.networkHandler.getLatency();
	}

	@Override
	public String getName() {
		return entity.getName().getString();
	}

	@Override
	public boolean hasPermission(String perm) {
		return isOp();
	}

	@Override
	public void kick(String reason) {
		entity.networkHandler.disconnect(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return entity.experienceLevel;
	}
	
	@Override
	public int getFoodLevel() {
		return entity.getHungerManager().getFoodLevel();
	}
	
	@Override
	public void setFoodLevel(int foodlevel) {
		entity.getHungerManager().setFoodLevel(foodlevel);
	}

	@Override
	public boolean getAllowFlight() {
		return entity.getAbilities().allowFlying;
	}

	@Override
	public Entity getVehicle() {
		return FabricEntityManager.getEntity(entity.getVehicle());
	}
	
	@Override
	public ItemStack getItemInHand() {
		return new FabricItemStack(entity.getActiveItem());
	}

	@Override
	public boolean isFlying() {
		return entity.getAbilities().flying;
	}
	
	@Override
	public void setFlying(boolean b) {
		entity.getAbilities().flying = b;
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		ServerPlayNetworking.send(entity, channelId.toLowerCase().contains("fml") ? new FMLMessagePayload(writeMessage) : new NegativityMessagePayload(writeMessage));
	}

	@Override
	public boolean isSleeping() {
		return entity.isSleeping();
	}

	@Override
	public boolean isSneaking() {
		return entity.isSneaking();
	}

	@Override
	public boolean isUsingRiptide() {
		return entity.isUsingRiptide();
	}
	
	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(entity);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.hasStatusEffect(FabricPotionEffectType.getEffect(type));
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		return entity.getStatusEffects().stream().map(effect -> new PotionEffect(FabricPotionEffectType.getEffect(effect.getEffectType()), effect.getDuration(), effect.getAmplifier())).collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		StatusEffectInstance effect = entity.getStatusEffect(FabricPotionEffectType.getEffect(type));
		return effect == null ? Optional.empty() : Optional.of(new PotionEffect(type, effect.getDuration(), effect.getAmplifier()));
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.addStatusEffect(new StatusEffectInstance(FabricPotionEffectType.getEffect(type), duration, amplifier));
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.removeStatusEffect(FabricPotionEffectType.getEffect(type));
	}

	@Override
	public String getIP() {
		return entity.getIp();
	}

	@Override
	public boolean isOnline() {
		return !entity.isDisconnected();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.isSneaking();
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return entity.isSprinting();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@Override
	public void teleport(Location loc) {
		entity.teleport(loc.getX(), loc.getY(), loc.getZ(), false);
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.getVehicle() != null;
	}

	@Override
	public float getFlySpeed() {
		return entity.getAbilities().getFlySpeed() * 2.0f;
	}

	@Override
	public void setSprinting(boolean b) {
		entity.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		// TODO manage near entity
		return new ArrayList<>();
	}

	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		return loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
	}

	@Override
	public ItemStack getItemInOffHand() {
		return new FabricItemStack(entity.getOffHandStack());
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public PlayerInventory getInventory() {
		return new FabricPlayerInventory(entity);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.currentScreenHandler != entity.playerScreenHandler;
	}

	@Override
	public Inventory getOpenInventory() {
		if (entity.currentScreenHandler == entity.playerScreenHandler) {
			return null;
		}
		
		return new FabricInventory(entity.currentScreenHandler);
	}

	@Override
	public void openInventory(Inventory inv) {
		Object o = inv.getDefault();
		if (o instanceof ScreenHandler screenHandler) {
			entity.openHandledScreen(new NegativityScreenHandlerFactory((syncId, inventory, player) -> screenHandler, Text.of(inv.getInventoryName())));
		} else if (inv instanceof FabricInventory fabricInventory) {
			entity.openHandledScreen(new NegativityScreenHandlerFactory(fabricInventory.getFactory(), Text.of(inv.getInventoryName())));
		//} else if (inv instanceof FabricPlayerInventory playerInventory) { // TODO open proper PlayerInventory implementation
		} else if (o != null) {
			Adapter.getAdapter().getLogger().warn("Tried opening unsupported inventory " + o.getClass().getName());
		} else {
			Adapter.getAdapter().getLogger().warn("Tried opening inventory with null platform value from " + inv);
		}
	}

	@Override
	public void closeInventory() {
		entity.closeHandledScreen();
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.getAbilities().allowFlying = b;
	}

	@Override
	public void setVanished(boolean vanished) {
		/*entity.offer(Keys.VANISH, vanished);
		if (vanished) {
			entity.offer(Keys.VANISH_IGNORES_COLLISION, true);
			entity.offer(Keys.VANISH_PREVENTS_TARGETING, true);
		}*/
		// TODO add vanish feature
	}
	
	@Override
	public InetSocketAddress getAddress() {
		SocketAddress address = entity.networkHandler.getConnectionAddress();
		if (address instanceof InetSocketAddress inetAddress) {
			return inetAddress;
		}
		return null;
	}
	
	@Override
	public void sendToServer(String serverName) {
		ServerPlayNetworking.send(entity, new BungeecordSendToServerMessagePayload(serverName));
	}
	
	@Override
	public String getServerName() {
		return "FabricServer"; // TODO check if fabric can have a server name
	}
	
	@Override
	public int getEntityId() {
		return entity.getId();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		Box box = entity.getBoundingBox();
		if(box == null)
			return null;
		return new BoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}

	@Override
	public Object getDefault() {
		return entity;
	}

	@Override
	public Vector getTheoricVelocity() {
		Vec3d vel = entity.getVelocity();
		return new Vector(vel.x, vel.y, vel.z);
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vec3d(vel.getX(), vel.getY(), vel.getZ()));
	}
}
