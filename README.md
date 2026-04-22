# Smart Campus Sensor & Room Management API

**Module:** 5COSC022C — Client-Server Architectures (2025/26)  
**Module Leader:** Mr. Hamed Hamzeh  
**Student:** Savindu John  
**Student ID:** 20240043 / w2119835

---

## API Overview

The Smart Campus API is a RESTful web service for managing campus rooms and the sensors deployed within them. It is built using **JAX-RS (Jersey 3.1.10)** and deployed as a **WAR** on **Apache Tomcat 10.1**.

The API manages three core resources:

- **Rooms** — physical campus spaces (e.g. "Library Quiet Study") with a name, capacity, and a list of assigned sensors.
- **Sensors** — devices installed in rooms (e.g. temperature, CO2, occupancy sensors) with a type, status, and current reading value.
- **Sensor Readings** — historical measurement records for each sensor, managed as a sub-resource under the parent sensor.

### Key Features

- Full CRUD operations for rooms and sensors
- Sub-resource pattern for sensor readings (`/sensors/{id}/readings`)
- Query parameter filtering (`/sensors?type=CO2`)
- API key authentication via `X-API-KEY` header
- Custom exception mappers returning clean JSON errors (409, 422, 403, 500)
- Request/response logging filter for observability
- HATEOAS discovery endpoint at `/api/v1/info`
- In-memory data storage using `ConcurrentHashMap` (no database)

### Technology Stack

- Java 21
- JAX-RS (Jersey 3.1.10)
- Apache Tomcat 10.1.54
- Maven (WAR packaging)
- Jackson (JSON serialization)

---

## How to Build and Run

### Prerequisites

- **Java JDK 21** installed and `JAVA_HOME` set
- **Apache Maven** installed
- **Apache Tomcat 10.1.x** downloaded and extracted (e.g. to `C:\apache-tomcat-10.1`)
- **NetBeans IDE** (recommended, as the project is configured for it)

### Option A — Run via NetBeans (Recommended)

1. Clone this repository:
   ```
   git clone git clone https://github.com/savindu204/smart-campus-api.git
   ```
2. Open **NetBeans** → File → Open Project → select the cloned folder
3. Go to **Tools → Servers → Add Server** → choose "Apache Tomcat" → point to your Tomcat directory
4. Right-click the project → **Properties → Run** → set Server to your Tomcat instance
5. Click the **green Run button** (or press F6)
6. The API will be available at: `http://localhost:8080/api/v1/info`

### Option B — Manual Deployment

1. Clone and build the project:
   ```
   git clone git clone https://github.com/savindu204/smart-campus-api.git
   cd smart-campus-api
   mvn clean package
   ```
2. Copy the WAR file to Tomcat:
   ```
   cp target/smart-campus-api.war /path/to/tomcat/webapps/
   ```
3. Start Tomcat:
   ```
   /path/to/tomcat/bin/startup.sh    # Linux/Mac
   /path/to/tomcat/bin/startup.bat   # Windows
   ```
4. The API will be available at: `http://localhost:8080/smart-campus-api/api/v1/info`

---

## Project Structure

```
src/main/java/com/smartcampus/
├── SmartCampusApplication.java      # JAX-RS Application class (@ApplicationPath)
├── model/
│   ├── Room.java                    # Room POJO
│   ├── Sensor.java                  # Sensor POJO
│   ├── SensorReading.java           # SensorReading POJO
│   └── ErrorResponse.java           # Standard error response structure
├── resource/
│   ├── DiscoveryResource.java       # GET /api/v1/info (HATEOAS)
│   ├── RoomResource.java            # /api/v1/rooms endpoints
│   ├── SensorResource.java          # /api/v1/sensors endpoints
│   └── SensorReadingResource.java   # Sub-resource for /sensors/{id}/readings
├── store/
│   └── DataStore.java               # In-memory ConcurrentHashMap storage
├── exception/
│   ├── RoomNotEmptyException.java           # Thrown on delete room with sensors
│   ├── LinkedResourceNotFoundException.java # Thrown on invalid roomId reference
│   └── SensorUnavailableException.java      # Thrown on reading to MAINTENANCE sensor
├── mapper/
│   ├── RoomNotEmptyMapper.java              # → 409 Conflict
│   ├── LinkedResourceNotFoundMapper.java    # → 422 Unprocessable Entity
│   ├── SensorUnavailableMapper.java         # → 403 Forbidden
│   └── GlobalExceptionMapper.java           # → 500 Internal Server Error
└── filter/
    ├── AuthFilter.java              # API key authentication filter
    └── LoggingFilter.java           # Request/response logging filter

src/main/webapp/WEB-INF/
└── web.xml                          # Minimal web descriptor for Tomcat
```

