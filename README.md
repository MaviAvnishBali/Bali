# Bali - Village Social Media App

Bali is a Kotlin Multiplatform (KMP) mobile app for community discussions.

## Project Structure
- `shared/`: Kotlin Multiplatform module (Domain, Data, Networking).
- `androidApp/`: Android application (Jetpack Compose).
- `iosApp/`: iOS application (SwiftUI Bridge).

## Getting Started

### Prerequisites
- JDK 17
- Android Studio / Xcode

### Mobile Setup
- Import the root `build.gradle.kts` into Android Studio.
- Run `androidApp`.

## API Documentation
This app talks to a GraphQL API. Configure the endpoint via `BASE_URL` in `shared/build.gradle.kts` (see the `dev`/`prod` product flavors).

### Sample Query
```graphql
query {
  feed(page: 0, size: 20) {
    id
    content
    author {
      username
    }
  }
}
```

## Firebase Setup

This project uses **Firebase Phone Authentication**.

### Android
1. Create a project in [Firebase Console](https://console.firebase.google.com/).
2. Enable **Authentication** -> **Sign-in method** -> **Phone**.
3. Add an Android app to the project (package: `com.bali.android`).
4. Download `google-services.json` and place it in `androidApp/google-services.json`.
