# Codemagic Connect (Unofficial) - Agent Instructions

This document outlines the architectural guidelines, design patterns, and testing principles for the Codemagic Connect Android app. AI Agents and human contributors must adhere strictly to these rules when modifying or adding new code.

## 1. Architecture Overview
- **Pattern:** Clean Architecture + MVVM/MVI hybrid.
- **Dependency Injection:** Dagger Hilt.
- **Module Structure:**
  - `:app`: Application class, DI setup, Jetpack Compose UI (Presentation layer), and Jetpack Navigation.
  - `:domain`: Pure business logic. Contains Entities, Use Case abstractions and implementations, and Gateway (Repository) abstractions.
  - `:data`: Data source implementations (Network, Local Storage). Provides Entities for the Domain layer.
  - `:core`: Supporting utilities and common shared components.

## 2. Presentation Layer (Jetpack Compose & MVI)
- **MVI Components:** The ViewModel should encapsulate its contract using inner classes:
  - `UiState`: Data class representing the current state of the UI.
  - `Action` (or `Intent`): Sealed class/interface representing user actions.
  - `UiEvent`: Sealed class/interface representing one-time events (e.g., Snackbars, Navigation triggers).

### Route vs Screen Pattern
Always split Compose UI into two layers: Route (stateful) and Screen (stateless).
- **Route:** Stateful container. Gets ViewModel, collects state/events, handles navigation, and triggers ViewModel initialization.
- **Screen:** Stateless pure UI. Only receives `UiState` and emits events via callbacks.

```kotlin
@Composable
internal fun ExampleRoute(
    viewModel: ExampleViewModel = hiltViewModel(),
    onNavigationEvent: (ExampleNavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.init()
        launch {
            viewModel.navigationEvent
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .collectLatest { onNavigationEvent(it) }
        }
        launch {
            viewModel.singleEvent
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .collectLatest { /* handle snackbar/intent/etc */ }
        }
    }

    ExampleScreen(uiState = uiState, onUiEvent = viewModel::onUiEvent, modifier = modifier)
}

@Composable
internal fun ExampleScreen(
    uiState: ExampleUiState,
    onUiEvent: (ExampleUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) { 
    /* stateless rendering */ 
}
```
*Note: Use `collectAsStateWithLifecycle()` for `StateFlow` and `flowWithLifecycle().collectLatest {}` for `SharedFlow`.*

### ViewModel Initialization
**Never** initialize data fetching in the `ViewModel` constructor. Use an explicit `init()` method called from the composable via `LaunchedEffect(Unit)`.

## 3. Navigation
Uses Jetpack Compose Navigation. The root graph lives in `:app` (`MainActivity.kt`/`CodemagicConnectApp.kt`). 
- Feature navigation lives in `feature/{feature}/{screen}/{ScreenName}Navigation.kt`.
- **ViewModels must never access NavController.** Pass navigation events upward via `NavigationEvent` to the Route's `onNavigationEvent` callback.
- **Arguments:** Passed via `SavedStateHandle` and wrapped in a strongly typed args class.
  ```kotlin
  internal class OrderDetailArgs(savedStateHandle: SavedStateHandle) {
      val orderId: Long = (checkNotNull(savedStateHandle["order_id"]) as String).toLong()
  }
  ```
- **Route Builders:** Always use the core route builders. **Never construct route strings manually.**
  - `GDDestinationRoute.Builder`: For registering in `NavGraphBuilder` (uses `{arg}` placeholders).
  - `GDNavigationRoute.Builder`: For navigating from `NavController` (uses actual values).

## 4. Coding Conventions
### Self-Explanatory Variables
Never inline magic numbers or unexplained literals. Extract them into a named `const val` whose name states *why* the value exists, not *what* it is. The name carries the intent so the call site reads like prose.
- Declare constants in a `companion object` at the **bottom** of the class.
- Use `private const val` with `UPPER_SNAKE_CASE`.
- Name by meaning, not value (e.g., `FULL_CAPTURE`, not `SAMPLE_RATE_ONE`).
- One constant per distinct concept, even if two values happen to be equal.

### Coroutines / Fail-Fast
- **Fail-fast:** If a failure is not explicitly required to be handled by product requirements, **let it crash**. Do not swallow exceptions silently in try/catch blocks without re-throwing or properly handling them.

## 5. Testing & TDD
**TDD is MANDATORY.** Follow the RED → GREEN cycle strictly:
1. Write the test first (it must fail to compile or fail at runtime).
2. Run it and observe the actual failure.
3. Write the minimum implementation to pass the test.
4. **Never test and implement in the same step.**

### Implementation Order
- **Cross-layer:** Feature (UI) → Domain → Infrastructure (Data/Core). UI defines the real API surface; Domain types compile-fail downstream into scope; Infrastructure is the mechanical mapping handled last.
- **Within a layer:** Compose UI → ViewModel → UseCase → Repository.

### Testing Frameworks & Rules
- **Tools:** JUnit 4, MockK, Turbine, Robolectric, Compose Testing Library.
- **Naming Convention:** `given <precondition> when <action> then <expected outcome>` (Leave untouched legacy tests as-is).
- **Test Rules:**
  - `CoroutineMainDispatcherRule`: Replaces Main dispatcher.
  - `LocaleRule`: For locale-sensitive tests.
  - `TestApiRule`: Stubs OkHttp for repository tests.

### ViewModel Test Pattern
```kotlin
class ExampleViewModelTest {
    @get:Rule val coroutineRule = CoroutineMainDispatcherRule()
    
    private val mockUseCase = mockk<SomeUseCase>()
    private lateinit var viewModel: ExampleViewModel

    @Before fun setUp() { 
        viewModel = ExampleViewModel(mockUseCase) 
    }

    @Test fun `test state`() = runTest {
        viewModel.uiState.test {
            viewModel.init()
            // assert with Turbine
        }
    }
}
```

### Data Factories & Repositories
- Data factories live in `src/debug/kotlin/` of domain/api modules (generated using the `/create-data-factory` skill).
- Repository tests should stub API responses (using `TestApiRule`) and strictly verify JSON parsing and mapping to domain entities.

## 6. Code Quality
- **Detekt** is used for static code analysis. Ensure code complies with the project's Detekt configuration.