---

## Sample curl Commands

All endpoints (except `/api/v1/info`) require the header `X-API-KEY: smartcampus-2026`.

### 1. Discovery Endpoint (no auth required)

```bash
curl -X GET http://localhost:8080/api/v1/info
```

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: smartcampus-2026" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

### 3. Get All Rooms

```bash
curl -X GET http://localhost:8080/api/v1/rooms \
  -H "X-API-KEY: smartcampus-2026"
```

### 4. Create a Sensor (linked to a room)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: smartcampus-2026" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

### 5. Get Sensors Filtered by Type

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature" \
  -H "X-API-KEY: smartcampus-2026"
```

### 6. Post a Sensor Reading (sub-resource)

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: smartcampus-2026" \
  -d '{"value":23.5}'
```

### 7. Get Reading History for a Sensor

```bash
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "X-API-KEY: smartcampus-2026"
```

### 8. Delete a Room (success — no sensors)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/ENG-101 \
  -H "X-API-KEY: smartcampus-2026"
```

### 9. Delete a Room with Sensors (409 Conflict)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "X-API-KEY: smartcampus-2026"
```

### 10. Request Without API Key (401 Unauthorized)

```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

---

## API Endpoints Summary

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/info` | Discovery / HATEOAS metadata | No |
| GET | `/api/v1/rooms` | List all rooms | Yes |
| POST | `/api/v1/rooms` | Create a new room | Yes |
| GET | `/api/v1/rooms/{roomId}` | Get room by ID | Yes |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room | Yes |
| GET | `/api/v1/sensors` | List all sensors (optional `?type=` filter) | Yes |
| POST | `/api/v1/sensors` | Register a new sensor | Yes |
| GET | `/api/v1/sensors/{sensorId}` | Get sensor by ID | Yes |
| DELETE | `/api/v1/sensors/{sensorId}` | Delete a sensor | Yes |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get reading history | Yes |
| POST | `/api/v1/sensors/{sensorId}/readings` | Record a new reading | Yes |

---

## Report — Answers to Coursework Questions

### Part 1: Service Architecture & Setup

#### 1.1 Project & Application Configuration

**Question:** *Explain the default lifecycle of a JAX-RS Resource class. Is a new instance created for every request, or is it a singleton? How does this affect your in-memory data?*

JAX-RS, by default, will generate a new instance of each resource class with each and every incoming HTTP request. Then when two clients make a GET request to /api/v1/rooms, one will receive a fresh RoomResource object. It is not a singleton - the objects are absolutely distinct.

This is significant to the way we store data. Because the resources are discarded once the request is completed, we can not keep our rooms and sensors as instance variables in those classes - the information would just disappear. We instead made our own DataStore class which has static fields in which the lifespan of the fields is the life of the application, not just one request.

We prefer using a ConcurrentHashMap over a regular one to make it thread safe. Since two or more requests can be received at the same time (one on each thread), a normal HashMap may become corrupted - two threads attempting to set one of the rooms at once may result in lost updates or even a deadlock within the hash table itself. ConcurrentHashMap takes care of all this automatically by locking only the parts of the map that are being updated, allowing concurrent writes to be performed without us needing to introduce explicit synchronisation code.

In brief, the per-request lifecycle implies that shared state cannot exist within a resource class, and ConcurrentHashMap guarantees that shared state remains consistent even when accessed concurrently.

#### 1.2 The "Discovery" Endpoint

**Question:** *Why is HATEOAS considered a hallmark of advanced RESTful design? How does it benefit client developers?*

Hypermedia As The Engine Of Application State (HATEOAS) is regarded to be the most mature level of REST since it makes the API self-descriptive. The API itself informs developers about the existence of endpoints, without having to read documentation to discover them, via links in the responses.

In our implementation, the GET /api/v1/info endpoint will give a JSON object with a resources map where there will be room: /api/v1/rooms and sensor: /api/v1/sensors. The first-time developer who reaches such an endpoint can see at a glance where to continue without looking at documentation.

This has a number of practical advantages. First, it lowers the level of coupling - in case the URL scheme is adapted in the future, any client that uses links dynamically will continue to work without modification. Second, it enhances discoverability - a new developer only has to follow the links posted by the root endpoint to find out about the entire API. Third, it favors evolvability - as new resources are added later they simply show up as new links, and well-written clients will automatically grab them. The links in the response are always up to date as opposed to the case of static documentation which may become outdated.

---

### Part 2: Room Management

#### 2.1 Room Resource Implementation

**Question:** *What are the implications of returning only IDs versus full room objects? Consider network bandwidth and client-side processing.*

The two approaches have a definite trade-off.

