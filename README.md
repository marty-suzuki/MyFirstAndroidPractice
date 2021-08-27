# Movie Search App

This is my first android practice project, using **Flow**, **Suspend Function**, **AAC ViewModel**, **Dagger Hilt** and so on.

<table>
  <tr>
    <td>
      <img src="https://user-images.githubusercontent.com/2082134/130643158-5f47a2f2-18e1-48f9-81aa-b8d3eae3c224.gif" />
    </td>
    <td>
      <img src="https://user-images.githubusercontent.com/2082134/130643528-034eaca0-40e0-44b2-841b-88931bc3ac2b.gif" />
    </td>
  </tr>
</table>

## Project Structure

```
root/
  ┣ app
  ┃  ┣ App.kt
  ┃  ┣ MainActivity.kt
  ┃  ┣ nav_graph.xml
  ┃  ┗ HiltInjectionModules
  ┣ Router
  ┃  ┣ MovieDetailRouter.kt (interface)
  ┃  ┗ MovieSearchRouter.kt (interface)
  ┣ UIComponent
  ┃  ┣ MovieDetailFragment.kt
  ┃  ┣ fragment_movie_detail.xml
  ┃  ┣ MovieSearchFragment.kt
  ┃  ┗ fragment_movie_search.xml
  ┣ ViewModel
  ┃  ┣ main
  ┃  ┃  ┣ MovieDetailUiLogicImpl.kt
  ┃  ┃  ┣ MovieDetailUiLogicFactoryImpl.kt
  ┃  ┃  ┣ MovieSearchUiLogicImpl.kt
  ┃  ┃  ┗ MovieSearchUiLogicFactoryImpl.kt
  ┃  ┗ test
  ┃     ┣ MovieDetailUiLogicImplTest.kt
  ┃     ┗ MovieSearchUiLogicImplTest.kt
  ┣ ViewModelInterface
  ┃  ┣ AnyViewModel.kt
  ┃  ┣ UiLogic.kt (interface)
  ┃  ┣ UiLogicFactory.kt (interface)
  ┃  ┣ MovieDetailViewModel.kt
  ┃  ┣ MovieDetailUiLogic.kt (interface)
  ┃  ┣ MovieSearchViewModel.kt
  ┃  ┗ MovieSearchUiLogic.kt (interface)
  ┣ Repository
  ┃  ┣ main
  ┃  ┃  ┗ MovieRepositoryImpl.kt
  ┃  ┗ test
  ┃     ┗ MovieRepositoryImplTest.kt
  ┣ RepositoryInterface
  ┃  ┗ MovieRepository.kt (interface)
  ┣ RemoteDataSource
  ┃  ┗ TheMovieDatabaseService.kt
  ┗ RemoteDataSourceInterface
     ┗ TheMovieDatabaseService.kt (interface)
```

### Module Dependency Graph

![module_dependency](https://user-images.githubusercontent.com/2082134/131148609-929a4704-9198-420f-988e-483f04c1221e.png)

### View Composition and UI Layer Data Structure

UI Layer Data Structure corresponds to View Composition, therefore be able to test view patterns via unit testing. (e.g. [MovieDetailUiLogicImplTest.kt](https://github.com/marty-suzuki/MyFirstAndroidPractice/blob/2c90576/ViewModel/src/test/java/com/martysuzuki/viewmodel/MovieDetailUiLogicImplTest.kt))

<img width="1808" alt="view" src="https://user-images.githubusercontent.com/2082134/131160492-3c6c2685-35c5-4b23-9080-60fe967fb972.png">

### Thrid Party Libraries
- [glide](https://github.com/bumptech/glide)
- [moshi](https://github.com/square/moshi)
- [retrofit](https://github.com/square/retrofit)

## Requirements

- Android Studio Arctic Fox 2020.3.1 Patch 1
- Java 8
- Kotlin 1.5.30

## Usage

To run this application, [The Movie Database](https://developers.themoviedb.org/3/getting-started/introduction) `API Key` and `Access Token` are needed.
Set those required strings to `app/gradle.properties` like below.

```
TMDB_API_KEY=xxxx
TMDB_ACCESS_TOKEN=xxxx
```