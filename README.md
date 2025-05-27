# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

Perfect — thank you for the clarification. Based on this, I’ve updated the architecture to reflect that:
	•	Metadata is stored in JSON format, typically served from backend files or API (not from DB).
	•	Actual data is stored in normalized tables in PostgreSQL, using standard columns and rows.

⸻

Updated Architecture Overview

Metadata-Driven UI Framework (Revised)

A declarative system where UI rendering is powered by JSON metadata, and data persistence is handled by traditional RDBMS.

⸻

1. Revised High-Level Architecture Diagram

┌────────────────────────────┐
│      Frontend (Lit)        │
│                            │
│ ┌───────────────────────┐  │
│ │ Metadata Parser       │  │
│ │ - Detect tabs         │  │
│ │ - Determine layout    │  │
│ └───────────────────────┘  │
│         ↓       ↑          │
│ ┌───────────────────────┐  │
│ │ Dynamic Field/Table   │  │
│ │ Renderer (Form/Table) │  │
│ └───────────────────────┘  │
│         ↓                  │
│  Rendered Screen (DOM)     │
└─────────┬─────────▲────────┘
          │         │
    Metadata JSON   │
     (from API or   │
       file)        │
                    │
          ┌─────────▼──────────────┐
          │   Backend (Quarkus)    │
          │                        │
          │ ┌────────────────────┐ │
          │ │ Metadata Provider  │ │
          │ │ (JSON/File Based)  │ │  ← serves only metadata
          │ └────────────────────┘ │
          │ ┌────────────────────┐ │
          │ │ Data Controller    │ │
          │ │ - CRUD APIs        │ │
          │ └────────────────────┘ │
          │ ┌────────────────────┐ │
          │ │ Services           │ │
          │ │ - Transform, Logic │ │
          │ └────────────────────┘ │
          └──────────┬────────────┘
                     │
              Actual Business Data
                Stored in PostgreSQL
                (Structured Tables)



⸻

2. Key Clarification in Architecture

Layer	Storage Format	Example
Metadata	JSON (config file/API)	e.g., field names, types, labels
Data	PostgreSQL Tables	e.g., rule, process, dropdown_reason

	•	Metadata defines what to show and how.
	•	Data is stored as structured columns in real tables, enabling:
	•	Efficient querying
	•	Indexing
	•	Relational integrity

⸻

3. Backend Design Roles

Metadata Provider
	•	JSON files or static configuration APIs.
	•	No need to store metadata in DB.
	•	Easy to cache or version control.

Data Controller
	•	REST or GraphQL APIs.
	•	Interfaces directly with relational tables.

⸻

4. Benefits of This Hybrid Design

Benefit	Description
Flexibility	Change UI structure by editing JSON only.
Performance	Real data is stored in efficient SQL tables.
Security	Roles and access can be implemented at API or table level.
Maintainability	Frontend code is generic and doesn’t change per screen.



⸻

Would you like me to visualize this into:
	•	Infographic (block diagram)?
	•	A developer onboarding slide with use-case examples?
	•	A README for your GitHub repo?

Let me know how you’d like to use this!

Thank you — this gives a clear picture of your application’s layered architecture and technology stack.

Below is the updated Application Architecture Overview tailored to your full stack setup, including:
	•	LIT frontend (TS)
	•	GraphQL to Experience API (Kotlin + Quarkus)
	•	REST to Process API (Java + Quarkus + Panache)
	•	PostgreSQL DB

⸻

Full Stack Architecture Overview

1. Technology Flow Diagram

┌──────────────────────────────────────────────────────────────┐
│                        Frontend (LIT + TS)                   │
│      - Dynamic metadata parsing & screen rendering           │
│      - UI talks to backend via GraphQL                       │
│                                                              │
│   [DynamicFormComponent]      [TabLayoutComponent]           │
│              │                        │                      │
└──────────────┼────────────────────────┼──────────────────────┘
               │ GraphQL API Call       │
               ▼                        ▼
        ┌──────────────────────────────────────────┐
        │     Experience API (Kotlin + Quarkus)    │
        │  - GraphQL schema & validation           │
        │  - Metadata routing                      │
        │  - Communicates to Process API via REST  │
        └──────────────────────────────────────────┘
                            │
                            │ REST API Call (JSON)
                            ▼
         ┌────────────────────────────────────────────┐
         │         Process API (Java + Quarkus)       │
         │  - Business logic & transformation layer   │
         │  - Uses Panache ORM                        │
         │  - Interacts directly with PostgreSQL      │
         └────────────────────────────────────────────┘
                              │
                        PostgreSQL
          (Normalized tables holding actual data)



⸻

2. Key Module Responsibilities

Layer	Tech Stack	Responsibilities
Frontend (UI)	Lit + TypeScript	Renders screens dynamically using JSON metadata
Experience API	Kotlin + Quarkus (GraphQL)	Parses GraphQL queries, validates inputs, routes requests
Process API	Java + Quarkus + Panache	Executes business logic, DB interactions, DTO transformation
Database	PostgreSQL	Stores actual structured data (not metadata)



⸻

3. Additional Design Highlights
	•	Metadata JSON (static or from Experience API) defines:
	•	screenType (form or table)
	•	Field names, types, labels, options, validations
	•	Frontend is Generic:
	•	No form/table code is hardcoded.
	•	Rendering is dynamic and reusable for all screens.
	•	Backend is Tiered:
	•	Experience API acts as the controller & validator.
	•	Process API is the executor and database handler.
	•	Data Store is Optimized:
	•	PostgreSQL tables are normalized.
	•	All audit fields (created_by, updated_at, etc.) are handled here.

⸻

4. Benefits of This Architecture

Benefit	Description
Separation of Concerns	Each layer handles one responsibility only.
Scalability	APIs and frontend can be scaled independently.
Reusability	One frontend can serve multiple business modules.
Extensibility	Add new forms/tables by just adding metadata and table in DB.
Performance	Postgres + Panache gives efficient querying with lazy/eager fetch.



⸻




