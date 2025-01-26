# 📱 Chatting App Mobile

This repository contains the source code for a mobile chatting application built using Kotlin and the Android SDK. The app supports **🔒 user authentication**, **💬 a chat interface**, and **🎨 customizable themes**.

## Features

- **🔒 User Authentication**: Login and registration functionalities.
- **💬 Chat Interface**: Real-time chat with other users.
- **🎨 Theming**: Customizable themes for a better user experience.

---

## Directory Structure

```
📂 thanhlamcode-chatting-app-mobile/
├── 📜 build.gradle.kts
├── 📜 gradle.properties
├── 📜 gradlew
├── 📜 gradlew.bat
├── 📜 settings.gradle.kts
├── 📂 app/
│   ├── 📜 build.gradle.kts
│   ├── 📜 proguard-rules.pro
│   ├── 📜 .gitignore
│   └── 📂 src/
│       ├── 📂 androidTest/
│       │   └── 📂 java/
│       │       └── 📂 lamdoan/
│       │           └── 📂 chatting/
│       │               └── 📜 ExampleInstrumentedTest.kt
│       ├── 📂 main/
│       │   ├── 📜 AndroidManifest.xml
│       │   ├── 📂 java/
│       │   │   └── 📂 lamdoan/
│       │   │       └── 📂 chatting/
│       │   │           ├── 📜 ChatListUser.kt
│       │   │           ├── 📜 ChattingDetail.kt
│       │   │           ├── 📜 ListUser.kt
│       │   │           ├── 📜 MainActivity.kt
│       │   │           ├── 📜 Model.kt
│       │   │           ├── 📜 Setting.kt
│       │   │           ├── 📜 Tab.kt
│       │   │           ├── 📂 auth/
│       │   │           │   ├── 📜 LoginActivity.kt
│       │   │           │   └── 📜 RegisterActivity.kt
│       │   │           └── 📂 ui/
│       │   │               └── 📂 theme/
│       │   │                   ├── 📜 Color.kt
│       │   │                   ├── 📜 Theme.kt
│       │   │                   └── 📜 Type.kt
│       │   └── 📂 res/
│       │       ├── 📂 drawable/
│       │       │   ├── 📜 ic_eye.xml
│       │       │   ├── 📜 ic_eye_off.xml
│       │       │   ├── 📜 ic_launcher_background.xml
│       │       │   ├── 📜 ic_launcher_foreground.xml
│       │       │   ├── 📜 ic_lock.xml
│       │       │   ├── 📜 ic_menu.xml
│       │       │   ├── 📜 ic_pin.xml
│       │       │   ├── 📜 ic_search.xml
│       │       │   └── 📜 ic_user.xml
│       │       ├── 📂 mipmap-anydpi/
│       │       │   ├── 📜 ic_launcher.xml
│       │       │   └── 📜 ic_launcher_round.xml
│       │       ├── 📂 mipmap-hdpi/
│       │       │   ├── 📜 ic_launcher.webp
│       │       │   └── 📜 ic_launcher_round.webp
│       │       ├── 📂 mipmap-mdpi/
│       │       │   ├── 📜 ic_launcher.webp
│       │       │   └── 📜 ic_launcher_round.webp
│       │       ├── 📂 mipmap-xhdpi/
│       │       │   ├── 📜 ic_launcher.webp
│       │       │   └── 📜 ic_launcher_round.webp
│       │       ├── 📂 mipmap-xxhdpi/
│       │       │   ├── 📜 ic_launcher.webp
│       │       │   └── 📜 ic_launcher_round.webp
│       │       ├── 📂 mipmap-xxxhdpi/
│       │       │   ├── 📜 ic_launcher.webp
│       │       │   └── 📜 ic_launcher_round.webp
│       │       ├── 📂 values/
│       │       │   ├── 📜 colors.xml
│       │       │   ├── 📜 strings.xml
│       │       │   └── 📜 themes.xml
│       │       └── 📂 xml/
│       │           ├── 📜 backup_rules.xml
│       │           └── 📜 data_extraction_rules.xml
│       └── 📂 test/
│           └── 📂 java/
│               └── 📂 lamdoan/
│                   └── 📂 chatting/
│                       └── 📜 ExampleUnitTest.kt
└── 📂 gradle/
    ├── 📜 libs.versions.toml
    └── 📂 wrapper/
        └── 📜 gradle-wrapper.properties
```

---

## Getting Started

### Prerequisites

- 📱 Android Studio
- 🛠️ Kotlin 1.8+
- ⚙️ Gradle 7.2+

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/thanhlamcode/Chatting-App-Mobile.git
   ```

2. Open the project in Android Studio.

3. Sync the Gradle files and resolve dependencies.

4. Run the application on an emulator or a physical device.

---

## ## 🛠️ Contributing

1. 🍴 **Fork the repository.**

2. 🌿 **Create a new branch:**

   ```bash
   git checkout -b feature-branch-name
   ```

3. ✏️ **Make your changes and commit them:**

   ```bash
   git commit -m 'Add new feature or fix bug'
   ```

4. 📤 **Push to the branch:**

   ```bash
   git push origin feature-branch-name
   ```

5. 📝 **Create a pull request.**

---

## 📜 License

This project is licensed under the MIT License. See the LICENSE file for details.

---

## 📧 Contact

If you have any questions or suggestions, feel free to open an issue or contact [@thanhlamcode](https://github.com/thanhlamcode).

