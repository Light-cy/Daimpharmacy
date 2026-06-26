<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/1dc1c3de-7346-4d3e-b011-1de15caba517

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device

## 🔑 Keystore Configuration

Since the `debug.keystore` is ignored in Git for security reasons, you must generate one locally to build the project. You may also want to generate a Release Keystore (`my-upload-key.jks`) for publishing signed APKs.

### 1. Generating `debug.keystore` (Required for both Debug & Release Builds)

**Method A: Using Terminal**
Run the following command in the root folder of the project to instantly generate the debug keystore with the required credentials:
```bash
keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
```

**Method B: Using Android Studio (Copy Default)**
1. Android Studio automatically generates a default debug keystore on your system.
   - **Windows:** `C:\Users\YOUR_USERNAME\.android\debug.keystore`
   - **Mac/Linux:** `~/.android/debug.keystore`
2. Locate this file and **copy** it.
3. **Paste** it into the root folder of this project.

### 2. Generating Release Keystore (`my-upload-key.jks`) (For Production)
If you want to build a properly signed release APK, you need this keystore. *(Note: If this file is missing, the build will safely fall back to using `debug.keystore` without failing).*

**Method A: Using Android Studio (Easiest)**
1. Go to **Build > Generate Signed Bundle / APK...** from the top menu.
2. Select **APK** and click **Next**.
3. Under **Key store path**, click **Create new...**
4. Set the path to this project's root folder and name it `my-upload-key.jks`.
5. Set the **Alias** to `upload` and fill in your passwords and certificate details.
6. Click **OK**.

**Method B: Using Terminal**
Run the following command in the project root folder:
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```
*Make sure to follow the prompts to enter your passwords and details.*
