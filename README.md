# RunTracker

RunTracker is an Android application developed in Kotlin for tracking running activities. The app records the running route, steps, calories burned, and provides additional features such as weather information, photo gallery, and history of running activities.

## Demo Video
https://www.youtube.com/watch?v=IA1gVsiLBIU

## Figma
<img width="817" alt="image" src="https://github.com/Skymore/run-tracker-kotlin/assets/11980286/b0c4a128-8fd6-4e6b-bdb4-df76576a7d7e">


## Features

- **Running Tracking**: Records running time, distance, steps, and calories burned, and visualizes the running route on a map.
- **Weather Information**: Retrieves and displays current weather information based on the user's location.
- **Photo Gallery**: Shows photos taken during running activities.
- **History Records**: View the history of running activities.
- **Account Information**: Manage personal account details like name, gender, date of birth, etc.
- **Settings**: Customize the app appearance, including theme color and night mode.

## Installation

1. Clone the repository to your local machine:
    ```sh
    git clone https://github.com/yourusername/RunTracker.git
    cd RunTracker
    ```

2. Open Android Studio, go to `File > Open...`, and select the `RunTracker` project.

3. Connect your Android device or start an Android emulator.

4. Click the run button to install the app on your device.

## Usage

### Main Menu

The main menu provides navigation options for the different features of the app:
- `START ACTIVITY`: Start a new running activity.
- `WEATHER`: View current weather information.
- `GALLERY`: View photos taken during running activities.
- `STATISTICS`: View running statistics.
- `MY ACCOUNT`: Manage personal account details.
- `HISTORY`: View the history of running activities.
- `SETTINGS`: Adjust app settings.

### Running Tracking

Click `START ACTIVITY` to begin a new running session. The app will track the running time, distance, steps, and calories burned, and display the route on a map.

### Weather Information

Click `WEATHER` to view current weather conditions based on your location.

### Photo Gallery

Click `GALLERY` to view photos taken during your running sessions.

### History Records

Click `HISTORY` to view the history of your running activities, including the date, distance, and route of each run.

### Account Information

Click `MY ACCOUNT` to manage your personal account details such as name, gender, and date of birth.

### Settings

Click `SETTINGS` to access the settings menu, where you can customize the app's appearance, including theme color and night mode.

## Project Structure

- `MainActivity`: The main entry point of the application, displaying the navigation menu.
- `MapFragment`: Fragment for recording and displaying the running route on a map.
- `WeatherActivity`: Activity for displaying current weather information.
- `GalleryActivity`: Activity for displaying photos taken during running activities.
- `HistoryActivity`: Activity for viewing the history of running activities.
- `MyAccountActivity`: Activity for managing personal account information.
- `SettingsActivity`: Activity for adjusting application settings.
- `StatisticsActivity`: Activity for displaying running statistics.
- `services`: Contains `TimerService` and `TrackerService` for tracking time and location.
- `database`: Contains the database structure and data access objects.
- `converters`: Contains type converters for storing custom data types in the database.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact

For any questions or suggestions, please contact [realruitao@gmail.com](mailto:realruitao@gmail.com).
