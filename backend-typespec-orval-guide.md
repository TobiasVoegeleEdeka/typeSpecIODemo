# Von der Backend-Schnittstelle über TypeSpec IO zu Orval

> **Ziel:** Eine Java Spring Boot REST API als Single Source of Truth in TypeSpec beschreiben, daraus eine OpenAPI-Spezifikation generieren und diese mit Orval in fertige, typsichere Angular-Services verwandeln.

---

## 📐 Der Gesamtfluss auf einen Blick

```
┌─────────────────────┐      Schritt 1      ┌─────────────────────┐
│  Java Backend        │   ────────────►    │  TypeSpec (.tsp)     │
│  Spring Boot REST    │   (Kontrakt ab-    │  libs/api-contract/  │
│  Controller + Model  │    leiten/schreiben)│  main.tsp           │
└─────────────────────┘                    └─────────────────────┘
          ▲                                           │
          │                               Schritt 2   │  npx tsp compile
          │ Schritt 3.5                               ▼
          │ (Generierung)                  ┌─────────────────────┐
          │                                │  openapi.yaml        │
          │                                │  (auto-generiert)    │
          └────────────────────────────────│                      │
                                           └─────────────────────┘
                                                      │
                                          Schritt 3   │  npx orval
                                                      ▼
                                           ┌─────────────────────┐
                                           │  Angular Services    │
                                           │  + TypeScript Models │
                                           │  (auto-generiert)    │
                                           └─────────────────────┘
```

---

## Schritt 1 – Den Backend-Kontrakt verstehen

Das Backend ist ein **Spring Boot REST Controller**. Wir schauen uns `InventoryController.java` an:

```java
// apps/backend/src/main/java/com/example/demo/controller/InventoryController.java

@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = "http://localhost:4200")
public class InventoryController {

    @GetMapping
    public List<Product> list() { ... }

    @PostMapping
    public Product create(@RequestBody Product product) { ... }

    @GetMapping("/{id}")
    public ResponseEntity<Product> read(@PathVariable String id) { ... }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product productUpdate) { ... }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String id) { ... }
}
```

Das zugehörige Model `Product` (als Java Record):

```java
// apps/backend/src/main/java/com/example/demo/model/Product.java
public record Product(
    String id,
    String name,
    String description,
    double price,
    int stock
) {}
```

**Was wir hier ablesen:**
| HTTP-Verb | Pfad             | Request Body | Response         |
|-----------|------------------|--------------|------------------|
| GET       | `/inventory`     | –            | `Product[]`      |
| POST      | `/inventory`     | `Product`    | `Product`        |
| GET       | `/inventory/{id}`| –            | `Product` / 404  |
| PATCH     | `/inventory/{id}`| `Product`    | `Product` / 404  |
| DELETE    | `/inventory/{id}`| –            | `void` / 404     |

---

## Schritt 2 – Den Kontrakt in TypeSpec beschreiben

### Installation und Projektstruktur

```bash
# TypeSpec Packages installieren (bereits im package.json vorhanden)
npm install --save-dev @typespec/compiler @typespec/http @typespec/rest @typespec/openapi3
```

```
libs/
└── api-contract/
    ├── main.tsp          ← TypeSpec Quelldatei (editieren wir hier)
    ├── tspconfig.yaml    ← Compiler-Konfiguration
    └── openapi.yaml      ← Auto-generiert, NICHT manuell editieren!
```

### `tspconfig.yaml` – Compiler konfigurieren

```yaml
# libs/api-contract/tspconfig.yaml
emit:
  - "@typespec/openapi3"
options:
  "@typespec/openapi3":
    output-file: "{project-root}/openapi.yaml"
```

Das weist den TypeSpec-Compiler an, eine `openapi.yaml` im selben Verzeichnis zu erzeugen.

### `main.tsp` – Die vollständige API beschreiben

