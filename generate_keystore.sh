#!/bin/bash

# BikeBell Keystore Generation Script
# This script generates a keystore for app signing

echo "🔐 Generating keystore for BikeBell..."

# Create keystore directory if it doesn't exist
mkdir -p keystore

# Generate keystore
keytool -genkey -v \
  -keystore keystore/bikebell-release-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias bikebell-key \
  -storepass bikebell123 \
  -keypass bikebell123 \
  -dname "CN=BikeBell, OU=Development, O=BikeBell, L=City, S=State, C=US"

if [ $? -eq 0 ]; then
    echo "✅ Keystore generated successfully!"
    echo ""
    echo "📁 Keystore location: keystore/bikebell-release-key.jks"
    echo ""
    echo "🔑 Keystore details:"
    echo "   Store password: bikebell123"
    echo "   Key password: bikebell123"
    echo "   Alias: bikebell-key"
    echo ""
    echo "⚠️  IMPORTANT: Keep this keystore safe!"
    echo "   - Store it securely"
    echo "   - Back it up"
    echo "   - Don't lose the passwords"
    echo ""
    echo "📝 Next steps:"
    echo "   1. Update local.properties with keystore details"
    echo "   2. Uncomment signing config in build.gradle.kts"
    echo "   3. Test the signed build"
else
    echo "❌ Keystore generation failed!"
    exit 1
fi 