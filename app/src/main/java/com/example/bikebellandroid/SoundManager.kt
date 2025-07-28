package com.bikebell.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.util.Log
import kotlin.random.Random
import kotlinx.coroutines.*
import androidx.compose.runtime.Composable

class SoundManager(private val context: Context) {
    private val mediaPlayers = mutableListOf<MediaPlayer>()
    private val maxPlayers = 3
    private val minTimeBetweenRings = 150L
    private var lastRingTime = 0L
    private var isChangingBellType = false
    private var sensitivityMultiplier: Float = 1.0f
    private var isInitialized = false
    private var isReady = false
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var currentPlayerIndex = 0
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    
    var currentBellType by mutableStateOf(BellType.BIKE_BELL)
        private set
    var useSpeakerOnly by mutableStateOf(false)
        private set
    var appVolume by mutableStateOf(1.0f)
        private set

    init {
        scope.launch(Dispatchers.Main) {
            initializePlayers()
        }
    }

    private fun createMediaPlayer(bellType: BellType): MediaPlayer? {
        return try {
            val player = MediaPlayer()
            val resId = when (bellType) {
                BellType.BIKE_BELL -> R.raw.bike_bell
                BellType.COW_BELL -> R.raw.cowbell
            }
            val afd = context.resources.openRawResourceFd(resId)
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            player.setOnCompletionListener { mp ->
                mp.seekTo(0)
            }
            player.prepare()

            // Force speaker if needed (API 23+)
            if (useSpeakerOnly && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                for (device in audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
                    if (device.type == android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                        player.setPreferredDevice(device)
                        break
                    }
                }
            }

            player
        } catch (e: Exception) {
            Log.e("BikeBell", "Error creating MediaPlayer: ${e.message}")
            null
        }
    }

    private suspend fun initializePlayers() {
        try {
            repeat(maxPlayers) {
                createMediaPlayer(currentBellType)?.let { mediaPlayers.add(it) }
            }
            isInitialized = true
            isReady = true
            Log.d("BikeBell", "MediaPlayers initialized: ${mediaPlayers.size}")
        } catch (e: Exception) {
            Log.e("BikeBell", "Error initializing MediaPlayers: ${e.message}")
        }
    }

    private fun getNextPlayer(): MediaPlayer? {
        if (mediaPlayers.isEmpty()) return null
        currentPlayerIndex = (currentPlayerIndex + 1) % mediaPlayers.size
        return mediaPlayers[currentPlayerIndex]
    }

    private fun cleanupPlayer(player: MediaPlayer) {
        try {
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
        } catch (e: Exception) {
            Log.e("BikeBell", "Error cleaning up player: ${e.message}")
        }
    }

    fun setBellType(type: BellType) {
        if (currentBellType == type) return
        isChangingBellType = true
        try {
            // Clean up existing players
            mediaPlayers.forEach { cleanupPlayer(it) }
            mediaPlayers.clear()
            // Create new players with the new bell type
            repeat(maxPlayers) {
                createMediaPlayer(type)?.let { mediaPlayers.add(it) }
            }
            currentBellType = type
            isReady = true
        } catch (e: Exception) {
            Log.e("BikeBell", "Error changing bell type: ${e.message}")
        } finally {
            isChangingBellType = false
        }
    }

    fun updateSensitivity(sensitivity: Float) {
        sensitivityMultiplier = ((sensitivity - 50f) / 150f).coerceIn(0f, 1f)
    }

    fun updateUseSpeakerOnly(useSpeaker: Boolean) {
        useSpeakerOnly = useSpeaker
        audioManager.isSpeakerphoneOn = useSpeaker
    }

    fun updateAppVolume(volume: Float) {
        appVolume = volume.coerceIn(0f, 1f)
    }

    fun playBell(intensity: Float) {
        if (!isInitialized || !isReady || isChangingBellType) {
            Log.d("BikeBell", "Skipping bell play - initialized: $isInitialized, ready: $isReady, changing: $isChangingBellType")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRingTime < minTimeBetweenRings) {
            return
        }

        try {
            val player = getNextPlayer() ?: return
            
            // Reset the player if it's currently playing
            if (player.isPlaying) {
                try {
                    player.seekTo(0)
                } catch (e: Exception) {
                    Log.e("BikeBell", "Error seeking player: ${e.message}")
                    // Try to recreate this player
                    val index = mediaPlayers.indexOf(player)
                    if (index != -1) {
                        cleanupPlayer(player)
                        createMediaPlayer(currentBellType)?.let { newPlayer ->
                            mediaPlayers[index] = newPlayer
                            playBell(intensity) // Retry with new player
                        }
                    }
                    return
                }
            }
            
            // Play the sound
            val randomIntensity = intensity * (0.85f + Random.nextFloat() * 0.15f)
            val volume = (randomIntensity * sensitivityMultiplier * appVolume).coerceIn(0f, 1f)
            player.setVolume(volume, volume)
            
            try {
                player.start()
                lastRingTime = currentTime
                Log.d("BikeBell", "Playing bell with volume: $volume on player $currentPlayerIndex")
            } catch (e: Exception) {
                Log.e("BikeBell", "Error starting player: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("BikeBell", "Error in playBell: ${e.message}")
        }
    }

    fun cleanup() {
        scope.cancel()
        mediaPlayers.forEach { cleanupPlayer(it) }
        mediaPlayers.clear()
    }
} 