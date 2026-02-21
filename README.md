# CocktailApp — Документація для розробника

## 📋 Огляд проєкту

**CocktailApp** — мобільний застосунок на **Jetpack Compose** для перегляду коктейлів з TheCocktailDB API та авторизацією через Google. Архітектура **MVVM**.

## 🛠️ Технологічний стек

- **Мова**: Kotlin 1.9+
- **UI**: Jetpack Compose (Material3)
- **Архітектура**: MVVM + Repository pattern
- **Мережа**: Retrofit 2 + OkHttp
- **Зображення**: Glide
- **Авторизація**: Google Credential Manager
- **Локальне зберігання**: SharedPreferences
- **Навігація**: Navigation Compose
- **Тестування**: Unit tests (ViewModel), Manual UI testing

## 📁 Структура проєкту

```
app/
├── src/main/
│   ├── java/com/example/cocktailapp/
│   │   ├── api/
│   │   │   ├── ApiService.kt/
│   │   │   ├── CocktailsRepository.kt/ 
│   │   │   └── GoogleAuthClient.kt           
│   │   ├── data/
│   │   │   │   ├── CocktailDetails.kt
│   │   │   │   ├── CocktailResponse.kt
│   │   │   │   ├── CocktailsCategory.kt
│   │   │   │   └── GoogleUser.kt
│   │   ├── ui/theme/                         
│   │   │   │   ├── CocktailsAppScreen.kt
│   │   │   │   ├── Color.kt
│   │   │   │   ├── Theme.kt
│   │   │   │   └── Type.kt
│   │   ├── viewmodel/
│   │   │   │   └── CocktailsViewModel.kt     
└── build.gradle.kts
```

## 🚀 Швидкий старт

### 1. Клонування та залежності
```bash
git clone <your-repo-url>
cd CocktailApp
./gradlew build
```

### 2. Налаштування ключів
1. **Google Cloud Console**:
   - Створіть проєкт: https://console.cloud.google.com
   - Credentials → Create Credentials → Android Client ID
   - Вкажіть **Package name**: `com.example.cocktailapp`
   - **SHA-1** debug.keystore (команда: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android`)
2. **serverClientId** (Web Client ID) вставте у `GoogleAuthClient.kt`.

### 3. Запуск
```bash
./gradlew installDebug
# або через Android Studio: Run → Run 'app'
```

### 4. Емулятор
```
AVD Manager → Pixel 6 → API 33+
```

## 🔧 Налаштування для розробки

### Змінні в `local.properties` (опціонально)
```
GOOGLE_WEB_CLIENT_ID=your_web_client_id_here
```

### Gradle залежності (build.gradle.kts)
```kotlin
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Image loading
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    
    // Google Auth
    implementation("androidx.credentials:credentials:1.2.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")
}
```

## 🏗️ Архітектура

```
[User] → [MainActivity] → [NavHost]
                              ↓
                       [HomeScreen] ← [CocktailListViewModel]
                              ↓                ↓
                       [CocktailInfo]    [CocktailRepository]
                              ↓                ↓
                       [ProfileScreen] ← [GoogleAuthClient]
```

### Ключові компоненти

1. **`ApiService`** — завантажує коктейлі з API, кешує списки
2. **`GoogleAuthClient`** — обробляє Credential Manager авторизацію
3. **`CocktailListViewModel`** — стан списку коктейлів, пошук

## 🔍 Налагодження

- **Logcat**: View → Tool Windows → Logcat
- **Layout Inspector**: Tools → Layout Inspector  
- **Profiler**: View → Tool Windows → Profiler

## 📱 API документація

```
GET https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Alcoholic
GET https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=11007
```

## ⚠️ Відомі проблеми

1. **SHA-1 debug.keystore** — кожен комп’ютер має свій ключ
2. **Помилка ** — неправильний SHA-1 або Web Client ID

***
