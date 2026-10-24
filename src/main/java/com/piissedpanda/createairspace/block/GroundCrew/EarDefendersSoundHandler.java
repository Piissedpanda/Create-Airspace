package com.piissedpanda.createairspace.block.GroundCrew;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CreateAirspace;
import com.piissedpanda.createairspace.foundation.config.CAConfigs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

@EventBusSubscriber(modid = CreateAirspace.MODID, value = Dist.CLIENT)
public class EarDefendersSoundHandler {

	// Voices stay clear, later radio calls should come through the headset
	private static final Set<SoundSource> UNCHANGED = Set.of(SoundSource.MASTER, SoundSource.MUSIC,
			SoundSource.VOICE);

	@SubscribeEvent
	public static void onPlaySound(PlaySoundEvent event) {
		SoundInstance sound = event.getSound();
		if (sound == null || UNCHANGED.contains(sound.getSource()))
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || !EarDefendersItem.isWearing(mc.player))
			return;
		event.setSound(new MuffledSound(sound, CAConfigs.client().earDefendersVolume.getF()));
	}

	// Plays the original sound with less volume, ticking along if it ticks
	private record MuffledSound(SoundInstance sound, float factor) implements TickableSoundInstance {

		@Override
		public void tick() {
			if (sound instanceof TickableSoundInstance tickable)
				tickable.tick();
		}

		@Override
		public boolean isStopped() {
			return sound instanceof TickableSoundInstance tickable && tickable.isStopped();
		}

		@Override
		public float getVolume() {
			return sound.getVolume() * factor;
		}

		@Override
		public ResourceLocation getLocation() {
			return sound.getLocation();
		}

		@Override
		@Nullable
		public WeighedSoundEvents resolve(SoundManager manager) {
			return sound.resolve(manager);
		}

		@Override
		public Sound getSound() {
			return sound.getSound();
		}

		@Override
		public SoundSource getSource() {
			return sound.getSource();
		}

		@Override
		public boolean isLooping() {
			return sound.isLooping();
		}

		@Override
		public boolean isRelative() {
			return sound.isRelative();
		}

		@Override
		public int getDelay() {
			return sound.getDelay();
		}

		@Override
		public float getPitch() {
			return sound.getPitch();
		}

		@Override
		public double getX() {
			return sound.getX();
		}

		@Override
		public double getY() {
			return sound.getY();
		}

		@Override
		public double getZ() {
			return sound.getZ();
		}

		@Override
		public Attenuation getAttenuation() {
			return sound.getAttenuation();
		}

		@Override
		public boolean canStartSilent() {
			return sound.canStartSilent();
		}

		@Override
		public boolean canPlaySound() {
			return sound.canPlaySound();
		}

		@Override
		public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound,
				boolean looping) {
			return this.sound.getStream(soundBuffers, sound, looping);
		}
	}

}