Sending back just IDs results in a much smaller response payload, which conserves bandwidth - this can be helpful when the collection is large or the client has a slow connection. The client must however follow up with an independent GET request to each room it actually requires information about. Assuming that there are 50 rooms then there will be 1 request of the list and 50 requests separately. This is referred to as the N+1 problem and it generates a lot of unwarranted round trips and latency.

Sending complete objects causes the response to be larger, yet the client obtains all it requires in a single request. No follow-up requests are made, and hence the general latency is significantly reduced and code on the client side is less complicated.

Our implementation uses full room objects (returned by a GET /api/v1/rooms) since the data is not too big (campus rooms will not be in the millions) and the ability to access all the fields simultaneously is more convenient than a smaller payload. Very large collections would be well compromised with pagination - full objects on smaller pages.

#### 2.2 Room Deletion & Safety Logic

**Question:** *Is DELETE idempotent in your implementation? What happens if the same DELETE request is sent multiple times?*

Yes, our DELETE implementation is idempotent. Idempotency implies that repeated identical requests will result in an identical server state, but the response can be different.

The step by step process is as follows: the initial request a client makes towards the server is a request to delete the room in the data store, and the room is located, deleted, and the server replies with a 200 OK, and a confirmation message. When the client re-posts the identical request, DataStore.rooms.get("LIB-301") will result in the null value since the room is no longer present and therefore the client would get 404 Not Found.

The important thing is that the server state following the first DELETE is the same server state following the second one - in both instances, there is no room LIB-301. This code is not 200 but 404, which does not violate idempotency since idempotency applies to server-side state, rather than the response body. This aligns with the HTTP specification (RFC 7231), which establishes DELETE as an idempotent method.

---

### Part 3: Sensor Operations & Linking

#### 3.1 Sensor Resource & Integrity

**Question:** *What happens if a client sends data in text/plain or application/xml instead of application/json to a @Consumes(APPLICATION_JSON) endpoint?*

The annotation of the method is an intrinsic part of JAX-RS that a request body must have a content-type of application/json to be accepted by the POST method. When a client request has a different content type such as text/plain or application/xml, JAX-RS will not even invoke our method. The framework itself is an interrupt that happens at the stage of routing and automatically generates a response of the type of HTTP 415 Unsupported Media Type before our code is ever hit.

This is due to the built-in content negotiation of JAX-RS. Upon receipt of a request, the runtime will verify that the Content-Type header corresponds to one of the annotation(s) on the corresponding resource methods. When no match is found, it is determined that there is no way to process that set of path and content type and 415 is returned.

This is handy in that it provides a contract on framework level - we do not have to create any manual validation code to verify the content type ourselves. It further avoids muting errors which would arise when Jackson (our JSON library) attempted to read XML or plain text as JSON, which would probably raise an unhelpful deserialization exception.

#### 3.2 Filtered Retrieval & Search

**Question:** *Why is @QueryParam for filtering better than putting the type in the URL path?*

We apply to the use of the @QueryParam with type as a way of allowing the client to filter sensors by using the GET /api/v1/sensors?type=CO2. The other option - inserting the filter as in the request such as GET /api/v1/sensors/type/CO2 - is typically deemed as poorer due to a number of reasons.

One, in REST, a path segment is expected to be a resource or a resource identifier. We have the sensors collection (/api/v1/sensors) and a particular sensor (/api/v1/sensors/TEMP-001). Putting in the path: /type/CO2 gives a misleading impression of the existence of sub-resources: type and CO2, when in fact, they are merely search terms.

Second, query parameters are optional in nature. To retrieve all sensors, a client may call GET /api/v1/sensors, or to filter, add a type parameter such as CO2. In path-based design, you would have to define separate route definitions with the filtered and unfiltered versions, making it unnecessarily complex.

Third, query parameters can be composed. Should we wish to further refine the filters in future (e.g. ?type=CO2&status=ACTIVE), it is easy - you simply add another @QueryParam. Path segments create even more cumbersome URLs such as /sensors/type/CO2/status/ACTIVE.

Lastly, query parameters are in compliance with web standards. Query strings are considered the standard way to parameterise requests by HTML forms, HTTP caches and proxy servers alike.

---

### Part 4: Deep Nesting with Sub-Resources

#### 4.1 The Sub-Resource Locator Pattern

**Question:** *Discuss the architectural benefits of the Sub-Resource Locator pattern compared to putting everything in one massive controller class.*

Sub-Resource Locator pattern allows us to outsource a path containing the sub-resource to another class. In our implementation, there is a method of SensorResource with an annotation of @Path("/{sensorId}/readings") which returns a new instance of SensorReadingResource where we pass sensorId through the constructor. Note no annotation of an HTTP verb (@GET, @POST, etc.) with this approach - it is just a locator, which delegates control.

