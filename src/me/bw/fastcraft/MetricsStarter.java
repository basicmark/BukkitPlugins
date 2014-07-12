package me.bw.fastcraft;

import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

public class MetricsStarter {
	public static void start(Plugin plugin){
		try {
			Metrics m = new Metrics(plugin);

			Graph autoUpdate = m.createGraph("Auto Update");
			if (FastCraft.config.getBoolean("autoUpdate.enabled")){
				autoUpdate.addPlotter(new MyPlotter("Enabled", 1));
			}else{
				autoUpdate.addPlotter(new MyPlotter("Disabled", 1));
			}

			Graph defaultEnabled = m.createGraph("FastCraft Default Enabled");
			if (FastCraft.config.getBoolean("fastCraftDefaultEnabled")){
				defaultEnabled.addPlotter(new MyPlotter("Enabled", 1));
			}else{
				defaultEnabled.addPlotter(new MyPlotter("Disabled", 1));
			}

			m.start();
		} catch (IOException e) {
			System.out.println("[FastCraft] Error submitting plugin metrics!");
		}
	}
}
class MyPlotter extends Plotter {
	int value;
	public MyPlotter(final String name, int value) {
		super(name);
		this.value = value;
	}
	@Override
	public int getValue() {
		return value;
	}
}