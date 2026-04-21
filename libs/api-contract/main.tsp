import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using Http;
using Rest;

@service
@server("http://localhost:8081", "Local server")
namespace TaskApi;

// ── Common Error Models ─────────────────────────────────────────────────────

@error
model ErrorResponse {
  code: string;
  message: string;
  details?: string[];
}

@error
model BadRequestError {
  @statusCode statusCode: 400;
  @body body: ErrorResponse;
}

@error
model NotFoundError {
  @statusCode statusCode: 404;
  @body body: ErrorResponse;
}

@error
model UnauthorizedError {
  @statusCode statusCode: 401;
  @body body: ErrorResponse;
}

// ── Shared Types ─────────────────────────────────────────────────────────────

enum Lifecycle {
  Read, Create, Update, Delete
}

// ── Models ───────────────────────────────────────────────────────────────────

model Task {
  @visibility(Lifecycle.Read)
  id: int32;
  
  @minLength(3)
  @maxLength(100)
  title: string;
  
  @maxLength(500)
  description?: string;
  
  completed: boolean;
}

model LoginRequest {
  @minLength(3)
  @maxLength(50)
  username: string;

  @minLength(8)
  @maxLength(128)
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

model Product {
  @visibility(Lifecycle.Read)
  id: string;
  
  @minLength(2)
  @maxLength(100)
  name: string;
  
  @maxLength(1000)
  description?: string;
  
  @minValue(0)
  @maxValue(1000000)
  price: float64;
  
  @minValue(0)
  stock: int32;
}

model Document {
  @visibility(Lifecycle.Read)
  id: int32;
  
  @minLength(1)
  @maxLength(255)
  name: string;
  
  @minLength(1)
  content: string;
  
  @visibility(Lifecycle.Read)
  createdAt?: utcDateTime;
}

model Wegbeschreibung {
  @visibility(Lifecycle.Read)
  id: string;

  @minLength(2)
  @maxLength(100)
  startPoint: string;

  @minLength(2)
  @maxLength(100)
  destination: string;

  @minLength(2)
  Person: string;
  
  @minValue(0)
  @maxValue(1440) // Max 24h
  estimatedMinutes: int32;
}

// ── Interfaces ───────────────────────────────────────────────────────────────

@route("/auth")
@tag("Auth")
interface Auth {
  @post login(@body request: LoginRequest): LoginResponse | UnauthorizedError | BadRequestError;
}

@route("/tasks")
@tag("Tasks")
interface Tasks {
  @get list(): Task[];
  @post create(@body task: Task): Task | BadRequestError;
  @get read(@path id: int32): Task | NotFoundError;
  @patch update(@path id: int32, @body task: Task): Task | NotFoundError | BadRequestError;
  @delete delete(@path id: int32): void | NotFoundError;
}

@route("/inventory")
@tag("Inventory")
interface Inventory {
  @get list(): Product[];
  @post create(@body product: Product): Product | BadRequestError;
  @get read(@path id: string): Product | NotFoundError;
  @patch update(@path id: string, @body product: Product): Product | NotFoundError | BadRequestError;
  @delete delete(@path id: string): void | NotFoundError;
}

@route("/documents")
@tag("Documents")
interface Documents {
  @get list(): Document[];
  @post create(@body doc: Document): Document | BadRequestError;
  @get read(@path id: int32): Document | NotFoundError;
  @delete delete(@path id: int32): void | NotFoundError;
}

@route("/directions")
@tag("Directions")
interface Directions {
  @get list(): Wegbeschreibung[];
  @post create(@body directions: Wegbeschreibung): Wegbeschreibung | BadRequestError;
  @get read(@path id: string): Wegbeschreibung | NotFoundError;
  @delete delete(@path id: string): void | NotFoundError;
}