```typespec
// libs/api-contract/main.tsp
import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using Http;
using Rest;

// ── Service-Deklaration ──────────────────────────────────────────────────────
@service
@server("http://localhost:8081", "Local server")
namespace TaskApi;

// ── Enum für Sichtbarkeits-Dekoratoren ──────────────────────────────────────
enum Lifecycle {
  Read, Create, Update, Delete
}

// ── Models ───────────────────────────────────────────────────────────────────

// Auth
model LoginRequest {
  username: string;
  password: string;
}

model UserProfile {
  id: string;
  username: string;
  permissions: string[];
}

model LoginResponse {
  token: string;
  user: UserProfile;
}

// Inventory – entspricht exakt dem Java Record Product
model Product {
  @visibility(Lifecycle.Read)   // id ist read-only: wird vom Backend gesetzt
  id: string;

  @minLength(1)
  name: string;

  description?: string;         // optional – darf null sein

  @minValue(0)
  price: float64;

  @minValue(0)
  stock: int32;
}

// Tasks
model Task {
  @visibility(Lifecycle.Read)
  id: int32;

  @minLength(1)
  title: string;

  description?: string;
  completed: boolean;
}

// Documents
model Document {
  @visibility(Lifecycle.Read)
  id: int32;

  @minLength(1)
  name: string;

  @minLength(1)
  content: string;

  @visibility(Lifecycle.Read)
  createdAt?: utcDateTime;
}

// ── Interfaces (Routen) ──────────────────────────────────────────────────────

@route("/auth")
@tag("Auth")
interface Auth {
  @post login(@body request: LoginRequest): LoginResponse | { @statusCode statusCode: 401 };
}

@route("/tasks")
@tag("Tasks")
interface Tasks {
  @get  list(): Task[];
  @post create(@body task: Task): Task;
  @get  read(@path id: int32): Task | { @statusCode statusCode: 404 };
  @patch update(@path id: int32, @body task: Task): Task | { @statusCode statusCode: 404 };
  @delete delete(@path id: int32): void | { @statusCode statusCode: 404 };
}

@route("/inventory")
@tag("Inventory")
interface Inventory {
  @get   list(): Product[];
  @post  create(@body product: Product): Product;
  @get   read(@path id: string): Product | { @statusCode statusCode: 404 };
  @patch update(@path id: string, @body product: Product): Product | { @statusCode statusCode: 404 };
  @delete delete(@path id: string): void | { @statusCode statusCode: 404 };
}

@route("/documents")
@tag("Documents")
interface Documents {
  @get   list(): Document[];
  @post  create(@body doc: Document): Document;
  @get   read(@path id: int32): Document | { @statusCode statusCode: 404 };
  @delete delete(@path id: int32): void | { @statusCode statusCode: 404 };
}
```

### TypeSpec-Konzepte kurz erklärt

| TypeSpec-Syntax | Bedeutung | Java-Äquivalent |
|---|---|---|
| `@service` | Markiert den Namespace als API-Service | `@SpringBootApplication` |
| `@server(url, desc)` | Basis-URL der API | `server.port` in `application.yml` |
| `@route("/inventory")` | URL-Präfix | `@RequestMapping("/inventory")` |
| `@tag("Inventory")` | OpenAPI-Gruppe (für UI + Orval-Splitting) | – |
| `@get`, `@post`, `@patch`, `@delete` | HTTP-Verben | `@GetMapping`, `@PostMapping` usw. |
| `@path id: string` | Path-Variable | `@PathVariable String id` |
| `@body task: Task` | Request-Body | `@RequestBody Task task` |
| `@visibility(Lifecycle.Read)` | Feld nur in Responses sichtbar (z.B. id) | – |
| `model?:` (Fragezeichen) | Optional / nullable | `@Nullable` / `Optional<>` |
| `\| { @statusCode 404 }` | Union-Type für Fehlerresponse | `ResponseEntity<>` mit Status |

---

## Schritt 3 – OpenAPI generieren

