# BikeBell Android Project Reference

## Project Overview
- **Repository:** https://github.com/stig3824/BikeBellAndroid.git
- **Branch:** android-bell-animation-development
- **Current Location:** /Users/nicholasspackman/Desktop/Android/BikeBellAndroid
- **Language:** Kotlin with Jetpack Compose
- **Target:** Android API 36+ (Android 14+)

## Core Architecture

### Main Components
1. **MainActivity.kt** - Main UI and animation logic
2. **MotionManager.kt** - Handles device motion detection and sensitivity
3. **SoundManager.kt** - Manages bell sound playback and audio settings

### Key Features Implemented
- **Motion Detection:** Accelerometer-based shake detection with configurable sensitivity
- **Bell Animation:** Smooth color transitions from green/red to orange during ringing
- **Sound Playback:** Bell sound with intensity-based volume control
- **Settings UI:** Sensitivity and threshold configuration
- **Fullscreen Mode:** Immersive experience with black background

## Animation System

### Current Implementation
```kotlin
// Animation state management
var ringIntensity by remember { mutableStateOf(0f) }
val animatedRingIntensity by animateFloatAsState(
    targetValue = ringIntensity,
    animationSpec = tween(
        durationMillis = if (ringIntensity > 0f) (30000 / ringIntensity.coerceAtLeast(0.1f)).toInt() else 500,
        easing = EaseInOut
    ),
    label = "ring_intensity"
)
```

### Animation Behavior
- **Fade IN (to orange):** ~30 seconds (based on shake intensity)
- **Fade OUT (back to original):** 0.5 seconds (smooth transition)
- **Color Logic:** Uses `ColorFilter.tint()` with RGB manipulation
- **Orange Formula:** `red = 1f, green = 0.5f - (animatedRingIntensity * 0.2f), blue = 0f`

### Animation Triggers
1. **Motion detected** → `ringIntensity = intensity.coerceIn(0.1f, 0.8f)`
2. **Motion stops** → `ringIntensity = 0f` (smooth fade)
3. **Motion manager inactive** → `ringIntensity = 0f` (smooth fade)
4. **Timer expires** → `ringIntensity = 0f` (smooth fade)

## Motion Detection System

### Sensitivity Configuration
```kotlin
val mappedThreshold = ((threshold - 1f) / 49f) * 1.95f + 0.05f
val mappedSensitivity = ((sensitivity - 50f) / 150f) * 6.0f + 0.3f
```

### Current Settings
- **Threshold Range:** 0.05f to 2.0f (mapped from 1-50)
- **Sensitivity Range:** 0.3f to 6.3f (mapped from 50-200)
- **Intensity Clamping:** 0.1f to 0.8f (prevents too red)

## UI Components

### Bell Display
- **Active State:** Green bell with "🟢 ON / ⚪ OFF" text
- **Inactive State:** Red bell with "⚪ ON / 🔴 OFF" text
- **Ringing State:** Orange fade animation with intensity-based color

### Settings Dialog
- **Motion Threshold Slider:** 1-50 range
- **Sensitivity Slider:** 50-200 range
- **Real-time updates** to MotionManager and SoundManager

### Background
- **Color:** Black (`Color.Black`)
- **Fullscreen:** `FLAG_KEEP_SCREEN_ON | FLAG_FULLSCREEN`

## Sound System

### Bell Sound
- **File:** `R.raw.bell_sound`
- **Duration:** 800ms (configurable)
- **Volume Control:** Intensity-based volume scaling
- **Speaker Mode:** Enabled for maximum volume

### Sound Manager Features
- **Audio Focus Management**
- **Speaker Mode Control**
- **Volume Scaling**
- **Error Handling**

## Development History

### Major Milestones
1. **Initial Setup:** Android Studio project with basic motion detection
2. **Animation Development:** Multiple iterations of bell animation
3. **Swinging Animation:** Attempted swinging motion (removed due to issues)
4. **Color Animation:** Implemented smooth color transitions
5. **Independent Animation:** Separated sound and visual animation timing
6. **Smooth Fade:** Fixed immediate color snapping with proper fade transitions

### Key Technical Challenges Solved
1. **Animation Persistence:** Fixed bell staying orange indefinitely
2. **Smooth Transitions:** Implemented proper `animateFloatAsState` usage
3. **Color Accuracy:** Fine-tuned orange color formula
4. **Sensitivity Range:** Expanded motion detection sensitivity
5. **Build Issues:** Resolved androidx.annotation conflicts and DEX merging

