┌───────────────────────────────────────────────────────────────┐
│                         React Frontend                        │
│   - Login / Register                                          │
│   - Tour List / Search                                        │
│   - Tour Details                                              │
│   - Tour Logs                                                 │
│   - Import / Export                                           │
└───────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP / JSON REST API
                              ▼
┌───────────────────────────────────────────────────────────────┐
│                    Presentation Layer                         │
│                         Controllers                           │
│                                                               │
│  AuthController                                               │
│  TourController                                               │
│  TourLogController                                            │
│  SearchController                                             │
│  ImportExportController                                       │
└───────────────────────────────────────────────────────────────┘
                              │
                              │ calls
                              ▼
┌───────────────────────────────────────────────────────────────┐
│                     Business Layer                            │
│                          Services                             │
│                                                               │
│  AuthService                                                  │
│  UserService                                                  │
│  TourService                                                  │
│  TourLogService                                               │
│  SearchService                                                │
│  StatisticsService                                            │
│  ImportExportService                                          │
│  RouteService                                                 │
│                                                               │
│  Responsibilities:                                            │
│  - business rules                                             │
│  - ownership checks                                           │
│  - validation logic                                           │
│  - popularity calculation                                     │
│  - child-friendliness calculation                             │
│  - orchestration of DB + external API                         │
└───────────────────────────────────────────────────────────────┘
                 │                     │
                 │                     │
                 │                     ▼
                 │      ┌──────────────────────────────────────┐
                 │      │         Integration Layer            │
                 │      │                                      │
                 │      │ OpenRouteServiceClient               │
                 │      │ - route lookup                       │
                 │      │ - distance/time retrieval            │
                 │      │ - map/route info                     │
                 │      └──────────────────────────────────────┘
                 │
                 ▼
┌───────────────────────────────────────────────────────────────┐
│                    Data Access Layer                          │
│                        Repositories                           │
│                                                               │
│  UserRepository                                               │
│  TourRepository                                               │
│  TourLogRepository                                            │
└───────────────────────────────────────────────────────────────┘
                              │
                              │ JPA / Hibernate
                              ▼
┌───────────────────────────────────────────────────────────────┐
│                         PostgreSQL                            │
│                                                               │
│  Tables / Entities:                                           │
│  - users                                                      │
│  - tours                                                      │
│  - tour_logs                                                  │
└───────────────────────────────────────────────────────────────┘


┌───────────────────────────────────────────────────────────────┐
│                    Supporting Components                      │
│                                                               │
│  Security                                                     │
│  - Spring Security                                            │
│  - authentication                                             │
│  - authorization / user ownership                             │
│                                                               │
│  DTO / Mapper Layer                                           │
│  - request DTOs                                               │
│  - response DTOs                                              │
│  - entity mapping                                             │
│                                                               │
│  Exception Handling                                           │
│  - custom exceptions                                          │
│  - global exception handler                                   │
│                                                               │
│  Logging                                                      │
│  - audit / error / API logs                                   │
│                                                               │
│  File System Storage                                          │
│  - image storage                                              │
│  - import/export files                                        │
└───────────────────────────────────────────────────────────────┘