```bash
# Im Projektroot ausführen:
npx tsp compile libs/api-contract/main.tsp

# Oder als Nx-Target (falls konfiguriert):
npx nx run api-contract:compile
```

Das erzeugt `libs/api-contract/openapi.yaml`. **Nicht manuell editieren!**

### Ausschnitt aus der generierten `openapi.yaml`

```yaml
openapi: 3.0.0
info:
  title: (title)
  version: 0.0.0
tags:
  - name: Auth
  - name: Tasks
  - name: Inventory
  - name: Documents

paths:
  /inventory:
    get:
      operationId: Inventory_list
      responses:
        '200':
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Product'
      tags: [Inventory]

    post:
      operationId: Inventory_create
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Product'
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
      tags: [Inventory]

  /inventory/{id}:
    get:
      operationId: Inventory_read
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
        '404':
          description: The server cannot find the requested resource.
      tags: [Inventory]

    patch:
      operationId: Inventory_update
      # ...
    delete:
      operationId: Inventory_delete
      # ...

components:
  schemas:
    Product:
      type: object
      required: [id, name, price, stock]
      properties:
        id:
          type: string
        name:
          type: string
          minLength: 1
        description:
          type: string
        price:
          type: number
          format: double
          minimum: 0
        stock:
          type: integer
          format: int32
          minimum: 0

servers:
  - url: http://localhost:8081
    description: Local server
```

---

## Schritt 3.5 – Backend-Code generieren (Java/Spring)

Um sicherzustellen, dass das Backend exakt den in TypeSpec definierten Kontrakt einhält, generieren wir die Modelle und Controller-Interfaces direkt aus der `openapi.yaml`.

### 1. Maven-Konfiguration (`pom.xml`)

Fügen Sie das `openapi-generator-maven-plugin` hinzu. Wir nutzen die Option `interfaceOnly`, damit nur die Interfaces generiert werden, die Sie dann in Ihren Controllern implementieren.

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.11.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/../../libs/api-contract/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.example.demo.api</apiPackage>
                <modelPackage>com.example.demo.api.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                    <useTags>true</useTags>
                    <openApiNullable>false</openApiNullable>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 2. Code generieren

Führen Sie den Befehl im Root oder direkt im Backend-Verzeichnis aus:

```bash
# Via Nx (falls konfiguriert)
npx nx run backend:generate-api

# Oder direkt via Maven
mvn clean compile
```

Die Dateien werden in `target/generated-sources/openapi/` erzeugt.

### 3. Implementation im Controller

Anstatt die Methoden manuell zu definieren, lassen Sie Ihren Controller einfach das generierte Interface implementieren.

```java
@RestController
public class InventoryController implements InventoryApi { // ← Generiertes Interface

    @Override
    public ResponseEntity<List<Product>> inventoryList() {
        // Logik hier implementieren
        return ResponseEntity.ok(productService.findAll());
    }

    @Override
    public ResponseEntity<Product> inventoryRead(String id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

> [!TIP]
> Durch die Nutzung der generierten Interfaces erhalten Sie sofort Compile-Fehler, wenn sich der Kontrakt in TypeSpec ändert und Sie die Implementation noch nicht angepasst haben.

---

---

## Schritt 4 – Orval konfigurieren

### `orval.config.js` im Projekt-Root

```javascript
// orval.config.js
module.exports = {
  task: {
    input: {
      // Quelle: die von TypeSpec generierte OpenAPI-Spec
      target: './libs/api-contract/openapi.yaml',
    },
    output: {
      // Pro Tag (Auth/Tasks/Inventory/Documents) eine eigene Datei
      mode: 'tags-split',

      // Haupt-Ausgabepfad für Services
      target: './libs/task-client/src/lib/generated/task-client.ts',

      // TypeScript-Interfaces / Typen (separate Ordner)
      schemas: './libs/task-client/src/lib/generated/model',

      // Angular HttpClient verwenden (statt fetch/axios)
      client: 'angular',

      // MSW Mock-Handler mitgenerieren (für Tests)
      mock: true,

      // Prettier-Formatierung auf generierten Code anwenden
      prettier: true,
    },
  },
};
```

### Orval ausführen

```bash
# Generiert alle Angular-Services + Models + Mocks
npx orval

