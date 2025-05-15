📍 Geofence Alert App (Android)
This Android application is designed to create and monitor geofences using Google's Location Services API. The app continuously detects the user's physical activity and location and alerts the user (via sound and toast messages) upon entering or exiting predefined geofenced areas.

Developed using Java, XML, and Groovy DSL in Android Studio, this project demonstrates location-aware development and user activity recognition.

✨ Features
📌 Geofence Setup: Automatically sets a geofence at a specified location.

🧍‍♂️ Activity Recognition: Detects user's activity (walking, still, in vehicle, etc.) to optimize geofence behavior.

📲 Real-Time Monitoring: Constantly monitors user's location against geofences.

🔔 Alerts on Enter & Exit: Plays a sound and shows an alert when user enters or exits a geofenced area.

📡 Efficient Location Handling: Uses FusedLocationProviderClient for battery-efficient location updates.

🧰 Tech Stack
Technology	Description
Java	Programming language used
XML	For UI design
Groovy DSL	For Gradle script configuration
Google Play Services	For Geofencing and Activity Recognition APIs

⚠️ Notes
Android 10+ requires background location access permission separately.
Some devices may require enabling location services manually.
Activity recognition can occasionally lag due to sensor calibration.

📎 License
This project is open-source and available under the MIT License.

https://github.com/user-attachments/assets/29b06757-fda3-4860-b2ac-f8cdbff47222

