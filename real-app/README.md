# Real App Implementations

This folder contains working code for the system design cases.

---

## The URL Shortener Explained

Imagine you are a **Librarian**.

People come to you with **really long book titles**, and you need to give them a **short ticket number** so they can find the book later.

### 1. Storing a Book (Creating a Short URL)
- **User**: "Here is *'The Lord of the Rings: The Fellowship of the Ring (Special Edition)'*."
- **You**: "Okay, take ticket `#abc123`."
- **You** write in your notebook: `abc123` = `The Lord of the Rings...`

### 2. Finding a Book (Redirecting)
- **User**: "I have ticket `#abc123`."
- **You**: Look at your notebook. "Ah! That means *'The Lord of the Rings...'*! Here you go."

---

## How We Built It (The Components)

1. **The Brain (Spring Boot)**
   - The librarian. It does the thinking.
   - It takes the long URL, generates a unique ID, and saves it.

2. **The Notebook (PostgreSQL)**
   - The permanent record.
   - Even if the librarian goes home (server restarts), the notebook remembers `abc123` = `google.com`.

3. **The Cheat Sheet (Redis)**
   - A sticky note on the librarian's desk.
   - If 10,000 people ask for `#abc123` at the same time, the librarian looks at the sticky note instead of opening the heavy notebook every time. **(This makes it fast!)**

4. **The Ticket Machine (Snowflake ID)**
   - To make sure two people never get the same ticket number, we use a special counter (Snowflake) that generates unique numbers like `18273645`, which we turn into `abc123`.

---

## Projects

| Project | Location | Status |
|---------|----------|--------|
| **URL Shortener** | [`./url-shortener`](./url-shortener) | Ready |
| Rate Limiter | *Coming soon* | Planned |