# Oder gegen eine spezifische Konfiguration:
npx orval --config orval.config.js
```

---

## Schritt 5 – Der generierte Angular-Code

### Dateistruktur nach `npx orval`

```
libs/task-client/src/lib/generated/
├── model/                        ← TypeScript Interfaces
│   ├── product.ts
│   ├── task.ts
│   ├── document.ts
│   ├── loginRequest.ts
│   ├── loginResponse.ts
│   └── userProfile.ts
├── auth/
│   ├── auth.service.ts           ← Angular Service für /auth
│   └── auth.msw.ts               ← MSW Mock-Handler
├── inventory/
│   ├── inventory.service.ts      ← Angular Service für /inventory
│   └── inventory.msw.ts
├── tasks/
│   ├── tasks.service.ts
│   └── tasks.msw.ts
└── documents/
    ├── documents.service.ts
    └── documents.msw.ts
```

### Generiertes TypeScript-Interface (Model)

```typescript
// libs/task-client/src/lib/generated/model/product.ts
// Generiert von orval v8.8.0 🍺 – NICHT manuell editieren!

export interface Product {
  id: string;
  name: string;
  description?: string;
  price: number;
  stock: number;
}
```

### Generierter Angular-Service (Ausschnitt `InventoryService`)

```typescript
// libs/task-client/src/lib/generated/inventory/inventory.service.ts
// Generiert von orval v8.8.0 🍺 – NICHT manuell editieren!

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type { Product } from '../model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private readonly http = inject(HttpClient);

  // GET /inventory → Product[]
  inventoryList<TData = Product[]>(options?: HttpClientBodyOptions): Observable<TData>;
  inventoryList<TData = Product[]>(options?: ...): Observable<...>;
  inventoryList<TData = Product[]>(options?: HttpClientObserveOptions) {
    return this.http.get<TData>(`/inventory`, { ...options, observe: 'body' });
  }

  // POST /inventory → Product
  inventoryCreate<TData = Product>(product: Product, options?: ...): Observable<TData> {
    return this.http.post<TData>(`/inventory`, product, { ...options, observe: 'body' });
  }

  // GET /inventory/:id → Product
  inventoryRead<TData = Product>(id: string, options?: ...): Observable<TData> {
    return this.http.get<TData>(`/inventory/${id}`, { ...options, observe: 'body' });
  }

  // PATCH /inventory/:id → Product
  inventoryUpdate<TData = Product>(id: string, product: Product, options?: ...): Observable<TData> {
    return this.http.patch<TData>(`/inventory/${id}`, product, { ...options, observe: 'body' });
  }

  // DELETE /inventory/:id → void
  inventoryDelete<TData = void>(id: string, options?: ...): Observable<TData> {
    return this.http.delete<TData>(`/inventory/${id}`, { ...options, observe: 'body' });
  }
}
```

---

## Schritt 6 – Den generierten Service im Frontend verwenden

### Pattern: Einen Wrapper-Service schreiben

> [!TIP]
> Man injiziert **nie** den generierten Service direkt in Komponenten. Stattdessen schreibt man einen schlanken Wrapper-Service, der Geschäftslogik kapselt.

```typescript
// apps/frontend/src/app/services/auth.service.ts
import { Injectable, signal, inject } from '@angular/core';
import {
  AuthService as GeneratedAuthService,  // ← generierter Orval-Service
  LoginRequest,
  UserProfile,
  LoginResponse
} from '@app/task-client';              // ← NX Lib path alias
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Generierter Service wird injiziert
  private generatedAuth = inject(GeneratedAuthService);

  // Angular Signals für reaktiven State
  currentUser = signal<UserProfile | null>(null);
  token = signal<string | null>(null);

  login(credentials: LoginRequest) {
    return this.generatedAuth.authLogin(credentials).pipe(
      // Side-Effect: State aktualisieren
      tap((response: LoginResponse) => {
        this.currentUser.set(response.user);
        this.token.set(response.token);
      })
    );
  }

  logout() {
    this.currentUser.set(null);
    this.token.set(null);
  }

  hasPermission(permission: string): boolean {
    const user = this.currentUser();
    return user ? user.permissions.includes(permission) : false;
  }
}
```

### Inventory-Service-Beispiel für eine Komponente

```typescript
// apps/frontend/src/app/services/inventory.service.ts
import { Injectable, inject } from '@angular/core';
import { InventoryService as GeneratedInventoryService, Product } from '@app/task-client';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private api = inject(GeneratedInventoryService);

  getAll(): Observable<Product[]> {
    return this.api.inventoryList();
  }

  getById(id: string): Observable<Product> {
    return this.api.inventoryRead(id);
  }

  create(product: Omit<Product, 'id'>): Observable<Product> {
    // id wird vom Backend gesetzt – wir übergeben einen Partial
    return this.api.inventoryCreate(product as Product);
  }

  update(id: string, changes: Partial<Product>): Observable<Product> {
    return this.api.inventoryUpdate(id, changes as Product);
  }

  delete(id: string): Observable<void> {
    return this.api.inventoryDelete(id);
  }
}
```

```typescript
// apps/frontend/src/app/components/inventory-list/inventory-list.component.ts
import { Component, inject, OnInit, signal } from '@angular/core';
import { InventoryService } from '../../services/inventory.service';
import { Product } from '@app/task-client';

