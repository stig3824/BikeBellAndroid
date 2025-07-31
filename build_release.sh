#!/bin/bash

# BikeBell Release Build Script
# This script builds the release APK and AAB for Google Play Store

echo "🚲 Building BikeBell Release..."

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build release APK
echo "📱 Building release APK..."
./gradlew assembleRelease

# Build release AAB (recommended for Play Store)
echo "📦 Building release AAB..."
./gradlew bundleRelease

# Check if builds were successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "📁 Generated files:"
    echo "   APK: app/build/outputs/apk/release/app-release.apk"
    echo "   AAB: app/build/outputs/bundle/release/app-release.aab"
    echo ""
    echo "📋 Next steps:"
    echo "   1. Test the APK on a device"
    echo "   2. Upload AAB to Google Play Console"
    echo "   3. Complete store listing"
    echo "   4. Submit for review"
else
    echo "❌ Build failed!"
    exit 1
fi 