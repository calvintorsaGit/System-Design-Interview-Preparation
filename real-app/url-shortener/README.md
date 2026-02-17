# How This URL Shortener Actually Works (Explained Simply)

Imagine you are a librarian, and people give you **really long book titles**.

## 1. The Core Idea
You need to give them a **short ticket number** so they can find the book later.

- **User**: "Here is *'The Lord of the Rings: The Fellowship of the Ring (Special Edition)'*."
- **You**: "Okay, take ticket `#abc123`."

Later:
- **User**: "I have ticket `#abc123`."
- **You**: "Ah, that means *'The Lord of the Rings...'*! Here you go."

This is exactly what our app does.

---

## 2. The Components (Like Lego Blocks)

### 🧱 The Website (Frontend)
- **What it does**: The pretty face. You type a long URL, click a button, and get a short one.
- **Tech**: React (Vite).

### 🧠 The Brain (Backend)
- **What it does**: Does the actual work.
  - Takes the long URL.
  - Generates a unique ID (like a ticket number).
  - Saves it in the database.
- **Tech**: Java (Spring Boot).

### 🗄️ The Filing Cabinet (Database)
- **What it does**: Remembers everything forever.
- **Tech**: PostgreSQL.
- **Data looks like**: `abc123` -> `google.com`.

### ⚡ The Cheat Sheet (Cache)
- **What it does**: Keeps popular URLs in memory so we don't have to walk to the filing cabinet every time.
- **Tech**: Redis.
- **Example**: If 10,000 people ask for `#abc123` at once, we just read from the cheat sheet instantly.

---

## 3. How We Create a Short URL (Step-by-Step)

1. **User sends Long URL**: `google.com`
2. **Backend needs a unique ID**:
   - We don't just use random numbers (collisions are bad!).
   - We use a **Snowflake ID Generator** (a fancy counter that works across many servers).
   - ID = `18273645`
3. **Convert ID to Letters**:
   - `18273645` looks ugly.
   - We convert it to Base62 (a-z, A-Z, 0-9).
   - Result: `xYz9`
4. **Save it**:
   - Database: `xYz9` = `google.com`
   - Cache: `xYz9` = `google.com`
5. **Return**: `http://localhost:8080/xYz9`

---

## 4. How We Redirect (Step-by-Step)

1. **User visits**: `http://localhost:8080/xYz9`
2. **Backend Checks Cache (Redis)**:
   - "Do we know `xYz9`?"
   - **Yes!** -> Return `google.com` immediately. (Super fast ⚡)
3. **If not in Cache**:
   - Check Database (Postgres).
   - "Ah, here it is."
   - Save to Cache for next time.
   - Return `google.com`.
4. **Browser Redirects**:
   - The user is sent to `google.com`.

---

That's it! It's just a very fast, distributed lookup table.
