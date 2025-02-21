package com.elikill58.negativity.fabric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.inventory.FabricInventory;
import com.elikill58.negativity.fabric.impl.item.FabricItemBuilder;
import com.elikill58.negativity.fabric.impl.item.FabricItemRegistrar;
import com.elikill58.negativity.fabric.impl.location.FabricWorld;
import com.elikill58.negativity.fabric.impl.plugin.FabricExternalPlugin;
import com.elikill58.negativity.fabric.nms.Fabric_1_21;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.Resource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class FabricAdapter extends Adapter {

	private final LoggerAdapter logger;
	private final FabricNegativity plugin;
	private Configuration config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(FabricNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final FabricItemRegistrar itemRegistrar;
	private final Version serverVersion;
	private final Scheduler scheduler;
	private final VersionAdapter<?> versionAdapter;
	
	public FabricAdapter(FabricNegativity sn, Logger logger) {
		this.plugin = sn;
		this.logger = new Slf4jLoggerAdapter(logger);
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(sn.getDataFolder().resolve("lang"), "Negativity", "CheatHover");
		this.itemRegistrar = new FabricItemRegistrar();
		this.serverVersion = Version.getVersionByProtocolID(MinecraftVersion.CURRENT.getProtocolVersion());
		this.scheduler = new FabricScheduler();
		this.versionAdapter = new Fabric_1_21();
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.FABRIC;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder().toFile();
	}

	@Override
	public void debug(String msg) {
		if(getConfig().getBoolean("debug", false))
			logger.info(msg);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
	}

	@Override
	public String getVersion() {
		return MinecraftVersion.CURRENT.getName();
	}
	
	@Override
	public Version getServerVersion() {
		return this.serverVersion;
	}
	
	@Override
	public String getPluginVersion() {
		return FabricLoader.getInstance().getModContainer("negativity")
			.map(ModContainer::getMetadata).map(ModMetadata::getVersion)
			.map(net.fabricmc.loader.api.Version::getFriendlyString)
			.orElse("?");
	}

	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		plugin.getServer().getCommandManager().executeWithPrefix(plugin.getServer().getCommandSource(), cmd);
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		return FabricNegativity.getOnlinePlayers().stream().map(ServerPlayerEntity::getUuid).collect(Collectors.toList());
	}

	@Override
	public double[] getTPS() {
		long[] ltps = plugin.getServer().getTickTimes();
		double[] tps = new double[4];
		for(int i = 0; i < (ltps.length > 3 ? 3 : ltps.length); i++)
			tps[i] = ltps[i];
		return tps;
	}

	@Override
	public double getLastTPS() {
		return getTPS()[0];
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		Text t = Text.of(message);
		List<Text> texts = t.getWithStyle(t.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(hover))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
		ServerPlayerEntity pe = ((ServerPlayerEntity) p.getDefault());
		texts.forEach(text -> pe.sendMessage(text, false));
	}

	@Override
	public List<Player> getOnlinePlayers() {
		return FabricNegativity.getOnlinePlayers().stream().map(FabricEntityManager::getPlayer).collect(Collectors.toList());
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new FabricItemBuilder(type);
	}

	@Override
	public ItemBuilder createItemBuilder(ItemStack item) {
		return new FabricItemBuilder(item);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new FabricItemBuilder(itemRegistrar.get(type.split(":")[0]));
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new FabricItemBuilder(owner);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		return new FabricItemBuilder(owner);
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new FabricInventory(inventoryName, size, holder);
	}

	@Override
	public @Nullable Player getPlayer(String name) {
		return FabricEntityManager.getPlayer(plugin.getServer().getPlayerManager().getPlayer(name));
	}

	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return FabricEntityManager.getPlayer(plugin.getServer().getPlayerManager().getPlayer(uuid));
	}

	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		return getPlayer(name); // TODO add offline players
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		return getPlayer(uuid);
	}

	@Override
	public boolean hasPlugin(String name) {
		name = name.toLowerCase(Locale.ROOT);
		try {
			return plugin.getServer().getResourceManager().getResource(Identifier.of(name)).isPresent();
		} catch (InvalidIdentifierException e) {
			return false;
		}
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		name = name.toLowerCase(Locale.ROOT);
		try {
			Optional<Resource> opt = plugin.getServer().getResourceManager().getResource(Identifier.of(name));
			if(opt.isPresent())
				return new FabricExternalPlugin(name, opt.get());
		} catch (InvalidIdentifierException ignore) {
		}
		return null;
	}
	
	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public void runSync(Runnable call) {
		this.plugin.getServer().submit(call);
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public boolean canSendStats() {
		return false;
	}
	
	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		/*ServerPlayNetworking.registerGlobalReceiver(CustomPayload.id(channel), (ctx, p) -> {
			Player player = FabricEntityManager.getPlayer(p);
			byte[] data;
			if (buf.hasArray()) {
				data = buf.array();
			} else {
				int length = buf.readableBytes();
				data = new byte[length];
				buf.getBytes(buf.readerIndex(), data, 0, length);
			}
			event.accept(player, data);
		});*/
	}
	
	@Override
	public void broadcastMessage(String message) {
		getOnlinePlayers().forEach(p -> p.sendMessage(message));
	}
	
	@Override
	public VersionAdapter<?> getVersionAdapter() {
		return versionAdapter;
	}
	
	@Override
	public List<String> getAllPlugins() {
		return new ArrayList<>(plugin.getServer().getResourceManager().getAllNamespaces());
	}

	@Override
	public World getServerWorld(Player p) {
		ServerPlayerEntity se = plugin.getServer().getPlayerManager().getPlayer(p.getUniqueId());
		return World.getWorld(se.getWorld().asString(), (a) -> new FabricWorld(se.getWorld()));
	}
}