@Component({
  selector: 'app-inventory-list',
  template: `
    @if (loading()) {
      <p>Lade Produkte...</p>
    } @else {
      <ul>
        @for (product of products(); track product.id) {
          <li>{{ product.name }} – {{ product.price | currency:'EUR' }} (Lager: {{ product.stock }})</li>
        }
      </ul>
    }
  `
})
export class InventoryListComponent implements OnInit {
  private inventoryService = inject(InventoryService);

  products = signal<Product[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.inventoryService.getAll().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
```

---

## Schritt 7 – Vollständiger Workflow als Cheat Sheet

```bash
# 1. Backend-Schnittstelle in TypeSpec beschreiben
#    → libs/api-contract/main.tsp editieren

# 2. OpenAPI generieren
npx nx run api-contract:compile  # (oder npx tsp compile ...)

# 3. Backend-Code generieren (Java Models + Interfaces)
npx nx run backend:generate-api  # (oder mvn compile)

# 4. Angular-Services + Models generieren
npx nx run task-client:generate  # (oder npx orval)

# 5. Implementation im Backend / Frontend nutzen
```

> [!IMPORTANT]
> **Goldene Regel:** Nur `main.tsp` editieren. `openapi.yaml`, die Java-Interfaces in `target/` und alles unter `generated/` im Frontend werden **immer** überschrieben. Logik gehört im Backend in den Controller (der das Interface implementiert) und im Frontend in die Wrapper-Services.

---

## Warum dieser Workflow?

| Problem ohne diese Chain | Lösung |
|---|---|
| Frontend und Backend driften auseinander – unterschiedliche Feldnamen, vergessene Felder | TypeSpec als **Single Source of Truth** |
| Manuell geschriebene HTTP-Calls sind fehleranfällig | Orval generiert **typsichere** Angular Services |
| API-Änderungen brechen den Frontend-Build still | TypeScript-Compiler schlägt bei Typ-Unstimmigkeiten an |
| Mocks für Tests manuell pflegen | Orval generiert MSW Mock-Handler automatisch mit |
| Dokumentation veraltet | OpenAPI-Spec immer aktuell, Swagger UI immer korrekt |