### Animation Evolution
1. **Swinging Motion** → **Color Changes** → **Smooth Fade**
2. **Immediate Resets** → **Timer-based** → **Motion-based** → **Smooth Transitions**
3. **3-second fade** → **1.5-second fade** → **0.5-second fade**

## Current State

### Working Features
✅ **Motion Detection:** Configurable sensitivity and threshold
✅ **Bell Animation:** Smooth orange fade with proper color transitions
✅ **Sound Playback:** Intensity-based bell sound
✅ **Settings UI:** Real-time configuration
✅ **Fullscreen Mode:** Immersive experience
✅ **Smooth Fade:** 0.5-second fade-out animation
✅ **Color States:** Proper green/red/orange transitions
✅ **Git Backup:** All changes committed to GitHub

### File Structure
```
BikeBellAndroid/
├── app/src/main/java/com/example/bikebellandroid/
│   ├── MainActivity.kt          # Main UI and animation logic
│   ├── MotionManager.kt         # Motion detection
│   └── SoundManager.kt          # Audio management
├── app/src/main/res/
│   ├── drawable/
│   │   ├── bellicongreen.png    # Green bell icon
│   │   └── belliconred.png      # Red bell icon
│   └── raw/
│       └── bell_sound.mp3       # Bell sound file
└── PROJECT_REFERENCE.md         # This file
```

## Build Commands

### Development Workflow
```bash
# Build and install to emulator
./gradlew assembleDebug && ~/Library/Android/sdk/platform-tools/adb -s emulator-5558 install -r app/build/outputs/apk/debug/app-debug.apk

# Install to all emulators
~/Library/Android/sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
~/Library/Android/sdk/platform-tools/adb -s emulator-5556 install -r app/build/outputs/apk/debug/app-debug.apk

# Git operations
git add app/src/main/java/com/example/bikebellandroid/MainActivity.kt
git commit -m "Description of changes"
git push origin android-bell-animation-development
```

## User Experience Flow

### Typical Usage
1. **Launch App:** Black fullscreen interface with red bell
2. **Tap Bell:** Turns green, shows "ON / OFF" status
3. **Shake Device:** Bell rings and fades to orange
4. **Stop Shaking:** Bell smoothly fades back to green
5. **Tap Again:** Turns red, motion detection disabled

### Animation Timing
- **Sound Duration:** 800ms
- **Animation Duration:** ~30 seconds (intensity-based)
- **Fade-out Duration:** 0.5 seconds
- **Timer Backup:** 5 seconds (animation reset)

## Technical Notes

### Compose Animation Best Practices
- Use `animateFloatAsState` for smooth transitions
- Avoid immediate state resets that cause snapping
- Let animation complete naturally with proper easing
- Use `EaseInOut` for smooth color transitions

### Motion Detection Optimization
- Clamp intensity values to prevent extreme colors
- Use separate thresholds for sound and animation
- Implement proper state management for active/inactive

### Color Management
- **Green Bell:** `R.drawable.bellicongreen`
- **Red Bell:** `R.drawable.belliconred`
- **Orange Animation:** `ColorFilter.tint()` with RGB manipulation
- **Text Colors:** Green/Red/Gray based on state

## Future Considerations

### Potential Enhancements
- **Knocker Animation:** Animate the bell's knocker part
- **Remove White Bar:** Eliminate bottom navigation bar
- **Haptic Feedback:** Add vibration during ringing
- **Multiple Bell Sounds:** Different sounds for different intensities
- **Background Themes:** Additional color themes

### Known Limitations
- **Single Bell Sound:** Only one sound file available
- **Fixed Animation:** No user-configurable animation timing
- **Basic UI:** Minimal settings interface
- **No Persistence:** Settings reset on app restart

## Debug Information

### Log Tags
- `"BikeBell"` - Main application logs
- `"MotionManager"` - Motion detection logs
- `"SoundManager"` - Audio system logs

### Key Debug Messages
- `"Animation state - isRinging: $isRinging, intensity: $animatedRingIntensity"`
- `"Playing bell with intensity: $intensity"`
- `"Stopping ring sound"`
- `"Animation reset complete"`

This reference document provides a complete overview of the BikeBell Android project's current state, implementation details, and development history for future reference. 