This trend assists in dealing with complexity in a number of ways. First, it implements the separation of concerns: sensor CRUD operations are managed by SensorResource whereas sensor reading history is managed by SensorReadingResource. One, single responsibility per class. In the absence of this pattern, the two sets of endpoints would be in a single class, which would become large and difficult to maintain as the API changes.

Second, it encourages reusability. The class, SensorReadingResource, is a self-contained one that can be theoretically reused under another parent resource in case of necessity.

Thirdly, it reflects the resource hierarchy in the code structure. The pattern of the URLs is /sensors/{id}/readings, which is directly translated into the delegation of the class, which makes the base easy to navigate.

Fourth, it makes testing simple. All sub-resource classes can be instantiated directly with a known sensorId, and exercised individually, without the complete JAX-RS routing stack.

In comparison, one huge controller class that deals with such paths as /sensors/{id}, /sensors/{id}/readings, /sensors/{id}/readings/{rid}, etc. is a maintenance nightmare and contravenes the Single Responsibility Principle.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

#### 5.2 Dependency Validation (422 Unprocessable Entity)

**Question:** *Why is HTTP 422 more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?*

The HTTP 404 status indicates that the resource which was identified by the request URL was not found. The HTTP 422 is the server was aware of the request and the syntax was correct yet cannot process the data in the body.

When a client POSTs to the endpoint at /api/v1/sensors with a body of roomId: "LIB-999" and the room does not exist the endpoint of /api/v1/sensors is a valid endpoint - the sensors collection endpoint was discovered and is known to be valid. It depends on the data within the request body rather than the URL.

In this case, 404 would be a false impression since it would mean that the endpoint itself, namely, the /api/v1/sensors, does not exist, which is not the case. The reason why 422 is more correct is that it conveys the information: you have a well-structured JSON and the endpoint is present, but the information is about a room that is not in the system and therefore cannot be processed.

This difference assists client developers to diagnose issues quicker. A 404 causes them to do a check on their URL, and a 422 causes them to check the request body with invalid references. We then, in our implementation, throw a LinkedResourceNotFoundException that is handled by LinkedResourceNotFoundMapper and sent back as a 422 with a clear JSON message of what linked resource could not be found.

#### 5.4 The Global Safety Net (500)

**Question:** *From a cybersecurity standpoint, what are the risks of exposing internal Java stack traces to external API consumers?*

Exposing raw Java stack traces is a serious security risk because they reveal a wealth of technical information that an attacker can use to plan targeted attacks.

Specifically, a stack trace can expose: fully qualified class names (e.g. `com.smartcampus.resource.SensorResource`), which reveal the internal package structure and naming conventions of the application; exact line numbers where errors occurred, which help an attacker understand which code paths are vulnerable; and the names and versions of libraries and frameworks being used (e.g. Jersey 3.1.10, Jackson 2.x), which an attacker can cross-reference against public CVE databases to find known vulnerabilities with ready-made exploits.

This is why we implemented a `GlobalExceptionMapper` that catches all `Throwable` exceptions and returns a generic message: "An unexpected error occurred. Please try again later." with a 500 status code. The real exception details are logged internally using `java.util.logging.Logger` so that developers can still debug issues, but the client never sees them. This follows the principle of "fail securely" - give the client only what they need to know, while keeping full diagnostic information on the server side.

#### 5.5 API Request & Response Logging Filters

**Question:** *Why use JAX-RS filters for logging instead of manually adding Logger.info() in every resource method?*

Filter-based logging via JAX-RS filters (by implementing ContainerRequestFilter and ContainerResponseFilter) is an improvement over sprinkling the Logger.info() calls throughout each resource method since filters represent a one-stop, centralised solution to a cross-cutting concern.

With no filters, a developer would have to insert logging statements in the start and end of each of the individual methods - getAllRooms, getRoomById, createSensor etc. This is redundant, against the DRY (Don't Repeat Yourself) principle, and likely to be inaccurate since a developer may forget to log a new method. In case the logging format should change, then all methods would have to be changed separately.

In our case with our `LoggingFilter` logging logic is implemented just once, and is automatically applied to all requests and responses passing through the JAX-RS pipeline - even any additional endpoints that might be added later. The filter uses ContainerRequestFilter to record the HTTP method and the URL prior to the request being processed and ContainerResponseFilter to record the status code once the response is created. This ensures that there is uniformity and completeness in the logging of the entire API.

It also ensures that infrastructure issues (logging) are independent of business logic (room and sensor management) and therefore the code is easier to read, maintain, and test. This pattern is simply Aspect-Oriented Programming - addressing cross-cutting issues in a manner that is modular instead of sprinkling them across the application.
