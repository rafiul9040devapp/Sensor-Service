# Sensor Service App

This Android application is designed to monitor and record sensor data, providing insights into light levels, proximity, accelerometer, and gyroscope readings. The app visualizes this information through user-friendly charts and ensures data persistence with a built-in SQLite database.

## Installation

[Download APK](https://drive.google.com/file/d/1sARS_aiyMR7Nn7J6acVF6YoKLT7CSrS1/view?usp=sharing)

## Tasks

- **Sensor Monitoring:**
  - Real-time display of light sensor, proximity sensor, accelerometer, and gyroscope values on the main screen.

- **Chart Visualizations:**
  - Time series charts illustrate the historical data trends for each sensor.

- **Background Service:**
  - An attempt has been made to run the service in the background for seamless data recording.
  - Currently facing challenges in ensuring the app continues running seamlessly in the background. Persistent issues may arise after closing the app from the task manager.

- **User Notification:**
  - Implemented a notification system to keep users informed about the 4 sensor values.
  - The notification functionality is not working as intended. Efforts are ongoing to address and resolve this issue.
    
## Known Issues

- **Challenges in Background Service:**
  - Faced difficulties in ensuring the app continues running seamlessly in the background.
  - Persistent issues after closing the app from the task manager.

- **User Notification:**
  - Currently, the notification functionality is not working as intended. Efforts are ongoing to address and resolve this issue.

## Lessons Learned

In the process of developing this application, I gained valuable insights into working with Android sensors, services, notifications, and chart visualizations.

## Contributing

Feel free to contribute by addressing known issues or enhancing existing features. Pull requests are welcome!

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


