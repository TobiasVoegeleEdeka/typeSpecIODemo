import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using Http;
using Rest;

@service
@server("http://localhost:8081", "Local server")
namespace TaskApi;

model Task {
  @visibility(Lifecycle.Read)
  id: int32;
  
  @minLength(1)
  title: string;
  
  description?: string;
  completed: boolean;
}

enum Lifecycle {
  Read, Create, Update, Delete
}

model LoginRequest {
  username: string;
  password: string;
}

model LoginResponse {
  token: string;
  user: UserProfile;
}

model UserProfile {
  id: string;
  username: string;
  permissions: string[];
}

@route("/auth")
@tag("Auth")
interface Auth {
  @post login(@body request: LoginRequest): LoginResponse | { @statusCode statusCode: 401 };
}

@route("/tasks")
@tag("Tasks")
interface Tasks {
  @get list(): Task[];
  
  @post create(@body task: Task): Task;
  
  @get read(@path id: int32): Task | { @statusCode statusCode: 404 };
  
  @patch update(@path id: int32, @body task: Task): Task | { @statusCode statusCode: 404 };
  
@delete delete(@path id: int32): void | { @statusCode statusCode: 404 };
}

model Product {
  @visibility(Lifecycle.Read)
  id: string;
  
  @minLength(1)
  name: string;
  
  description?: string;
  
  @minValue(0)
  price: float64;
  
  @minValue(0)
  stock: int32;
}

@route("/inventory")
@tag("Inventory")
interface Inventory {
  @get list(): Product[];
  @post create(@body product: Product): Product;
  @get read(@path id: string): Product | { @statusCode statusCode: 404 };
  @patch update(@path id: string, @body product: Product): Product | { @statusCode statusCode: 404 };
  @delete delete(@path id: string): void | { @statusCode statusCode: 404 };
}

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

@route("/documents")
@tag("Documents")
interface Documents {
  @get list(): Document[];
  @post create(@body doc: Document): Document;
  @get read(@path id: int32): Document | { @statusCode statusCode: 404 };
  @delete delete(@path id: int32): void | { @statusCode statusCode: 404 };
}
