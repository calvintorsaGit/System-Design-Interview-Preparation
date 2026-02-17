# 🎤 Step 9: The Interview Script

> A concise 5-minute summary to use in the interview.

---

## The Pitch

**1. Requirements**
"I'll build a highly available URL shortener service that handles ~100M writes/month and is read-heavy (10:1 ratio)."

**2. Estimation**
"That's about 40 writes/sec and 400 reads/sec. The real challenge is storage (1.5TB over 5 years) and fast lookups."

**3. Design**
"I'll use a **3-tier architecture**:
- **Load Balancer** to distribute traffic.
- **Stateless Application Servers** (Spring Boot) for logic.
- **Sharded PostgreSQL Database** for storage, with a **Redis Cache** in front for speed."

**4. Core Logic**
"To guarantee unique short codes, I'll use a **Snowflake ID generator** to create unique integers, then convert them to **Base62**. This avoids collisions entirely."

**5. Scaling**
"I'll shard the database by `shortCode` hash to distribute load evenly. I'll use **master-slave replication** for high availability."

---

*This concludes Case 1. Ready for the next one?*
