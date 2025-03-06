# Flight App (Jetpack Compose)

An Android Flight App, allowing users to search for destinations, browse flight routes, and manage favorite flight destinations.

## Description

This Flight App is a demonstration project built using Jetpack Compose to showcase modern Android UI development. It provides a user-friendly interface for searching and exploring flight destinations from a selected departure airport. Users can also save their preferred flight routes as favorites for easy access.

## Key Features

*   Airport Search & Autocomplete: Intelligent search field with real-time suggestions to quickly find airports by IATA code or name as you type.
*   Browse Flight Destinations: Discover a curated list of potential flight destinations originating from a user-selected departure airport.
*   Local Data Persistence (Favorites): Utilizes Room Database and DataStore to locally persist user's favorite flight routes, ensuring data is saved across app sessions.
*   Favorite Routes Management: Easily add and remove flight routes from your favorites list with a simple star button.
*   Favorite Routes Screen: Dedicated section to view and manage all your saved favorite flight routes in one place.
*   Snackbar Feedback: Provides visual feedback using Snackbars when routes are added to or removed from favorites, ensuring a smooth user experience.
*   Material Design 3 UI: Built with the latest Material Design 3 components, offering a clean, modern, and consistent Android look and feel.

## Screenshots

<img src="https://github.com/user-attachments/assets/d6b1a01a-fb1a-454c-aa53-2318512cc9a4" width="250" alt="Search Screen 1">
<img src="https://github.com/user-attachments/assets/e650f2de-acb4-409e-9ba7-ef4b6afa70d6" width="250" alt="Search Screen 3">
<img src="https://github.com/user-attachments/assets/75201380-03f5-499f-a764-7abafb060ee7" width="250" alt="Search Screen 2">
<img src="https://github.com/user-attachments/assets/d67974a4-a525-46fc-a13b-5a9e2ab35795" width="250" alt="Flights Screen">
<img src="https://github.com/user-attachments/assets/3189d546-f3ef-4744-bd27-3fe421c460e2" width="250" alt="Favorite Added Snackbar">
<img src="https://github.com/user-attachments/assets/3f12c0de-703f-4a3b-92bc-9f339ef1f5e1" width="250" alt="Flights Removed Snackbar">
<img src="https://github.com/user-attachments/assets/24772923-4fb4-4c91-be59-3a58394c581a" width="250" alt="Favorites Screen">

## Technologies Used

*   Jetpack Compose: Android's modern UI toolkit for building native UIs declaratively.
*   Kotlin: The officially recommended programming language for Android development.
*   Android Material Design 3: For a visually appealing and up-to-date user interface.
*   ViewModel: To manage UI-related data in a lifecycle-conscious way.
*   Room Persistence Library: For robust and efficient local data storage of favorite flight routes.
*   DataStore: For storing app settings and user preferences.

## Installation

To run this project, follow these steps:

1.  **Prerequisites:**
    *   [Android Studio](https://developer.android.com/studio) installed on your machine.
    *   An Android emulator or a physical Android device.

2.  **Clone the repository:**

    ```bash
    git clone https://github.com/Qume02/Flight-App.git
    ```

3.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an existing project" and navigate to the cloned `Flight-App` directory.

4.  **Build and Run:**
    *   Once the project is loaded, allow Gradle to sync and build.
    *   Select your desired emulator or connected Android device in Android Studio.
    *   Click the "Run" button (or press `Shift+F10`).

5.  **Explore the App:**
    *   Once the app is running, you can start searching for airports and browsing flight destinations.
    *   Test adding and removing routes from favorites on both the Destinations and Favorites screens to see the Snackbar feedback and verify data persistence.

## Features (Detailed)

*   Interactive Airport Search: As you type in the search field, the app dynamically suggests airports matching your input, making airport selection quick and efficient.
*   Destination Listing: After selecting a departure airport, the app presents a list of potential destinations, displaying key information like airport codes and names, as seen in the screenshot.
*   Favorite Route Toggling: A star icon on each route allows you to easily toggle the favorite status, adding or removing routes from your personal favorites.
*   Dedicated Favorites Screen: Access a separate screen to view all your saved favorite flight routes, providing a convenient way to manage your preferred destinations.
*   Snackbar Notifications: Subtle Snackbar messages appear at the bottom of the screen to confirm when a route is added to or removed from favorites, enhancing user feedback without interrupting the workflow.
*   Material Design 3 UI: Built entirely with Jetpack Compose, the app offers a smooth, responsive, and visually appealing user interface adhering to Material Design 3 principles.
*   Persistent Favorites: User's favorite flight routes are stored locally using Room Database and DataStore, ensuring that favorites are remembered even after the app is closed and reopened.
